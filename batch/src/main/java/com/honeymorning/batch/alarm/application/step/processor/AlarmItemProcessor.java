package com.honeymorning.batch.alarm.application.step.processor;

import org.springframework.batch.item.ItemProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeymorning.batch.alarm.application.dto.ReadyAlarmDto;
import com.honeymorning.batch.alarm.application.dto.ToAiRequestDto;
import com.honeymorning.common.domain.event.entity.OutBoxAlarmEventEntity;

public class AlarmItemProcessor implements ItemProcessor<ReadyAlarmDto, OutBoxAlarmEventEntity> {

	private final ObjectMapper mapper;

	public AlarmItemProcessor(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public OutBoxAlarmEventEntity process(ReadyAlarmDto alarm) throws Exception {
		String payload = mapper.writeValueAsString(new ToAiRequestDto(alarm.userId(), alarm.tags()));

		return OutBoxAlarmEventEntity.initialize(alarm.userId(), payload);
	}
}
