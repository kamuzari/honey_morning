package com.sf.honeymorning.brief.application.port.in;

import com.sf.honeymorning.brief.adapter.in.web.dto.request.SelectionRequestDto;

public interface QuizCommandUseCase {

	void solve(Long userId, SelectionRequestDto selectionRequestDto);
}
