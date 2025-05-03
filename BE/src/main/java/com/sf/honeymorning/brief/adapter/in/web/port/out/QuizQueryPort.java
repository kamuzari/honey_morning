package com.sf.honeymorning.brief.adapter.in.web.port.out;

import java.util.List;

import com.sf.honeymorning.brief.adapter.in.web.dto.response.detail.QuizResponseDto;

public interface QuizQueryPort {
	List<QuizResponseDto> getQuizzes(Long userId, Long briefId);
}
