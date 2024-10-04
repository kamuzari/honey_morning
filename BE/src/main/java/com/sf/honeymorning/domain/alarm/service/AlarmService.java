package com.sf.honeymorning.domain.alarm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.domain.alarm.dto.*;
import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import com.sf.honeymorning.domain.alarm.repository.AlarmCategoryRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.brief.dto.response.TopicAiResponseDto;
import com.sf.honeymorning.domain.brief.dto.response.TopicResponse;
import com.sf.honeymorning.domain.brief.dto.response.TopicWord;
import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.entity.TopicModel;
import com.sf.honeymorning.domain.brief.entity.TopicModelWord;
import com.sf.honeymorning.domain.brief.entity.Word;
import com.sf.honeymorning.domain.brief.repository.BriefRepository;
import com.sf.honeymorning.domain.brief.repository.TopicModelRepository;
import com.sf.honeymorning.domain.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.domain.brief.repository.WordRepository;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import com.sf.honeymorning.domain.quiz.repository.QuizRepository;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import com.sf.honeymorning.exception.alarm.AlarmFatalException;
import com.sf.honeymorning.util.TtsUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AlarmService {

    private static final Logger log = LoggerFactory.getLogger(AlarmService.class);
    @Value("${ai.url.briefing}")
    private String briefingAi;
    @Value("${ai.url.quiz}")
    private String quizAi;
    @Value("${ai.url.music}")
    private String musicAi;
    @Value("${ai.url.topic-model}")
    private String topicModelAi;

    private final TopicModelRepository topicModelRepository;
    private final TopicModelWordRepository topicModelWordRepository;
    private final WordRepository wordRepository;
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final AlarmCategoryRepository alarmCategoryRepository;
    private final BriefRepository briefRepository;
    private final QuizRepository quizRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final TtsUtil ttsUtil;
    private final int timeGap = 5;

    public AlarmResponseDto findAlarmByUsername() {
        User user = authService.getLoginUser();

        Alarm alarm = alarmRepository.findByUser(user)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));

        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                .id(alarm.getId())
                .alarmTime(alarm.getAlarmTime())
                .daysOfWeek(alarm.getDaysOfWeek())
                .repeatFrequency(alarm.getRepeatFrequency())
                .repeatInterval(alarm.getRepeatInterval())
                .isActive(alarm.getIsActive())
                .build();

        return alarmResponseDto;

    }

    public AlarmDateDto updateAlarm(AlarmRequestDto alarmRequestDto) {

        User user = authService.getLoginUser();
        Alarm alarm = alarmRepository.findByUser(user)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));

        alarm.setAlarmTime(alarmRequestDto.getAlarmTime());
        alarm.setDaysOfWeek(alarmRequestDto.getDaysOfWeek());
        alarm.setRepeatFrequency(alarmRequestDto.getRepeatFrequency());
        alarm.setRepeatInterval(alarmRequestDto.getRepeatInterval());
        alarm.setIsActive(alarmRequestDto.getIsActive());

        // 현재 시간
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalTime nowTime = LocalTime.now();
        int currentDayOfWeek = nowDateTime.getDayOfWeek().getValue() - 1; // 현재 요일 (0 ~ 6)

        // 오늘 날짜에 해당하는 요일부터 차례로 list에 들어간다.
        List<Integer> alarmDayOfWeekList = new ArrayList<>();

        // 설정한 알람 시각
        LocalTime alarmTime = alarmRequestDto.getAlarmTime();
        // 설정한 알람 요일
        String alarmWeek = alarmRequestDto.getDaysOfWeek();

        for (int i = 0; i < alarmWeek.length(); i++) {
            if (alarmWeek.substring((i + currentDayOfWeek) % 7, (i + currentDayOfWeek) % 7 + 1)
                    .equals("1")) {
                alarmDayOfWeekList.add((i + currentDayOfWeek));
            }
        }

        String binary = "";

        for (int i = 0; i < 7; i++) {
            if (i == currentDayOfWeek) {
                binary += "1";
            } else {
                binary += "0";
            }
        }

        // 알람 설정 요일 중, 당일의 요일 또한 설정이 되어있으며,
        if (alarmWeek.substring(currentDayOfWeek, currentDayOfWeek + 1).equals("1")) {
            // 알람 설정 시간이 현재보다 이후라면,
            if (ChronoUnit.SECONDS.between(nowTime, alarmTime) > 0) {
                Duration duration = Duration.between(nowTime, alarmTime);

                AlarmDateDto alarmDateDto = AlarmDateDto.builder()
                        .alarmDate(nowDateTime.plus(duration).truncatedTo(ChronoUnit.MINUTES))
                        .day(0L)
                        .hour(duration.toHours())
                        .minuet(duration.toMinutes())
                        .dayOfWeek(binary)
                        .build();

                return alarmDateDto;

            }
        }

        for (int i = 0; i < alarmDayOfWeekList.size(); i++) {
            if (i == 0 && currentDayOfWeek == alarmDayOfWeekList.get(i)) {
                continue;
            }

            int dow = alarmDayOfWeekList.get(i % alarmDayOfWeekList.size());

            Duration duration = Duration.between(nowTime, alarmTime);

            LocalDateTime alarmDateTime = nowDateTime.plusDays(dow - currentDayOfWeek)
                    .plus(duration);

            Duration duration1 = Duration.between(nowDateTime, alarmDateTime);

            long totalMinutes = duration1.toMinutes();
            long days = totalMinutes / (24 * 60);
            long hours = (totalMinutes % (24 * 60)) / 60;
            long minutes = totalMinutes % 60;

            AlarmDateDto alarmDateDto = AlarmDateDto.builder()
                    .alarmDate(nowDateTime.plusDays(dow - currentDayOfWeek).plus(duration).truncatedTo(ChronoUnit.MINUTES))
                    .day(days)
                    .hour(hours)
                    .minuet(minutes)
                    .dayOfWeek(binary)
                    .build();

            return alarmDateDto;
        }

        throw new IllegalArgumentException("알람 시간 설정에 실패하였습니다.");

    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void readyBriefing() {
        log.warn("=============================== ready Briefing ===============================");

        LocalTime start = LocalTime.now().plusMinutes(40).withSecond(0).withNano(0);
        LocalTime end = LocalTime.now().plusMinutes(40).withSecond(59).withNano(0);

        List<Alarm> alarms = alarmRepository.findAlarmsWithinTimeRange(start, end, 1);
        log.warn("alarms: {}, start --- > {} , end --- > {}", alarms, start, end);

        for (int j = 0; j < alarms.size(); j++) {
            Alarm alarm = alarms.get(j);
            User user = alarm.getUser();
            List<AlarmCategory> alarmCategoryList = alarmCategoryRepository.findByAlarm(alarm);
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < alarmCategoryList.size(); i++) {
                AlarmCategory alarmCategory = alarmCategoryList.get(i);
                Tag tag = alarmCategory.getTag();
                if (tag.getIsCustom() == 1) {
                    continue;
                }

                tags.add(alarmCategory.getTag().getWord());
            }
            log.warn("tags params : {}", tags);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            Map<String, Object> body = new HashMap<>();
            body.put("tags", tags);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            try {
                ResponseEntity<String> briefResponse = restTemplate.exchange(briefingAi,
                        HttpMethod.POST, requestEntity,
                        String.class);
                ResponseEntity<String> topicModelResponse = restTemplate.exchange(topicModelAi,
                        HttpMethod.POST,
                        requestEntity, String.class);
                if (briefResponse.getStatusCode().is2xxSuccessful()
                        && topicModelResponse.getStatusCode()
                        .is2xxSuccessful()) {
                    log.info(
                            "@@@@@@@@@@@@@@@@@@@@briefResponse , topicModelResponse success@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                    String data = briefResponse.getBody();
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(data);
                    String shortBriefing = jsonNode.get("data").get("shortBriefing").asText();
                    String longBriefing = jsonNode.get("data").get("longBriefing").asText();
                    System.out.println("Short Briefing: " + shortBriefing);
                    System.out.println("Long Briefing: " + longBriefing);
                    String summaryPath = ttsUtil.textToSpeech(shortBriefing, "summary");
                    String contentPath = ttsUtil.textToSpeech(longBriefing, "content");
                    Brief save = briefRepository.save(
                            Brief.builder()
                                    .user(user)
                                    .summary(shortBriefing)
                                    .content(longBriefing)
                                    .summaryFilePath(summaryPath)
                                    .contentFilePath(contentPath)
                                    .build()
                    );

                    data = topicModelResponse.getBody();
                    objectMapper = new ObjectMapper();
                    TopicResponse topicResponse = objectMapper.readValue(data, TopicResponse.class);
                    for (TopicAiResponseDto responseDto : topicResponse.getData()) {
                        TopicModel topicModel = topicModelRepository.save(TopicModel.builder()
                                .topicId((long) responseDto.getTopic_id())
                                .brief(save)
                                .build());
                        for (TopicWord topicWord : responseDto.getTopic_words()) {
                            Word word = wordRepository.save(
                                    Word.builder()
                                            .word(topicWord.getWord())
                                            .build()
                            );

                            topicModelWordRepository.save(
                                    TopicModelWord.builder()
                                            .word(word)
                                            .weight(topicWord.getWeight())
                                            .topicModel(topicModel)
                                            .build()
                            );
                        }
                    }

                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                    body = new HashMap<>();
                    body.put("briefing", save.getSummary());
                    requestEntity = new HttpEntity<>(body, headers);
                    ResponseEntity<String> songResponse = restTemplate.exchange(musicAi,
                            HttpMethod.POST, requestEntity,
                            String.class);

                    if (songResponse.getStatusCode().is2xxSuccessful()) {
                        data = songResponse.getBody();
                        objectMapper = new ObjectMapper();
                        jsonNode = objectMapper.readTree(data);
                        String url = jsonNode.get("url").asText();
                        alarm.setMusicFilePath(url);
                        System.out.println("url: " + url);

                    } else {
                        System.out.println("POST 요청 실패: " + briefResponse.getStatusCode());
                    }

                    headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                    body = new HashMap<>();
                    body.put("text", save.getSummary());
                    requestEntity = new HttpEntity<>(body, headers);
                    ResponseEntity<String> quizResponse = restTemplate.exchange(
                            quizAi, HttpMethod.POST, requestEntity, String.class);
                    if (quizResponse.getStatusCode().is2xxSuccessful()) {
                        String body1 = quizResponse.getBody();
                        objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(body1);
                        JsonNode dataArray = rootNode.get("data");
                        for (JsonNode jNode : dataArray) {
                            String problem = jNode.get("problem").asText();
                            System.out.println("문제: " + problem);
                            Quiz quiz = new Quiz();
                            quiz.setQuestion(problem);
                            JsonNode choices = jNode.get("choices");
                            List<String> items = new ArrayList<>();
                            for (JsonNode choice : choices) {
                                int id = choice.get("id").asInt();
                                String item = choice.get("item").asText();
                                System.out.println("선택지 " + id + ": " + item);
                                items.add(item);
                            }
                            quiz.setOption1(items.get(0));
                            quiz.setOption2(items.get(1));
                            quiz.setOption3(items.get(2));
                            quiz.setOption4(items.get(3));
                            int answer = jNode.get("answer").asInt();
                            quiz.setAnswer(answer);
                            quiz.setBrief(save);
                            System.out.println("정답: " + answer);
                            System.out.println("---------------------------");
                            String quizPath = ttsUtil.textToSpeech(quiz.getQuestion(), "quiz");
                            quiz.setQuizFilePath(quizPath);
                            Quiz saveQuiz = quizRepository.save(quiz);
                        }
                    } else {
                        System.out.println("POST 요청 실패: " + briefResponse.getStatusCode());
                    }
                } else {
                    System.out.println("POST 요청 실패: " + briefResponse.getStatusCode());
                    System.out.println("POST 요청 실패: " + topicModelResponse.getStatusCode());
                }
            } catch (Exception e) {
                System.out.println("에러 발생: " + e.getMessage());
            }
        }
    }

    public AlarmStartDto getThings() {
        User user = authService.getLoginUser();
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();  // Midnight of next day
        Brief brief = briefRepository.findByUserAndCreatedAtToday(user, startOfDay, endOfDay)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));
        List<Quiz> quizzes = quizRepository.findByBrief(brief)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));
        Alarm alarm = alarmRepository.findByUser(user)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));
        List<QuizDto> quizDtos = new ArrayList<>();
        for (int i = 0; i < quizzes.size(); i++) {
            Quiz quiz = quizzes.get(i);
            quizDtos.add(new QuizDto(
                    quiz.getId(),
                    quiz.getQuestion(),
                    quiz.getAnswer(),
                    quiz.getOption1(),
                    quiz.getOption2(),
                    quiz.getOption3(),
                    quiz.getOption4(),
                    quiz.getQuizFilePath()
            ));
        }
        return new AlarmStartDto(
                alarm.getMusicFilePath(),
                quizDtos,
                brief.getId(),
                brief.getSummary(),
                brief.getSummaryFilePath()
        );
    }

    public void getSleep() {

        /**
         *
         * 알람 까지의 남은 시간이 5시간 미만이라면 예외를 던진다.
         * 그것이 아니라면 200 신호를 반환한다.
         *
         */

        User user = authService.getLoginUser();
        Alarm alarm = alarmRepository.findByUser(user)
                .orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));
        ;

        // 현재 시간
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalTime nowTime = LocalTime.now();
        int currentDayOfWeek = nowDateTime.getDayOfWeek().getValue() - 1; // 현재 요일 (0 ~ 6)

        String binary = "";
        String nextBinary = "";

        for (int i = 0; i < 7; i++) {
            if (i == currentDayOfWeek) {
                binary += "1";
            } else {
                binary += "0";
            }
        }

        for (int i = 0; i < 7; i++) {
            if (i == (currentDayOfWeek + 1) % 7) {
                nextBinary += "1";
            } else {
                nextBinary += "0";
            }
        }

        int alarmWeek = Integer.parseInt(alarm.getDaysOfWeek());
        LocalTime alarmTime = alarm.getAlarmTime();

        // 알람이 요일만 설정 되어 있고, 이후 시간이며, 5시간 이전에 설정되어 있을 때.
        // equal이 아닌 &연산을 통해서 비교할 것.
        if ((Integer.parseInt(binary) & alarmWeek) > 0
                && ChronoUnit.SECONDS.between(nowTime, alarmTime) > 0
                && ChronoUnit.HOURS.between(nowTime, alarmTime) < timeGap) {
            throw new IllegalArgumentException("알람 시간이 현재 시간으로부터 5시간 이내여서 수면 시작이 거부되었습니다.");
        }
        //
        // 알람이 내일 요일만 설정 되어 있고, 5시간 이전에 설정되어 있을 때.
        if ((Integer.parseInt(nextBinary) & alarmWeek) > 0
                && ChronoUnit.HOURS.between(nowTime, alarmTime) + 24 < timeGap) {
            throw new IllegalArgumentException("알람 시간이 현재 시간으로부터 5시간 이내여서 수면 시작이 거부되었습니다.");
        }

    }

}
