package com.sf.honeymorning.quiz.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.quiz.dto.QuizRequestDto;
import com.sf.honeymorning.quiz.service.QuizService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@Tag(name = "퀴즈")
@RequestMapping("/api/quizzes")
@RestController
@AllArgsConstructor
public class QuizController {

	private final QuizService quizService;

	@Operation(
		summary = "퀴즈 확인"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "퀴즈 가져오기 성공",
			content = @Content(schema = @Schema(type = "string", example = "success", implementation = QuizRequestDto.class))
		)
	})
	@GetMapping("/{briefId}")
	public ResponseEntity<List<QuizResponseDto>> showAllResults(@PathVariable Long briefId) {
		List<QuizResponseDto> quizResponseDtoList = quizService.getQuiz(briefId);
		return new ResponseEntity<>(quizResponseDtoList, HttpStatus.OK);
	}

	@Operation(
		summary = "퀴즈 결과 업데이트"
	)
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "퀴즈 결과 업데이트 성공",
			content = @Content(schema = @Schema(type = "string", example = "success", implementation = QuizRequestDto.class))
		)
	})
	@PatchMapping
	public ResponseEntity<?> updateQuizResults(@RequestBody QuizRequestDto quizRequestDto) {
		return quizService.updateQuiz(quizRequestDto);
	}

	@Operation(summary = "퀴즈 오디오 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "오디오 파일 조회 성공",
			content = @Content(mediaType = "audio/mpeg", schema = @Schema(type = "string", format = "binary"))),
		@ApiResponse(responseCode = "404", description = "브리핑을 찾을 수 없음", content = @Content),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
	})
	@GetMapping("/audio/{quiz_id}")
	public ResponseEntity<Resource> getQuizAudio(@PathVariable(name = "quiz_id") Long quizId) {
		try {
			Resource resource = quizService.getQuizAudio(quizId);
			return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType("audio/mpeg"))
				.header(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
		} catch (EntityNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (IOException e) {
			return ResponseEntity.internalServerError().build();
		}
	}

}

