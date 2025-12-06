package com.honeymorning.batch.alarm.application.constant;

public final class AlarmBatchConstant {
	private AlarmBatchConstant() {}

	public static final String JOB = "alarmToAlarmEventCreateJob";

	public static final String STEP = JOB + "_step";
	public static final String STEP_MANAGER = "alarmStep.manager";
	public static final String STEP_PARTITIONER = "alarmStep_partitioner";

	public static final String READER = JOB + "_reader";

	public static final String THREAD_PREFIX = "multi-thread -- " + JOB;
}