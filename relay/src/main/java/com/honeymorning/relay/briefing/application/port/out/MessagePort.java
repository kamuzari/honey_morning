package com.honeymorning.relay.briefing.application.port.out;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.honeymorning.relay.alarm.adapter.in.cdc.CdcAlarmEventDto;

public interface MessagePort {
	void publish(CdcAlarmEventDto scheduledAlarmContent) throws
		ExecutionException,
		InterruptedException,
		TimeoutException;
}
