package com.sf.honeymorning.alarm.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import com.sf.honeymorning.alarm.client.dto.QuizResponseDto;

@FeignClient(name = "quizGeneratorClient", url = "${ai.client.quiz}")
public interface QuizGeneratorClient {
	@PostMapping(value = "/ai/quiz",
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
	List<QuizResponseDto> send(String readContent);
}
