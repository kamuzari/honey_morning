package com.honeymorning.relay.event.compensation.compensator;

public interface FailEventTyper {
	Long getIdentifierForReward();

	FailType getFailType();
}
