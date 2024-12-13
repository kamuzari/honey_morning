package com.sf.honeymorning.common.exception.model;

public class UnExpectedFatalException extends RuntimeException {
	private final ErrorProtocol errorProtocol;

	public UnExpectedFatalException(String detailMessage, ErrorProtocol errorProtocol) {
		super(detailMessage);
		this.errorProtocol = errorProtocol;
	}

	public UnExpectedFatalException(String message, Throwable cause, ErrorProtocol errorProtocol) {
		super(message, cause);
		this.errorProtocol = errorProtocol;
	}

	public ErrorProtocol getErrorProtocol() {
		return errorProtocol;
	}
}
