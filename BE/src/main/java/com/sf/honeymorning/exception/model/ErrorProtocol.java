package com.sf.honeymorning.exception.model;

import java.util.StringJoiner;

import org.springframework.http.HttpStatus;

public enum ErrorProtocol {
	POLICY_VIOLATION(HttpStatus.INTERNAL_SERVER_ERROR, 1000, "관리자에게 코드로 문의해주세요.", "정책 비즈니스 위반하였습니다."),
	BUSINESS_VIOLATION(HttpStatus.INTERNAL_SERVER_ERROR, 4004, "잘못된 접근입니다.", "허용하지 않는 접근입니다."),
	READY_TO_ALARM_VOICE_TRANSLATION_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 5000, "관리자에게 코드로 문의해주세요.", "외부 음성 컨텐츠를 만들지 못하였습니다. 음성 콘텐츠 서버를 확인해주세요.");

	private final HttpStatus status;
	private final int customCode;
	private final String clientMessage;
	private final String internalMessage;

	ErrorProtocol(HttpStatus status, int customCode, String clientMessage, String internalMessage) {
		this.status = status;
		this.customCode = customCode;
		this.clientMessage = clientMessage;
		this.internalMessage = internalMessage;
	}

	public HttpStatus getStatus() {
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

	@Override
	public String toString() {
		return new StringJoiner(", ", ErrorProtocol.class.getSimpleName() + "[", "]")
			.add("status=" + status)
			.add("customCode=" + customCode)
			.add("clientMessage='" + clientMessage + "'")
			.add("internalMessage='" + internalMessage + "'")
			.toString();
	}
}
