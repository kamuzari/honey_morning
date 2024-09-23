package com.sf.honeymorning.domain.quiz.controller;

import com.sf.honeymorning.domain.quiz.dto.QuizDto;
import com.sf.honeymorning.domain.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "퀴즈")
@RequestMapping("/api/quizzes")
@RestController
@AllArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @Operation(
            summary = "퀴즈 전체 결과 확인"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "퀴즈 결과 가져오기 성공",
                    content = @Content(schema = @Schema(type = "string", example = "success", implementation = QuizDto.class))
            )
    })
    @GetMapping("/results")
    public ResponseEntity<?> show() {
        return quizService.getQuiz();
    }

}
	
