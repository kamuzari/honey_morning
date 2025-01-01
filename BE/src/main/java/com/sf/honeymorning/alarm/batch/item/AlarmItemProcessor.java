package com.sf.honeymorning.alarm.batch.item;

import org.springframework.batch.item.ItemProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sf.honeymorning.alarm.batch.item.dto.ReadyAlarmDto;
import com.sf.honeymorning.alarm.batch.outbox.OutBoxAlarmEvent;
import com.sf.honeymorning.alarm.service.dto.request.ToAIRequestDto;

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
