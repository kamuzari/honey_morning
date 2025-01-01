package com.sf.honeymorning.alarm.batch.outbox;

public enum EventStatus {
	PUBLISH("이벤트 발행"), PENDING("발행 전"), COMPLETED("이벤트 처리 완료"), FAILED("이벤트 발행 실패");

	private String status;

	EventStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
