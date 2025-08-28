package com.honeymorning.batch.exception;

public enum ServiceStatus {
	SERVICE_UNAVAILABLE(503, "서비스를 사용할 수 없습니다.");

	private final int code;
	private final String message;

	ServiceStatus(int code, String message) {
		this.code = code;
		this.message = message;
	}
}
