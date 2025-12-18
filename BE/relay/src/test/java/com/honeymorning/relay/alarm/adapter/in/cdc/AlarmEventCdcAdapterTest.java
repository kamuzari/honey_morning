package com.honeymorning.relay.alarm.adapter.in.cdc;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StreamUtils;

import com.honeymorning.relay.alarm.adapter.out.message.AlarmContentCreatePublisher;
import com.honeymorning.relay.briefing.adapter.in.consumer.AiClientDltConsumer;
import com.honeymorning.relay.context.integration.DefaultIntegrationTest;

@TestPropertySource(properties = {
	"app.kafka.consumers.cdc.group-id=${random.uuid}"
})
class AlarmEventCdcAdapterTest extends DefaultIntegrationTest {

	@SpyBean
	AlarmEventCdcAdapter sut;

	@Value("${app.kafka.topics.to-ai-cdc.name}")
	String topic;

	@MockBean
	AiClientDltConsumer aiClientMessenger;

	@MockBean
	AlarmContentCreatePublisher alarmContentCreatePublisher;

	@Autowired
	KafkaTemplate<String, String> kafkaTemplate;

	String publishedMessage;

	@Test
	@DisplayName("카프카에 적재된 로그 테일링 할 메시지를 소비한다.")
	void testConsumeMessage() throws IOException, ExecutionException, InterruptedException, TimeoutException {
		//given
		loadCdcSampleData();
		willDoNothing().given(alarmContentCreatePublisher).publish(any(CdcAlarmEventDto.class));
		kafkaTemplate.send(topic, publishedMessage);

		//when
		//then
		await().atMost(10, SECONDS)
			.untilAsserted(() -> {
				verify(sut, times(1)).consumeOutboxEvent(any(CdcAlarmEventDto.class), any(Acknowledgment.class));
			});
	}

	private void loadCdcSampleData() throws IOException {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("./sample/cdc-consume.json");
		publishedMessage = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
	}
}