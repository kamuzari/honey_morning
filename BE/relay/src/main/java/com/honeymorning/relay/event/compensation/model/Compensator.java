package com.honeymorning.relay.event.compensation.model;

import com.honeymorning.relay.event.compensation.compensator.FailEventTyper;

public interface Compensator {
	void fallback(FailEventTyper typer);

	boolean isEqual(FailEventTyper typer);
}


