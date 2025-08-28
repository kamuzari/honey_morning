package com.honeymorning.batch.item.processor;

import org.springframework.batch.item.ItemProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeymorning.batch.item.dto.ReadyAlarmDto;
import com.honeymorning.batch.outbox.OutBoxAlarmEvent;
import com.honeymorning.batch.item.dto.ToAIRequestDto;

public class AlarmItemProcessor implements ItemProcessor<ReadyAlarmDto, OutBoxAlarmEvent> {

	private final ObjectMapper mapper;

	public AlarmItemProcessor(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public OutBoxAlarmEvent process(ReadyAlarmDto alarm) throws Exception {
		String payload = mapper.writeValueAsString(new ToAIRequestDto(alarm.userId(), alarm.tags()));

		return OutBoxAlarmEvent.initialize(alarm.userId(), payload);
	}
}
