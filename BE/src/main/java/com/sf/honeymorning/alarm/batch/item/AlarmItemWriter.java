package com.sf.honeymorning.alarm.batch.item;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.sf.honeymorning.alarm.service.dto.request.ToAIRequestDto;
import com.sf.honeymorning.config.RabbitConfig;

public class AlarmItemWriter implements ItemWriter<ToAIRequestDto> {
	private static final String PUBLISH_QUEUE_NAME = RabbitConfig.AI_GENERATIVE_ALARM_CONTENTS_QUEUE_NAME;

	private final RabbitTemplate rabbitTemplate;

	public AlarmItemWriter(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void write(Chunk<? extends ToAIRequestDto> chunk) throws Exception {
		chunk.getItems().forEach(toAIRequestDto -> rabbitTemplate.convertAndSend(PUBLISH_QUEUE_NAME, toAIRequestDto));
	}
}
