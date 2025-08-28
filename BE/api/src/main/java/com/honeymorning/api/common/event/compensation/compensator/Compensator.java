package com.honeymorning.api.common.event.compensation.compensator;

import com.honeymorning.api.common.event.compensation.model.FailEventTyper;

public interface Compensator {
	void fallback(FailEventTyper typer);

	boolean isEqual(FailEventTyper typer);
}


