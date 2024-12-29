package com.sf.honeymorning.alarm.batch.item;

import org.springframework.batch.item.ItemProcessor;

import com.sf.honeymorning.alarm.batch.item.dto.ReadyAlarmDto;
import com.sf.honeymorning.alarm.service.dto.request.ToAIRequestDto;

public class AlarmItemProcessor implements ItemProcessor<ReadyAlarmDto, ToAIRequestDto> {

	@Override
	public ToAIRequestDto process(ReadyAlarmDto alarm) throws Exception {
		return new ToAIRequestDto(alarm.userId(), alarm.tags());
	}
}
