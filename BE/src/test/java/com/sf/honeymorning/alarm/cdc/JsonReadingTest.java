package com.sf.honeymorning.alarm.cdc;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class JsonReadingTest {

	private static final String JSON_SAMPLE_FILE_LOCATION = "./sample/cdc-consume.json";
	public static final String REQUIREMENT_DATA_FORMAT = "payload";

	@Autowired
	private ObjectMapper objectMapper;

	ResourceLoader resourceLoader = new DefaultResourceLoader();

	@DisplayName("cdc 에서 가져온 json 형식의 데이터를 변환한다")
	@Test
	void testReadCdcEventJson() throws IOException {
		/// given
		Resource resource = resourceLoader.getResource(JSON_SAMPLE_FILE_LOCATION);
		JsonNode rootNode = objectMapper.readTree(resource.getInputStream());

		// when
		var alarmEventDto = objectMapper.convertValue(rootNode.get(REQUIREMENT_DATA_FORMAT), CdcAlarmEventDto.class);

		// then
		assertThat(alarmEventDto).isNotNull();
		assertThat(alarmEventDto.getId()).isNotNull();
		assertThat(alarmEventDto.getAlarmId()).isNotNull();
		assertThat(alarmEventDto.getPayload()).isNotNull();
		assertThat(alarmEventDto.getCreatedAt()).isNotNull();
	}

}
