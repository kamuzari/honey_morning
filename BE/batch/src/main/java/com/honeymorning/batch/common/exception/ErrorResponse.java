package com.honeymorning.batch.common.exception;

record ErrorResponse(String code, String message, String detail) {
	public static ErrorResponse of(ErrorProtocol errorCode, String detail) {
		return new ErrorResponse(
			errorCode.getCustomCode(),
			errorCode.getClientMessage(),
			detail);
	}


}
