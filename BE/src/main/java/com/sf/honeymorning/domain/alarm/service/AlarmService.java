package com.sf.honeymorning.domain.alarm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.domain.alarm.dto.AlarmCategoryDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmRequestDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResponseDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmResultDto;
import com.sf.honeymorning.domain.alarm.dto.AlarmStartDto;
import com.sf.honeymorning.domain.alarm.dto.QuizDto;
import com.sf.honeymorning.domain.alarm.entity.Alarm;
import com.sf.honeymorning.domain.alarm.entity.AlarmCategory;
import com.sf.honeymorning.domain.alarm.entity.AlarmResult;
import com.sf.honeymorning.domain.alarm.repository.AlarmCategoryRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmRepository;
import com.sf.honeymorning.domain.alarm.repository.AlarmResultRepository;
import com.sf.honeymorning.domain.auth.service.AuthService;
import com.sf.honeymorning.domain.brief.entity.Brief;
import com.sf.honeymorning.domain.brief.repository.BriefRepository;
import com.sf.honeymorning.domain.quiz.entity.Quiz;
import com.sf.honeymorning.domain.quiz.repository.QuizRepository;
import com.sf.honeymorning.domain.tag.entity.Tag;
import com.sf.honeymorning.domain.tag.repository.TagRepository;
import com.sf.honeymorning.domain.user.entity.User;
import com.sf.honeymorning.domain.user.repository.UserRepository;
import com.sf.honeymorning.exception.alarm.AlarmFatalException;
import com.sf.honeymorning.exception.user.AlarmCategoryNotFoundException;
import com.sf.honeymorning.exception.user.UserNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final AlarmCategoryRepository alarmCategoryRepository;
    private final AlarmResultRepository alarmResultRepository;
    private final TagRepository tagRepository;
	private final RestTemplate restTemplate = new RestTemplate();
	private final BriefRepository briefRepository;
	private final QuizRepository quizRepository;


	public ResponseEntity<?> findAlarmByUsername() {
        User user = authService.getLoginUser();

        if (user == null) {
            return new ResponseEntity<>("현재 로그인된 유저 정보가 없습니다.", HttpStatus.UNAUTHORIZED);
        }

        Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());

        AlarmResponseDto alarmResponseDto = AlarmResponseDto.builder()
                .id(alarm.getId())
                .alarmTime(alarm.getAlarmTime())
                .daysOfWeek(alarm.getDaysOfWeek())
                .repeatFrequency(alarm.getRepeatFrequency())
                .repeatInterval(alarm.getRepeatInterval())
                .isActive(alarm.getIsActive())
                .build();

        return ResponseEntity.ok(alarmResponseDto);

    }

	@Transactional
	public ResponseEntity<?> updateAlarm(AlarmRequestDto alarmRequestDto) {

		/**
		 *설정한 시간 및 요일이 현재 시간에서 5시간 이상 차이가 나지 않으면 업데이트 거부
		 */

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

		// 설정한 알람 시각
		LocalTime alarmTime = alarmRequestDto.getAlarmTime();
		// 설정한 알람 요일
		String alarmWeek = alarmRequestDto.getDaysOfWeek();

		// 알람이 현재 요일만 설정 되어 있고, 이후 시간이며, 5시간 이전에 설정되어 있을 때.

		if (binary.equals(alarmWeek) && ChronoUnit.SECONDS.between(nowTime, alarmTime) > 0
			&& ChronoUnit.HOURS.between(nowTime, alarmTime) < 5) {
			throw new IllegalArgumentException("알람 시간이 현재 시간으로부터 5시간 이내여서 설정이 거부되었습니다.");
		}

		// 알람이 내일 요일만 설정 되어 있고, 5시간 이전에 설정되어 있을 때.
		if (nextBinary.equals(alarmWeek) && ChronoUnit.HOURS.between(nowTime, alarmTime) + 24 <= 5) {
			throw new IllegalArgumentException("알람 시간이 현재 시간으로부터 5시간 이내여서 설정이 거부되었습니다.");
		}

		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UserNotFoundException("해당 알람의 유저가 없습니다"));
		Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());

		alarm.setAlarmTime(alarmRequestDto.getAlarmTime());
		alarm.setDaysOfWeek(alarmRequestDto.getDaysOfWeek());
		alarm.setRepeatFrequency(alarmRequestDto.getRepeatFrequency());
		alarm.setRepeatInterval(alarmRequestDto.getRepeatInterval());
		alarm.setIsActive(alarmRequestDto.getIsActive());

		return ResponseEntity.ok("알람이 성공적으로 업데이트되었습니다.");
	}

	// 알람 카테고리 조회
	public ResponseEntity<?> findAlarmCategory() {

		User user = authService.getLoginUser();

		// 알람 조회
		Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());

		// 알람 카테고리 조회
		List<AlarmCategory> alarmCategoryList = alarmCategoryRepository.findAllByAlarmId(alarm.getId());
		if (alarmCategoryList.size() == 0) {
			throw new AlarmCategoryNotFoundException("해당 알람이 카테고리를 가지고 있지 않습니다.");
		}

		List<AlarmCategoryDto> alarmCategoryDtoList = new ArrayList<>();

		for (AlarmCategory alarmCategory : alarmCategoryList) {
			AlarmCategoryDto alarmCategoryDto = AlarmCategoryDto.builder()
				.alarmCategoryId(alarmCategory.getId())
				.alarmId(alarmCategory.getAlarm().getId())
				.tagId(alarmCategory.getTag().getId())
				.word(alarmCategory.getTag().getWord())
				.build();
			alarmCategoryDtoList.add(alarmCategoryDto);
		}
		return ResponseEntity.ok(alarmCategoryDtoList);

	}

	// 알람 카테고리 추가
	public ResponseEntity<?> addAlarmCategory(String word) {

		Tag tag = tagRepository.findTagByWord(word);

		// 해당 단어에 대한 tag 데이터가 존재하지 않다면 tag 데이터를 추가한다.
		if (tag == null) {
			int customNum = 1;

			String[] wordList = {"정치", "경제", "사회", "생활/문화", "IT/과학", "세계", "연예", "스포츠"};

			// 기본 태그에 해당되는 단어라면 custom 되지 않았음을 명시해준다.
			for (String w : wordList) {
				if (w.equals(word)) {
					customNum = 0;
					break;
				}
			}

			Tag tempTag = Tag.builder()
				.word(word)
				.isCustom(customNum)
				.build();

			tag = tagRepository.save(tempTag);
		}

		// 똑같은 알람 카테고리를 추가했는지 확인.
		AlarmCategory alarmCategory = alarmCategoryRepository.findByTagId(tag.getId());
		if (alarmCategory != null) {
			return new ResponseEntity<>("이미 같은 알람 카테고리를 유저가 갖고 있습니다.", HttpStatus.CONFLICT);
		}

		// alarmCategory 추가.
		User user = authService.getLoginUser();
		Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());
		alarmCategoryRepository.save(new AlarmCategory(alarm, tag));

		return ResponseEntity.ok("alarm category successfully saved");
	}

	// 알람 카테고리 삭제
	public void deleteAlarmCategory(String word) {
		// tag는 공유되는 것이기 때문에 alarmCategory만 삭제한다.
		User user = authService.getLoginUser();
		Tag tag = tagRepository.findTagByWord(word);
		alarmCategoryRepository.deleteByAlarmIdAndTagIds(user.getId(), tag.getId());
	}


	// 알람 결과 조회
	public ResponseEntity<?> findAlarmResult() {
		User user = authService.getLoginUser();

		List<AlarmResult> alarmResultList = alarmResultRepository.findAllByUserId(user.getId());


		List<AlarmResultDto> alarmResultDtoList = new ArrayList<>();
		for (AlarmResult alarmResult : alarmResultList) {
			AlarmResultDto alarmResultDto = AlarmResultDto.builder()
				.count(alarmResult.getCount())
				.isAttending(alarmResult.getIsAttended())
				.build();
			alarmResultDtoList.add(alarmResultDto);
		}
		return ResponseEntity.ok(alarmResultDtoList);
	}

	// 알람 결과 추가
	public ResponseEntity<?> saveAlarmResult(AlarmResultDto alarmResultDto) {
		User user = authService.getLoginUser();

		AlarmResult alarmResult = AlarmResult.builder()
			.user(user)
			.count(alarmResultDto.getCount())
			.isAttended(alarmResultDto.getIsAttending())
			.build();

		alarmResultRepository.save(alarmResult);

		return ResponseEntity.ok("알람 결과가 성공적으로 추가되었습니다.");
	}

	public ResponseEntity<?> getStreak() {
		User user = authService.getLoginUser();

		// 유저의 모든 알람_결과 테이블 데이터를 가져온다.
		List<AlarmResult> alarmResultList = alarmResultRepository.findAllByUserId(user.getId());

		int streak = 0;

		// 최신 알람 결과 데이터를 조회하여 연속 출석일을 계산한다.
		for (int i = alarmResultList.size() - 1; i >= 0; i--) {
			AlarmResult alarmResult = alarmResultList.get(i);

			if (alarmResult.getIsAttended() == 1 && alarmResult.getCount() >= 1) {
				streak++;
			} else {
				break;
			}
		}

		return ResponseEntity.ok(streak);

	}

	@Transactional
	@Scheduled(fixedRate = 60000)
	public void readyBrief() {
		HashMap<String, Integer> categoryMap = new HashMap<>();
		int[] categories = new int[] {100, 101, 102, 103, 104, 105, 106, 107};
		String[] categoryNames = new String[] {"정치", "경제", "사회", "생활/문화", "IT/과학", "세계", "연예", "스포츠"};
		for (int i = 0; i < categories.length; i++) {
			categoryMap.put(categoryNames[i], categories[i]);
		}
		List<Alarm> alarms = alarmRepository.findByAlarmTime(LocalTime.now().minusMinutes(10));
		for (int j = 0; j < alarms.size(); j++) {
			Alarm alarm = alarms.get(j);
			User user = alarm.getUser();
			List<AlarmCategory> alarmCategoryList = alarmCategoryRepository.findAllByAlarmId(alarm.getId());
			List<Integer> tagIds = new ArrayList<>();
			for (int i = 0; i < alarmCategoryList.size(); i++) {
				AlarmCategory alarmCategory = alarmCategoryList.get(i);
				if (categoryMap.containsKey(alarmCategory.getTag().getWord())) {
					Integer tagId = categoryMap.get(alarmCategory.getTag().getWord());
					tagIds.add(tagId);
				}
			}
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(List.of(MediaType.APPLICATION_JSON));
			Map<String, Object> body = new HashMap<>();
			body.put("tags", tagIds);
			HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
			try {
				ResponseEntity<String> briefResponse = restTemplate.exchange(
					"https://www.honeymorning.store/ai/briefing/", HttpMethod.POST, requestEntity,
					String.class);
				if (briefResponse.getStatusCode().is2xxSuccessful()) {
					String data = briefResponse.getBody();
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode jsonNode = objectMapper.readTree(data);
					String shortBriefing = jsonNode.get("data").get("shortBriefing").asText();
					String longBriefing = jsonNode.get("data").get("longBriefing").asText();
					System.out.println("Short Briefing: " + shortBriefing);
					System.out.println("Long Briefing: " + longBriefing);

					Brief save = briefRepository.save(
						Brief.builder()
							.user(user)
							.summary(shortBriefing)
							.content(longBriefing)
							.build()
					);
					ResponseEntity<String> topicModelResponse = restTemplate.exchange(
						"https://www.honeymorning.store/ai/topic/", HttpMethod.POST, requestEntity, String.class);

					if (topicModelResponse.getStatusCode().is2xxSuccessful()) {
						data = briefResponse.getBody();
						objectMapper = new ObjectMapper();
						jsonNode = objectMapper.readTree(data);
						String url = jsonNode.get("url").asText();
						alarm.setMusicFilePath(url);
						alarmRepository.save(alarm);
					} else {
						System.out.println("POST 요청 실패: " + briefResponse.getStatusCode());
					}

					headers = new HttpHeaders();
					headers.setContentType(MediaType.APPLICATION_JSON);
					headers.setAccept(List.of(MediaType.APPLICATION_JSON));
					body = new HashMap<>();
					body.put("briefing", save.getSummary());
					requestEntity = new HttpEntity<>(body, headers);
					ResponseEntity<String> songResponse = restTemplate.exchange(
						"https://www.honeymorning.store/ai/music/music/", HttpMethod.POST, requestEntity, String.class);
					if (songResponse.getStatusCode().is2xxSuccessful()) {
						data = songResponse.getBody();
						objectMapper = new ObjectMapper();
						jsonNode = objectMapper.readTree(data);
						String url = jsonNode.get("url").asText();
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
						"https://www.honeymorning.store/ai/quiz/", HttpMethod.POST, requestEntity, String.class);
					if (quizResponse.getStatusCode().is2xxSuccessful()) {

						String body1 = quizResponse.getBody();
						objectMapper = new ObjectMapper();
						JsonNode rootNode = objectMapper.readTree(body1);
						JsonNode dataArray = rootNode.get("data");

						for (JsonNode quiz : dataArray) {
							String problem = quiz.get("problem").asText();
							System.out.println("문제: " + problem);
							Quiz savings = new Quiz();
							savings.setQuestion(problem);
							JsonNode choices = quiz.get("choices");
							List<String> items = new ArrayList<>();
							for (JsonNode choice : choices) {
								int id = choice.get("id").asInt();
								String item = choice.get("item").asText();
								System.out.println("선택지 " + id + ": " + item);
								items.add(item);
							}
							savings.setOption1(items.get(0));
							savings.setOption2(items.get(1));
							savings.setOption3(items.get(2));
							savings.setOption4(items.get(3));
							int answer = quiz.get("answer").asInt();
							savings.setAnswer(answer);
							System.out.println("정답: " + answer);
							System.out.println("---------------------------");
							quizRepository.save(savings);
						}
					} else {
						System.out.println("POST 요청 실패: " + briefResponse.getStatusCode());
					}
				} else {
					System.out.println("POST 요청 실패: " + briefResponse.getStatusCode());
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
		Alarm alarm = alarmRepository.findAlarmsByUserId(user.getId());
		brief.getSummary();
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
				quiz.getOption4()
			));
		}
		alarm.getMusicFilePath();
		ArrayList<QuizDto> list = new ArrayList<>();
		list.add(new QuizDto(
			1L,
			"한국토지자원관리공단의 목적은 무엇인가요?",
			1,
			"지역경제",
			"국제무역",
			"외환관리",
			"관세제도"
		));
		list.add(new QuizDto(
			2L,
			"한국토지자원관리공단은 어떤 방법으로 경제활성화를 도모하고 있나요?",
			1,
			"지분참여",
			"장기대출",
			"세금감면",
			"임대사업"
		));
		return new AlarmStartDto(
			"https://cdn1.suno.ai/a3aa4a9c-5f27-445b-af00-2babbd3bc924.mp3",
			list,
			"한국토지자원관리공단은 한국토지공사의 지분참여를 통해 지역경제활성화를 이룰 수 있는 방안을 모색하기 위해 노력중이다. "
				+ "\n 한국토지자원관리공단은 한국토지공사의 지분참여를 통해 지역경제활성화를 이룰 수 있는 방안을 모색하기 위해 노력중이다.  "
		);
	}

}
