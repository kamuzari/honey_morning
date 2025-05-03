package com.sf.honeymorning.brief.application.port.in;

import com.sf.honeymorning.brief.adapter.in.web.dto.request.SelectionRequestDto;

public interface SolveQuizUseCase {

	void solve(Long userId, SelectionRequestDto selectionRequestDto);
}
