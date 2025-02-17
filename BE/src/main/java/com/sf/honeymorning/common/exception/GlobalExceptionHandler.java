package com.sf.honeymorning.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import java.time.LocalTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.naming.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sf.honeymorning.alarm.exception.AlarmFatalException;
import com.sf.honeymorning.common.exception.alarm.ReadyAlramBatchException;
import com.sf.honeymorning.common.exception.model.BusinessException;
import com.sf.honeymorning.common.exception.model.ErrorProtocol;
import com.sf.honeymorning.common.exception.model.NotFoundResourceException;
import com.sf.honeymorning.common.exception.user.AlarmCategoryNotFoundException;
import com.sf.honeymorning.common.exception.user.UserUpdateException;

import jakarta.persistence.PersistenceException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	private static final String MESSAGE_PROPERTY_KEY = "errorMessages";

	@ExceptionHandler(AuthenticationException.class)
	public ErrorResponse handleAuthenticationExceptions(AuthenticationException exception) {
		log.warn("Authentication Error: {}", exception.getMessage(), exception);
		return ErrorResponse.builder(exception, HttpStatus.INTERNAL_SERVER_ERROR, "")
			.detail("[authentication error] - authentication check")
			.property(MESSAGE_PROPERTY_KEY, LocalTime.now())
			.build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException exception) {
		log.warn("Validation Error: {}", exception.getMessage(), exception);
		Map<String, String> errorDetailMessage = exception.getBindingResult().getFieldErrors().stream()
			.collect(Collectors.toMap(FieldError::getField,
				Objects.requireNonNull(FieldError::getDefaultMessage)));

		return ErrorResponse.builder(exception, BAD_REQUEST, "")
			.detail("One or more fields are invalid.")
			.property(MESSAGE_PROPERTY_KEY, errorDetailMessage)
			.build();
	}

	@ExceptionHandler(PersistenceException.class)
	public ErrorResponse handlePersistenceException(PersistenceException exception) {
		log.error("Persistence Error: {}", exception.getMessage(), exception);
		return ErrorResponse.builder(exception, HttpStatus.INTERNAL_SERVER_ERROR, "")
			.detail("[fatal error] - calling administrator")
			.property(MESSAGE_PROPERTY_KEY, LocalTime.now())
			.build();
	}

	@ExceptionHandler(BusinessException.class)
	public ErrorResponse handleBusinessException(BusinessException exception) {
		log.error("Business Error: {}, {} ", exception.getErrorProtocol(), exception.getMessage(), exception);
		ErrorProtocol errorProtocol = exception.getErrorProtocol();

		return ErrorResponse.builder(exception, errorProtocol.getStatus(),
				errorProtocol.getClientMessage())
			.detail(errorProtocol.getInternalMessage()) // 내부 메시지 설정
			.property(MESSAGE_PROPERTY_KEY, errorProtocol.getClientMessage()) // 커스텀 코드 포함
			.property("code", errorProtocol.getCustomCode()) // 커스텀 코드 포함
			.build();
	}

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<String> handleTransactionSystemException(TransactionSystemException ex) {
		Throwable rootCause = ex.getRootCause();
		String errorMessage = "데이터베이스 트랜잭션 처리 중 오류가 발생했습니다.";

		if (rootCause != null) {
			errorMessage = "원인 " + rootCause.getMessage();
		}

		return new ResponseEntity<>(errorMessage, INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(NotFoundResourceException.class)
	public ErrorResponse handleUserNotFoundException(NotFoundResourceException exception) {
		log.error("not found resource error: {}, {} ", exception.getErrorProtocol(), exception.getMessage(), exception);
		ErrorProtocol errorProtocol = exception.getErrorProtocol();

		return ErrorResponse.builder(exception, errorProtocol.getStatus(),
				errorProtocol.getClientMessage())
			.detail(errorProtocol.getInternalMessage())
			.property(MESSAGE_PROPERTY_KEY, errorProtocol.getClientMessage())
			.property("code", errorProtocol.getCustomCode())
			.build();
	}

	@ExceptionHandler(ReadyAlramBatchException.class)
	public ErrorResponse handleBatchExceptions(ReadyAlramBatchException exception) {
		log.error("Batch Job Error: {}, {}", exception.getErrorProtocol(), exception.getMessage(), exception);

		ErrorProtocol errorProtocol = exception.getErrorProtocol();
		return ErrorResponse.builder(exception, HttpStatus.INTERNAL_SERVER_ERROR, "배치 작업 중 오류가 발생했습니다.")
			.detail("배치 작업 실행 중 내부 오류가 발생했습니다. 관리자에게 문의하세요.")
			.detail(errorProtocol.getInternalMessage())
			.property(MESSAGE_PROPERTY_KEY, errorProtocol.getClientMessage())
			.property("code", errorProtocol.getCustomCode())
			.build();
	}

	@ExceptionHandler(UserUpdateException.class)
	public ResponseEntity<String> handleUserUpdateException(final UserUpdateException e) {
		return new ResponseEntity<>(e.getMessage(), CONFLICT);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
	}

	@ExceptionHandler(AlarmCategoryNotFoundException.class)
	public ResponseEntity<String> handleAlarmCategoryNotFoundException(
		final AlarmCategoryNotFoundException e) {
		return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
	}

	@ExceptionHandler(AlarmFatalException.class)
	public ResponseEntity<String> handleAlarmFatalException(final AlarmFatalException e) {
		return new ResponseEntity<>(e.getMessage(), SERVICE_UNAVAILABLE);
	}
}
