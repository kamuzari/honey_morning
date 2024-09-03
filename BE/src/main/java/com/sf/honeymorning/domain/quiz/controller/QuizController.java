package com.sf.honeymorning.domain.quiz.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "퀴즈")
@RequestMapping("/api/quizzes")
@RestController
public class QuizController {
	@Operation(
		summary = "퀴즈 결과 확인"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "퀴즈 결과 가져오기 성공",
			content = @Content(schema = @Schema(type = "string", example = "success"))
		)
	})
	@GetMapping("/result")
	public ResponseEntity<String> show() {
		return ResponseEntity.ok("success");
	}

}
	
