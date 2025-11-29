package com.honeymorning.batch.exception;

public class ReadyAlramBatchException extends RuntimeException {
	private final ErrorProtocol errorProtocol;

	public ReadyAlramBatchException(String detailMessage, ErrorProtocol errorProtocol) {
		super(detailMessage);
		this.errorProtocol = errorProtocol;
	}

	public ReadyAlramBatchException(String message, Throwable cause, ErrorProtocol errorProtocol) {
		super(message, cause);
		this.errorProtocol = errorProtocol;
	}

	public ErrorProtocol getErrorProtocol() {
		return errorProtocol;
	}
}
