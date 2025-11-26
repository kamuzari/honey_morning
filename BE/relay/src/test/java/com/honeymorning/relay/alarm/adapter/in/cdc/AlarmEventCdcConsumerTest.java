package com.honeymorning.relay.alarm.adapter.in.cdc;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.honeymorning.relay.context.infra.broker.KafkaContext;
import com.honeymorning.relay.context.integration.DefaultIntegrationTest;

class AlarmEventCdcConsumerTest extends DefaultIntegrationTest implements KafkaContext {

	@SpyBean
	AlarmEventCdcConsumer sut;

	@Value("${app.kafka.topics.cdc.name}")
	String topic;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	String publishedOffset;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@DisplayName("카프카에 적재된 로그 테일링 할 메시지를 소비한다.")
	void testConsumeMessage() throws IOException {
		//given
		updateUpLogTailingData();

		//when
		//then
		await().atMost(1, SECONDS)
			.untilAsserted(() -> {
				verify(sut, times(1)).consumeOutboxEvent(eq(publishedOffset), forClass(Acknowledgment.class).capture());
			});
	}

	private void updateUpLogTailingData() throws IOException {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("./sample/cdc-consume.json");
		publishedOffset = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		kafkaTemplate.send(topic, publishedOffset);
	}
}