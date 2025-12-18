package com.honeymorning.relay.briefing.adapter.in.cdc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.honeymorning.relay.briefing.application.port.in.SearchCommandUseCase;

@Component
public class SearchCdcAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchCdcAdapter.class);

	private final SearchCommandUseCase searchCommandUseCase;

	public SearchCdcAdapter(SearchCommandUseCase searchCommandUseCase) {
		this.searchCommandUseCase = searchCommandUseCase;
	}

	@KafkaListener(
		topics = "${app.kafka.topics.briefing-indexing-cdc.name}",
		groupId = "${app.kafka.consumers.briefing-indexing-cdc.group-id}",
		containerFactory = "cdcKafkaListenerContainerFactory"
	)
	public void consumeBriefingEvent(CommandSearchBriefingTextDto dto, Acknowledgment acknowledgment) {
		LOGGER.info("consume briefing cdc event, briefingId={}", dto.briefingId());
		searchCommandUseCase.register(dto.briefingId());
		acknowledgment.acknowledge();
	}

	@KafkaListener(
		topics = "${app.kafka.topics.briefing-indexing-cdc.name}.DLT",
		groupId = "${app.kafka.consumers.briefing-indexing-cdc.group-id}",
	containerFactory = "cdcKafkaListenerContainerFactory"
	)
	public void consumeBriefingEventDlt(CommandSearchBriefingTextDto dto) {
		LOGGER.warn("consume briefing cdc DLT event, briefingId={}", dto.briefingId());
		searchCommandUseCase.register(dto.briefingId());
	}
}
