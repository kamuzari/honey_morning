package com.sf.honeymorning.alarm.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.sf.honeymorning.alarm.client.BriefingClient;
import com.sf.honeymorning.alarm.client.QuizGeneratorClient;
import com.sf.honeymorning.alarm.client.TopicModelingClient;
import com.sf.honeymorning.alarm.client.WakeUpCallSongClient;
import com.sf.honeymorning.alarm.dto.request.AlarmSetRequest;
import com.sf.honeymorning.alarm.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.exception.AlarmFatalException;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.alarm.repository.AlarmTagRepository;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.brief.repository.BriefingTagRepository;
import com.sf.honeymorning.brief.repository.TopicModelWordRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.quiz.repository.QuizRepository;
import com.sf.honeymorning.user.entity.User;
import com.sf.honeymorning.user.repository.UserRepository;
import com.sf.honeymorning.util.TtsUtil;

@Service
@Transactional(readOnly = true)
public class AlarmService {

	private static final Logger log = LoggerFactory.getLogger(AlarmService.class);

	private final BriefingClient briefingClient;
	private final TopicModelingClient topicModelingClient;
	private final WakeUpCallSongClient wakeUpCallSongClient;
	private final QuizGeneratorClient quizGeneratorClient;
	private final TopicModelWordRepository topicModelWordRepository;
	private final AlarmRepository alarmRepository;
	private final UserRepository userRepository;
	private final AlarmTagRepository alarmTagRepository;
	private final BriefingRepository briefingRepository;
	private final QuizRepository quizRepository;
	private final RestTemplate restTemplate = new RestTemplate();
	private final TtsUtil ttsUtil;
	private final int timeGap = 5;
	private final BriefingTagRepository briefingTagRepository;

	public AlarmService(BriefingClient briefingClient,
		TopicModelingClient topicModelingClient,
		WakeUpCallSongClient wakeUpCallSongClient,
		QuizGeneratorClient quizGeneratorClient,
		TopicModelWordRepository topicModelWordRepository,
		AlarmRepository alarmRepository,
		UserRepository userRepository,
		AlarmTagRepository alarmTagRepository,
		BriefingRepository briefingRepository,
		QuizRepository quizRepository,
		TtsUtil ttsUtil,
		BriefingTagRepository briefingTagRepository) {
		this.briefingClient = briefingClient;
		this.topicModelingClient = topicModelingClient;
		this.wakeUpCallSongClient = wakeUpCallSongClient;
		this.quizGeneratorClient = quizGeneratorClient;
		this.topicModelWordRepository = topicModelWordRepository;
		this.alarmRepository = alarmRepository;
		this.userRepository = userRepository;
		this.alarmTagRepository = alarmTagRepository;
		this.briefingRepository = briefingRepository;
		this.quizRepository = quizRepository;
		this.ttsUtil = ttsUtil;
		this.briefingTagRepository = briefingTagRepository;
	}

	public AlarmResponse getMyAlarm(Long userId) {
		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
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
	public void set(AlarmSetRequest alarmRequestDto, Long userId) {
		List<Alarm> all = alarmRepository.findAll();
		Optional<User> byId = userRepository.findById(userId);

		Alarm alarm = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
				, ErrorProtocol.POLICY_VIOLATION));

		alarm.set(alarmRequestDto.alarmTime(),
			alarmRequestDto.weekdays(),
			alarmRequestDto.repeatFrequency(),
			alarmRequestDto.repeatInterval(),
			alarmRequestDto.isActive());
	}

	public void getSleep(Long userId) {

		/**
		 *
		 * 알람 까지의 남은 시간이 5시간 미만이라면 예외를 던진다.
		 * 그것이 아니라면 200 신호를 반환한다.
		 *
		 */

		Alarm alarm = alarmRepository.findByUserId(userId)
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
