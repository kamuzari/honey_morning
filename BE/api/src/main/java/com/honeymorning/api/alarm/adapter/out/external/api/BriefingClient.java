package com.honeymorning.api.alarm.adapter.out.external.api;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.honeymorning.api.alarm.adapter.out.external.dto.BriefingResponse;

@FeignClient(name = "briefingClient", url = "${ai.client.brief}")
public interface BriefingClient {
	@PostMapping(value = "/ai/briefing",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
	BriefingResponse send(@RequestBody List<String> categories);
}
