package com.honeymorning.api.alarm.adapter.out.persistence.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.honeymorning.api.alarm.adapter.in.web.dto.response.AlarmResponse;
import com.honeymorning.api.alarm.adapter.in.web.dto.response.PreparedAlarmContentResponse;
import com.honeymorning.api.alarm.application.domain.UpdateAlarm;
import com.honeymorning.api.alarm.application.domain.VerifySleepModeAlarm;
import com.honeymorning.common.domain.alarm.entity.AlarmEntity;
import com.honeymorning.common.domain.briefing.entity.BriefingEntity;
import com.honeymorning.common.domain.briefing.entity.QuizEntity;

@Component
public class AlarmPersistenceAdapterMapper {
	public AlarmResponse toAlarmResponse(AlarmEntity alarmEntity) {
		return new AlarmResponse(
			alarmEntity.getId(),
			alarmEntity.getWakeUpTime(),
			alarmEntity.getDayOfTheWeeks(),
			alarmEntity.getRepeatFrequency(),
			alarmEntity.getRepeatInterval(),
			alarmEntity.isActive()
		);
	}

	public PreparedAlarmContentResponse toPreparedAlarmContentResponse(AlarmEntity alarmEntity,
		BriefingEntity briefingEntity,
		List<QuizEntity> quizEntities) {
		return new PreparedAlarmContentResponse(
			alarmEntity.getRepeatInterval(),
			alarmEntity.getRepeatFrequency(),
			alarmEntity.getWakeUpTime(),
			briefingEntity.getWakeUpBriefingContent().getFileUrl(),
			quizEntities.stream().map(quiz -> quiz.getWakeUpQuizContent().getFileUrl()).toList()
		);
	}

	public VerifySleepModeAlarm toSleepModeDomain(AlarmEntity alarmEntity) {
		return new VerifySleepModeAlarm(
			alarmEntity.getWakeUpTime(),
			alarmEntity.getDayOfTheWeeks(),
			alarmEntity.isActive()
		);
	}

	public UpdateAlarm toDomain(AlarmEntity alarmEntity) {
		return new UpdateAlarm(
			alarmEntity.getId(),
			alarmEntity.getUserId(),
			alarmEntity.getWakeUpTime(),
			alarmEntity.getDayOfTheWeeks(),
			alarmEntity.getRepeatFrequency(),
			alarmEntity.getRepeatInterval(),
			alarmEntity.isActive()
		);
	}
}
