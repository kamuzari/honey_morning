package com.sf.honeymorning.brief.adapter.in.event;

import org.springframework.stereotype.Component;

import com.sf.honeymorning.brief.application.port.in.FallBackTtsCommandUseCase;
import com.sf.honeymorning.common.event.compensation.compensator.Compensator;
import com.sf.honeymorning.common.event.compensation.model.FailEventTyper;
import com.sf.honeymorning.common.event.compensation.model.FailType;

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
