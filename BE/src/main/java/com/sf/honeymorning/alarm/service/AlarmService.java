package com.sf.honeymorning.alarm.service;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.sf.honeymorning.alarm.client.BriefingClient;
import com.sf.honeymorning.alarm.client.QuizGeneratorClient;
import com.sf.honeymorning.alarm.client.TopicModelingClient;
import com.sf.honeymorning.alarm.client.WakeUpCallSongClient;
import com.sf.honeymorning.alarm.client.dto.QuizOption;
import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.entity.AlarmTag;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.alarm.repository.AlarmTagRepository;
import com.sf.honeymorning.authentication.service.AuthService;
import com.sf.honeymorning.brief.entity.Brief;
import com.sf.honeymorning.brief.entity.BriefCategory;
import com.sf.honeymorning.brief.entity.TopicModel;
import com.sf.honeymorning.brief.entity.TopicModelWord;
import com.sf.honeymorning.brief.entity.Word;
import com.sf.honeymorning.brief.repository.BriefCategoryRepository;
import com.sf.honeymorning.brief.repository.BriefRepository;
import com.sf.honeymorning.brief.repository.TopicModelRepository;
import com.sf.honeymorning.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.brief.repository.WordRepository;
import com.sf.honeymorning.domain.alarm.dto.AlarmStartDto;
import com.sf.honeymorning.domain.alarm.dto.QuizDto;
import com.sf.honeymorning.exception.alarm.AlarmFatalException;
import com.sf.honeymorning.exception.model.BusinessException;
import com.sf.honeymorning.exception.model.ErrorProtocol;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.repository.UserRepository;
import com.sf.honeymorning.util.TtsUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlarmService {

	private static final Logger log = LoggerFactory.getLogger(AlarmService.class);

	private final BriefingClient briefingClient;
	private final TopicModelingClient topicModelingClient;
	private final WakeUpCallSongClient wakeUpCallSongClient;
	private final QuizGeneratorClient quizGeneratorClient;

	private final ReadyAlarmService readyAlarmService;

	private final TopicModelRepository topicModelRepository;
	private final TopicModelWordRepository topicModelWordRepository;
	private final WordRepository wordRepository;
	private final AlarmRepository alarmRepository;
	private final UserRepository userRepository;
	private final AuthService authService;
	private final AlarmTagRepository alarmTagRepository;
	private final BriefRepository briefRepository;
	private final QuizRepository quizRepository;
	private final RestTemplate restTemplate = new RestTemplate();
	private final TtsUtil ttsUtil;
	private final int timeGap = 5;
	private final BriefCategoryRepository briefCategoryRepository;

	public AlarmResponse getMyAlarm(String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException("not found Resources. username : {}" + username));
		Alarm alarm = alarmRepository.findByUserId(user.getId())
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("알람이 반드시 존재했어야합니다. userId -> {0}", user.getId())
				, ErrorProtocol.POLICY_VIOLATION));

		return new AlarmResponse(
			alarm.getId(),
			alarm.getWakeUpTime(),
			alarm.getDayOfWeek(),
			alarm.getRepeatFrequency(),
			alarm.getRepeatInterval(),
			alarm.isActive()
		);
	}

	@Transactional
	public void set(AlarmSetRequest alarmRequestDto, String username) {
		User user = userRepository.findByUsername(username)
			.orElseThrow(() -> new EntityNotFoundException("not found Resources. username : {}" + username));
		Alarm alarm = alarmRepository.findByUserId(user.getId())
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("알람이 반드시 존재했어야합니다. userId -> {0}", user.getId())
				, ErrorProtocol.POLICY_VIOLATION));

		alarm.set(alarmRequestDto.alarmTime(),
			alarmRequestDto.weekdays(),
			alarmRequestDto.repeatFrequency(),
			alarmRequestDto.repeatInterval(),
			alarmRequestDto.isActive());
	}

	@Transactional
	@Scheduled(fixedRate = 60000)
	public void readyBriefing() {
		readyAlarmService.getReadyAlarm()
			.forEach(alarm -> {
				// todo : 브리핑 AI 서버에게 요청, 토픽 모델링 AI 서버에게 요청 with feign
				List<AlarmTag> alarmWithTag = alarmTagRepository.findByAlarmWithTag(alarm);
				List<String> tags = alarmWithTag.stream().map(AlarmTag::getTag)
					.map(tag -> tag.getWord())
					.toList();

				var briefingResponse = briefingClient.send(tags);
				var wakeUpCallSongResponse = wakeUpCallSongClient.send(briefingResponse.voiceContent());
				var quizResponseDtos = quizGeneratorClient.send(briefingResponse.readContent());

				// voiceContent 에 대한 음성 url 만들기
				String voiceContentUrl = null;
				try {
					voiceContentUrl = ttsUtil.textToSpeech(briefingResponse.voiceContent(), "summary");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				Brief savedBrief = briefRepository.save(new Brief(alarm.getUserId(),
					briefingResponse.voiceContent(),
					briefingResponse.readContent(),
					voiceContentUrl
				));

				alarmWithTag.stream()
					.forEach(
						alarmTag -> briefCategoryRepository.save(new BriefCategory(savedBrief, alarmTag.getTag())));
				alarm.addMusicFilePath(wakeUpCallSongResponse.url());
				List<Quiz> quiz = quizResponseDtos.stream().map(quizResponseDto -> {
					String quizVoiceUrl = null;
					try {
						quizVoiceUrl = ttsUtil.textToSpeech(quizResponseDto.problem(), "quiz");
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

					List<String> quizOption = quizResponseDto.quizOptions().stream()
						.sorted(Comparator.comparingInt(QuizOption::order))
						.map(QuizOption::content)
						.collect(Collectors.toList());

					return new Quiz(
						savedBrief,
						quizResponseDto.problem(),
						quizResponseDto.answer(),
						quizOption,
						quizVoiceUrl
					);
				}).toList();
				quizRepository.saveAll(quiz);

				var topicModelingResponse = topicModelingClient.send(tags);
				topicModelingResponse.sections().entrySet()
					.forEach(entry -> {
						Long sectionId = entry.getKey();
						TopicModel savedTopicModel = topicModelRepository.save(new TopicModel(savedBrief, sectionId));

						entry.getValue()
							.stream()
							.map(detail -> {
								Word savedWord = wordRepository.save(new Word(detail.word()));
								return new TopicModelWord(savedTopicModel, savedWord, detail.percentage());
							})
							.forEach(topicModelWord -> topicModelWordRepository.save(topicModelWord));
					});

			});

	}

	public AlarmStartDto getThings() {
		User user = authService.getLoginUser();
		LocalDate today = LocalDate.now();
		LocalDateTime startOfDay = today.atStartOfDay();
		LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();  // Midnight of next day
		Brief brief = briefRepository.findByUserAndCreatedAtToday(user.getId(), startOfDay, endOfDay)
			.orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));
		List<Quiz> quizzes = quizRepository.findByBrief(brief)
			.orElseThrow(() -> new AlarmFatalException("알람 준비가 안됬어요. 큰일이에요. ㅠ"));
		Alarm alarm = alarmRepository.findByUserId(user.getId())
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
				quiz.getQuizVoiceUrl()
			));
		}
		return new AlarmStartDto(
			alarm.getMusicFilePath(),
			quizDtos,
			brief.getId(),
			brief.getSummary(),
			brief.getVoiceContentUrl()
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
		Alarm alarm = alarmRepository.findByUserId(user.getId())
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

		int alarmWeek = alarm.getDayOfWeek();
		LocalTime alarmTime = alarm.getWakeUpTime();

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
