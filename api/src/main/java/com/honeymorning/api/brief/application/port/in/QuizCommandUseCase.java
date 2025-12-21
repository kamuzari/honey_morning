package com.honeymorning.api.brief.application.port.in;

import com.honeymorning.api.brief.adapter.in.web.dto.request.SelectionRequestDto;

public interface QuizCommandUseCase {

	void solve(Long userId, SelectionRequestDto selectionRequestDto);
}
