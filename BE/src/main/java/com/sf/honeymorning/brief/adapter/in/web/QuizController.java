package com.sf.honeymorning.brief.adapter.in.web;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sf.honeymorning.brief.adapter.in.web.dto.request.SelectionRequestDto;
import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.QuizResponseDto;
import com.sf.honeymorning.brief.adapter.in.web.port.out.QuizQueryPort;
import com.sf.honeymorning.brief.application.port.in.SolveQuizUseCase;
import com.sf.honeymorning.user.adapter.in.authentication.model.JwtAuthentication;

import jakarta.validation.Valid;

@RequestMapping("/api/quizzes")
@RestController
public class QuizController {
	private final QuizQueryPort quizQueryPort;
	private final SolveQuizUseCase solveQuizUseCase;

	public QuizController(QuizQueryPort quizQueryPort, SolveQuizUseCase solveQuizUseCase) {
		this.quizQueryPort = quizQueryPort;
		this.solveQuizUseCase = solveQuizUseCase;
	}

	@GetMapping("/{briefId}")
	public List<QuizResponseDto> getQuizzes(@AuthenticationPrincipal
		JwtAuthentication principal,
		@PathVariable Long briefId) {

		return quizQueryPort.getQuizzes(principal.id(), briefId);
	}

	@PatchMapping
	public void solve(@AuthenticationPrincipal
		JwtAuthentication principal,
		@Valid
		@RequestBody
		SelectionRequestDto selectionRequestDto) {
		solveQuizUseCase.solve(principal.id(), selectionRequestDto);
	}
}

