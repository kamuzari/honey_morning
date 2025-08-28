package com.honeymorning.api.common.event.compensation.compensator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.honeymorning.api.common.event.compensation.model.FailEventTyper;

@Component
public class CompensationFactory {
	private final List<Compensator> compensators;

	public CompensationFactory(List<Compensator> compensators) {
		this.compensators = compensators;
	}

	public void compensate(FailEventTyper typer) {
		var compensatorRepresent = compensators.stream().filter(c-> c.isEqual(typer))
			.findAny()
			.orElseThrow(() -> new IllegalStateException("Compensator doesn't exist"));
		compensatorRepresent.fallback(typer);
	}
}
