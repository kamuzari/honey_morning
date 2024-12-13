package com.sf.honeymorning.alarm.exception;

import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;

public class AlarmBusinessException extends BusinessException {
	public AlarmBusinessException(String detailMessage,
		ErrorProtocol errorProtocol) {
		super(detailMessage, errorProtocol);
	}

	public AlarmBusinessException(String message, Throwable cause, ErrorProtocol errorProtocol) {
		super(message, cause, errorProtocol);
	}
}
