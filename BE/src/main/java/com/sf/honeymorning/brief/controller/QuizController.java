package com.sf.honeymorning.brief.controller;

import com.sf.honeymorning.brief.controller.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.controller.dto.request.SelectionRequestDto;
import com.sf.honeymorning.brief.service.QuizService;
import com.sf.honeymorning.user.adapter.in.authentication.model.JwtAuthentication;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/quizzes")
@RestController
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/{briefId}")
    public List<QuizResponseDto> getQuizzes(@AuthenticationPrincipal
                                            JwtAuthentication principal,
                                            @PathVariable Long briefId) {
        return quizService.getQuizzes(principal.id(), briefId);
    }

    @PatchMapping
    public void addSelections(@AuthenticationPrincipal
                              JwtAuthentication principal,
                              @Valid
                              @RequestBody
                              SelectionRequestDto selectionRequestDto) {
        quizService.addSelections(principal.id(),selectionRequestDto);
    }
}

