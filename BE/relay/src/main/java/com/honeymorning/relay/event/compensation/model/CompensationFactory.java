package com.honeymorning.relay.event.compensation.model;

import java.util.List;

import org.springframework.stereotype.Component;

import com.honeymorning.relay.event.compensation.compensator.FailEventTyper;

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
