package com.honeymorning.common.exception;

import com.honeymorning.common.exception.constant.ErrorProtocol;

public class BusinessException extends RuntimeException {
	private final ErrorProtocol errorProtocol;

	public BusinessException(String detailMessage, ErrorProtocol errorProtocol) {
		super(detailMessage);
		this.errorProtocol = errorProtocol;
	}

	public BusinessException(String message, Throwable cause, ErrorProtocol errorProtocol) {
		super(message, cause);
		this.errorProtocol = errorProtocol;
	}

	public ErrorProtocol getErrorProtocol() {
		return errorProtocol;
	}
}
