package com.sf.honeymorning.account.authenticater.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenNotFoundException extends RuntimeException {

	public TokenNotFoundException(String message) {
		super(message);
	}

	public TokenNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}
