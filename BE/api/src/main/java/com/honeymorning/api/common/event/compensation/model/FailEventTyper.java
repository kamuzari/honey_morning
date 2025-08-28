package com.honeymorning.api.common.event.compensation.model;

public interface FailEventTyper {
	Long getIdentifierForReward();
	FailType getFailType();
}
