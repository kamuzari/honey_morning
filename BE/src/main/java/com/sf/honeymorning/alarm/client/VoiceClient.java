package com.sf.honeymorning.alarm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.sf.honeymorning.alarm.client.dto.VoiceCreateRequestDto;

@FeignClient(name = "elevenLabsClient",
	url = "${voice.client.base-url}")
public interface VoiceClient {
	@PostMapping(value = "/text-to-speech/{voiceId}",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	ResponseEntity<Resource> createTts(
		@PathVariable("voiceId") String voiceId,
		@RequestHeader("xi-api-key") String apiKey,
		@RequestHeader("optimize_streaming_latency") int latency,
		@RequestHeader("output_format") String outputFormat,
		@RequestBody VoiceCreateRequestDto body
	);

}
