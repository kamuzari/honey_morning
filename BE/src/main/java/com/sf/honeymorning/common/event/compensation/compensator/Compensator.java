package com.sf.honeymorning.common.event.compensation.compensator;

import com.sf.honeymorning.common.event.compensation.model.FailEventTyper;

public interface Compensator {
	void fallback(FailEventTyper typer);

	boolean isEqual(FailEventTyper typer);
}


