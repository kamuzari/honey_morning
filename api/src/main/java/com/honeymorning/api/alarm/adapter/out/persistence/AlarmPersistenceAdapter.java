package com.honeymorning.api.alarm.adapter.out.persistence;

import static com.honeymorning.common.exception.constant.ErrorProtocol.BUSINESS_VIOLATION;
import static com.honeymorning.common.exception.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Component;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmResponse;
import com.honeymorning.api.alarm.adapter.in.web.dto.response.PreparedAlarmContentResponse;
import com.honeymorning.api.alarm.adapter.in.web.port.out.AlarmQueryPort;
import com.honeymorning.api.alarm.adapter.out.persistence.mapper.AlarmPersistenceAdapterMapper;
import com.honeymorning.api.alarm.application.domain.UpdateAlarm;
import com.honeymorning.api.alarm.application.domain.VerifySleepModeAlarm;
import com.honeymorning.api.alarm.application.port.out.CommandAlarmPort;
import com.honeymorning.api.alarm.application.port.out.LoadAlarmPort;
import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.alarm.repository.AlarmRepository;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;
import com.honeymorning.common.domain.briefing.repository.BriefingRepository;
import com.honeymorning.common.domain.briefing.repository.QuizRepository;
import com.honeymorning.common.exception.BusinessException;
import com.honeymorning.common.exception.NotFoundResourceException;

@Component
public class AlarmPersistenceAdapter implements AlarmQueryPort, LoadAlarmPort, CommandAlarmPort {
	private final BriefingRepository briefingRepository;
	private final AlarmRepository alarmRepository;
	private final QuizRepository quizRepository;
	private final AlarmPersistenceAdapterMapper alarmPersistenceAdapterMapper;

	public AlarmPersistenceAdapter(
		BriefingRepository briefingRepository,
		AlarmRepository alarmRepository,
		QuizRepository quizRepository,
		AlarmPersistenceAdapterMapper alarmPersistenceAdapterMapper) {

		this.briefingRepository = briefingRepository;
		this.alarmRepository = alarmRepository;
		this.quizRepository = quizRepository;
		this.alarmPersistenceAdapterMapper = alarmPersistenceAdapterMapper;
	}

	public PreparedAlarmContentResponse getPreparedAlarmContents(Long userId) {
		AlarmEntity alarmEntity = alarmRepository.findByUserIdAndIsActiveTrue(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("존재하지 않는 사용자입니다. userId : {0}", userId),
				BUSINESS_VIOLATION
			));
		BriefingEntity briefingEntity = briefingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("알람 콘텐츠가 완성되지 않았어요. userId : {0}", userId),
				POLICY_VIOLATION
			));
		List<QuizEntity> quizEntities = quizRepository.findByBriefingEntity(briefingEntity);

		return alarmPersistenceAdapterMapper.toPreparedAlarmContentResponse(alarmEntity, briefingEntity, quizEntities);
	}

	public AlarmResponse getMyAlarmWithMyTags(Long userId) {
		AlarmEntity alarmEntity = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
				, POLICY_VIOLATION));

		return alarmPersistenceAdapterMapper.toAlarmResponse(alarmEntity);
	}

	@Override
	public UpdateAlarm getAlarm(Long userId) {
		AlarmEntity alarmEntity = alarmRepository.findByUserId(userId)
			.orElseThrow(() -> new NotFoundResourceException(
				format("알람이 반드시 존재했어야합니다. userId -> {0}", userId)
				, POLICY_VIOLATION));

		return alarmPersistenceAdapterMapper.toDomain(alarmEntity);
	}

	@Override
	public VerifySleepModeAlarm getActivatedAlarm(Long userId) {
		AlarmEntity alarmEntity = alarmRepository.findByUserIdAndIsActiveTrue(userId)
			.orElseThrow(() -> new NotFoundResourceException("알람이 활성화되지 않았습니다.", BUSINESS_VIOLATION));

		return alarmPersistenceAdapterMapper.toSleepModeDomain(alarmEntity);
	}

	@Override
	public void reflect(UpdateAlarm updateAlarm) {
		AlarmEntity alarmEntity = alarmRepository.findByUserId(updateAlarm.getUserId())
			.orElseThrow(() -> new NotFoundResourceException(
				format("알람이 존재하지 않습니다.. domain  -> {0}", updateAlarm.getUserId())
				, POLICY_VIOLATION));

		alarmEntity.update(
			updateAlarm.getWakeUpTime(),
			updateAlarm.getDayOfTheWeeks(),
			updateAlarm.getRepeatFrequency(),
			updateAlarm.getRepeatInterval(),
			updateAlarm.isActive()
		);
	}
}
