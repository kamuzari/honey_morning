package com.honeymorning.common.exception;

import com.honeymorning.common.exception.constant.ErrorProtocol;

public class NotFoundResourceException extends RuntimeException {
	private final ErrorProtocol errorProtocol;

	public NotFoundResourceException(String detailMessage, ErrorProtocol errorProtocol) {
		super(detailMessage);
		this.errorProtocol = errorProtocol;
	}

	public NotFoundResourceException(String message, Throwable cause, ErrorProtocol errorProtocol) {
		super(message, cause);
		this.errorProtocol = errorProtocol;
	}

	public ErrorProtocol getErrorProtocol() {
		return errorProtocol;
	}
}
