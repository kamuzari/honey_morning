package com.honeymorning.api.brief.adapter.in.web.port.out;

import java.util.List;

import com.honeymorning.api.brief.adapter.in.web.dto.response.detail.QuizResponseDto;

public interface QuizQueryPort {
	List<QuizResponseDto> getQuizzes(Long userId, Long briefId);
}
