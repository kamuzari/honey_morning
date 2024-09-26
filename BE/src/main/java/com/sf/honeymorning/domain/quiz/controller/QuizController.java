package com.sf.honeymorning.domain.quiz.controller;

import com.sf.honeymorning.domain.brief.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.domain.quiz.dto.QuizRequestDto;
import com.sf.honeymorning.domain.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

}

