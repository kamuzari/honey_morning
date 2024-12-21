package com.sf.honeymorning.util;

import java.util.Objects;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class ResponseEntityUtils {
	private ResponseEntityUtils() {
	}

	public static String getContentLength(ResponseEntity<?> response) {
		return Objects.requireNonNull(response.getHeaders()
			.getFirst(HttpHeaders.CONTENT_LENGTH));
	}

	public static String getContentType(ResponseEntity<?> response) {
		return Objects.requireNonNull(response.getHeaders()
			.getFirst(HttpHeaders.CONTENT_TYPE));
	}
}
