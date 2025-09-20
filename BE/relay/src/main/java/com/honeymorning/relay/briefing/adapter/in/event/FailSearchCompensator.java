package com.honeymorning.relay.briefing.adapter.in.event;

import org.springframework.stereotype.Component;

import com.honeymorning.relay.briefing.application.port.in.FallBackSearchCommandUseCase;
import com.honeymorning.relay.event.compensation.compensator.FailEventTyper;
import com.honeymorning.relay.event.compensation.compensator.FailType;
import com.honeymorning.relay.event.compensation.model.Compensator;

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
