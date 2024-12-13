package com.sf.honeymorning.alarm.exception;

import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;

public class NotPreparedAlarmException extends BusinessException {
	public NotPreparedAlarmException(String detailMessage,
		ErrorProtocol errorProtocol) {
		super(detailMessage, errorProtocol);
	}

	public NotPreparedAlarmException(String message, Throwable cause, ErrorProtocol errorProtocol) {
		super(message, cause, errorProtocol);
	}
}
