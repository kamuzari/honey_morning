package com.honeymorning.api.alarm.adapter.out.external.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.honeymorning.api.alarm.adapter.out.external.dto.WakeUpCallSongResponse;

@FeignClient(name = "wakeUpCallSongClient", url = "${ai.client.song}")
public interface WakeUpCallSongClient {

	@PostMapping(value = "/ai/song",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
	WakeUpCallSongResponse send(@RequestBody String voiceContent);
}
