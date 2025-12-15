package com.honeymorning.batch.common.exception;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorProtocol {
	UNEXPECTED_FATAL_ERROR("FATAL-1000", "관리자에게 코드로 문의해주세요.", "예상치 못한 오류가 발생하였습니다."),
	BATCH_JOB_RUN_ERROR("BATCH-1000", "관리자에게 코드로 문의해주세요.", "Batch Job 실행 중 오류가 트리거와 앱 로그를 확인하세요.");
	private final String customCode;
	private final String clientMessage;
	private final String internalMessage;

	ErrorProtocol(
		String customCode,
		String clientMessage,
		String internalMessage) {

		this.customCode = customCode;
		this.clientMessage = clientMessage;
		this.internalMessage = internalMessage;
	}

}
