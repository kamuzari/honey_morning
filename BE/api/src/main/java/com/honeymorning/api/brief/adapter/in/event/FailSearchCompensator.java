package com.honeymorning.api.brief.adapter.in.event;

import org.springframework.stereotype.Component;

import com.honeymorning.api.brief.application.port.in.FallBackSearchCommandUseCase;
import com.honeymorning.api.common.event.compensation.compensator.Compensator;
import com.honeymorning.api.common.event.compensation.model.FailEventTyper;
import com.honeymorning.api.common.event.compensation.model.FailType;

@Component
public class FailSearchCompensator implements Compensator {
	private static final FailType MY_FAIL_TYPE = FailType.SEARCH_FAILURE;
	private final FallBackSearchCommandUseCase fallBackSearchCommandUseCase;

	public FailSearchCompensator(FallBackSearchCommandUseCase fallBackSearchCommandUseCase) {
		this.fallBackSearchCommandUseCase = fallBackSearchCommandUseCase;
	}

	@Override
	public void fallback(FailEventTyper typer) {
		fallBackSearchCommandUseCase.write(typer.getIdentifierForReward());
	}

	@Override
	public boolean isEqual(FailEventTyper typer) {
		return MY_FAIL_TYPE.equals(typer.getFailType());
	}
}
