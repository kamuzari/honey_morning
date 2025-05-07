package com.sf.honeymorning.alarm.adapter.out.persistence;

import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.BUSINESS_VIOLATION;
import static com.sf.honeymorning.common.exception.model.constant.ErrorProtocol.POLICY_VIOLATION;
import static java.text.MessageFormat.format;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.alarm.adapter.in.web.dto.response.AlarmResponse;
import com.sf.honeymorning.alarm.adapter.in.web.dto.response.PreparedAlarmContentResponse;
import com.sf.honeymorning.alarm.adapter.in.web.port.out.AlarmQueryPort;
import com.sf.honeymorning.alarm.adapter.out.persistence.entity.AlarmEntity;
import com.sf.honeymorning.alarm.adapter.out.persistence.mapper.AlarmPersistenceAdapterMapper;
import com.sf.honeymorning.alarm.adapter.out.persistence.repository.AlarmRepository;
import com.sf.honeymorning.alarm.application.domain.UpdateAlarm;
import com.sf.honeymorning.alarm.application.domain.VerifySleepModeAlarm;
import com.sf.honeymorning.alarm.application.port.out.CommandAlarmPort;
import com.sf.honeymorning.alarm.application.port.out.LoadAlarmPort;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.BriefingEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.entity.QuizEntity;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.BriefingRepository;
import com.sf.honeymorning.brief.adapter.out.persistence.repository.QuizRepository;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.common.exception.model.constant.ErrorProtocol;

@Component
public class AlarmPersistenceAdapter implements AlarmQueryPort, LoadAlarmPort, CommandAlarmPort {
	private final BriefingRepository briefingRepository;
	private final AlarmRepository alarmRepository;
	private final QuizRepository quizRepository;

	private final AlarmPersistenceAdapterMapper alarmPersistenceAdapterMapper;

	public AlarmPersistenceAdapter(BriefingRepository briefingRepository, AlarmRepository alarmRepository,
		QuizRepository quizRepository, AlarmPersistenceAdapterMapper alarmPersistenceAdapterMapper) {
		this.briefingRepository = briefingRepository;
		this.alarmRepository = alarmRepository;
		this.quizRepository = quizRepository;
		this.alarmPersistenceAdapterMapper = alarmPersistenceAdapterMapper;
	}

	public PreparedAlarmContentResponse getPreparedAlarmContents(Long userId) {
		AlarmEntity alarmEntity = alarmRepository.findByUserIdAndIsActiveTrue(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("존재하지 않는 사용자입니다. userId : {0}", userId),
				ErrorProtocol.BUSINESS_VIOLATION
			));
		BriefingEntity briefingEntity = briefingRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
			.orElseThrow(() -> new BusinessException(
				MessageFormat.format("알람 콘텐츠가 완성되지 않았어요. userId : {0}", userId),
				ErrorProtocol.POLICY_VIOLATION
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
