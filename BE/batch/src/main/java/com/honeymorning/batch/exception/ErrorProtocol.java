package com.honeymorning.batch.exception;

import static com.honeymorning.batch.exception.ServiceStatus.SERVICE_UNAVAILABLE;

import lombok.ToString;

@ToString
public enum ErrorProtocol {
	UNEXPECTED_FATAL_ERROR(SERVICE_UNAVAILABLE, 5003, "관리자에게 코드로 문의해주세요.", "예상치 못한 오류가 발생하였습니다.");

	private final ServiceStatus status;
	private final int customCode;
	private final String clientMessage;
	private final String internalMessage;

	ErrorProtocol(
		ServiceStatus status,
		int customCode,
		String clientMessage,
		String internalMessage) {

		this.status = status;
		this.customCode = customCode;
		this.clientMessage = clientMessage;
		this.internalMessage = internalMessage;
	}

	public ServiceStatus getStatus() {
		return status;
	}

	public int getCustomCode() {
		return customCode;
	}

	public String getClientMessage() {
		return clientMessage;
	}

	public String getInternalMessage() {
		return internalMessage;
	}

}
