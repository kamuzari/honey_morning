package com.sf.honeymorning.alarm.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.sf.honeymorning.alarm.client.dto.BriefingResponse;

@FeignClient(name = "briefingClient", url = "${ai.client.brief}")
public interface BriefingClient {
	@PostMapping(value = "/ai/briefing",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
	BriefingResponse send(@RequestBody List<String> categories);
}
