package com.sf.honeymorning.alarm.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sf.honeymorning.alarm.client.dto.TopicModelingResponse;

@FeignClient(name = "topicModelingClient", url = "${ai.client.topic-model}")
public interface TopicModelingClient {
	@PostMapping(value = "/ai/topic-model",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
	TopicModelingResponse send(@RequestBody List<String> categories);
}
