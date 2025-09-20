package com.honeymorning.relay.briefing.adapter.in.event;

import org.springframework.stereotype.Component;

import com.honeymorning.relay.briefing.application.port.in.FallBackTtsCommandUseCase;
import com.honeymorning.relay.event.compensation.compensator.FailEventTyper;
import com.honeymorning.relay.event.compensation.compensator.FailType;
import com.honeymorning.relay.event.compensation.model.Compensator;

@Component
public class FailTtsCompensator implements Compensator {
	private static final FailType MY_FAIL_TYPE = FailType.TTS_FAILURE;
	private final FallBackTtsCommandUseCase fallBackTtsCommandUseCase;

	public FailTtsCompensator(FallBackTtsCommandUseCase toSpeechCommandUseCase) {
		this.fallBackTtsCommandUseCase = toSpeechCommandUseCase;
	}

	@Override
	public void fallback(FailEventTyper typer) {
		fallBackTtsCommandUseCase.write(typer.getIdentifierForReward());
	}

	@Override
	public boolean isEqual(FailEventTyper typer) {
		return MY_FAIL_TYPE.equals(typer.getFailType());
	}
}
