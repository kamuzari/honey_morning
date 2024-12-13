package com.sf.honeymorning.alarm.service;

import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sf.honeymorning.alarm.controller.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.entity.Alarm;
import com.sf.honeymorning.alarm.entity.DayOfWeek;
import com.sf.honeymorning.alarm.exception.NotPreparedAlarmException;
import com.sf.honeymorning.alarm.repository.AlarmRepository;
import com.sf.honeymorning.alarm.service.mapper.PreparedAlarmMapper;
import com.sf.honeymorning.brief.entity.Briefing;
import com.sf.honeymorning.brief.repository.BriefingRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.quiz.entity.Quiz;
import com.sf.honeymorning.quiz.repository.QuizRepository;
import com.sf.honeymorning.util.TimeUtils;

@Transactional(readOnly = true)
@Service
public class PreparedAlarmContentService {
	public static final int FIXED_NEXT_MINUTE = 40;

	private final BriefingRepository briefingRepository;
	private final AlarmRepository alarmRepository;
	private final QuizRepository quizRepository;
	private final PreparedAlarmMapper preparedAlarmMapper;

	public PreparedAlarmContentService(BriefingRepository briefingRepository, AlarmRepository alarmRepository,
		QuizRepository quizRepository, PreparedAlarmMapper preparedAlarmMapper) {
		this.briefingRepository = briefingRepository;
		this.alarmRepository = alarmRepository;
		this.quizRepository = quizRepository;
		this.preparedAlarmMapper = preparedAlarmMapper;
	}

	public PreparedAlarmContentResponse getPreparedAlarmContents(Long userId) {
		Alarm alarm = alarmRepository.findByUserIdAndIsActiveTrue(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("존재하지 않는 사용자입니다. userId : {0}", userId),
				ErrorProtocol.BUSINESS_VIOLATION
			));
		Briefing briefing = briefingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
			.orElseThrow(() -> new NotPreparedAlarmException(
				MessageFormat.format("알람 콘텐츠가 완성되지 않았어요. userId : {0}", userId),
				ErrorProtocol.POLICY_VIOLATION
			));
		List<Quiz> quizzes = quizRepository.findByBriefing(briefing);

		return preparedAlarmMapper.toPreparedAlarmContentResponse(alarm, briefing, quizzes);
	}

	public List<Alarm> getReadyAlarm() {
		LocalTime timeAfter40Minutes = TimeUtils.getNow().plusMinutes(FIXED_NEXT_MINUTE);
		Integer dayOfWeekMask = DayOfWeek.getToday();

		return alarmRepository.findActiveAlarmsForToday(dayOfWeekMask, timeAfter40Minutes);
	}

	@Transactional
	public void bulkSave(Briefing briefing) {
		briefingRepository.save(briefing);
	}

}
