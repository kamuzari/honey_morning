package com.sf.honeymorning.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
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

import com.sf.honeymorning.exception.alarm.AlarmFatalException;
import com.sf.honeymorning.exception.model.BusinessException;
import com.sf.honeymorning.exception.model.ErrorProtocol;
import com.sf.honeymorning.exception.user.AlarmCategoryNotFoundException;
import com.sf.honeymorning.exception.user.UserNotFoundException;
import com.sf.honeymorning.exception.user.UserUpdateException;

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
			.property(MESSAGE_PROPERTY_KEY, errorDetailMessage)  // 필드 오류를 추가로 포함
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

	// 트랜잭션 에러 처리
	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<String> handleTransactionSystemException(TransactionSystemException ex) {
		Throwable rootCause = ex.getRootCause();
		String errorMessage = "데이터베이스 트랜잭션 처리 중 오류가 발생했습니다.";

		if (rootCause != null) {
			errorMessage = "원인 " + rootCause.getMessage();
		}

		return new ResponseEntity<>(errorMessage, INTERNAL_SERVER_ERROR);
	}

	// 유저가 존재하지 않는 오류
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
		return new ResponseEntity<>(null, OK);
	}

	// 업데이트 오류
	@ExceptionHandler(UserUpdateException.class)
	public ResponseEntity<String> handleUserUpdateException(final UserUpdateException e) {
		return new ResponseEntity<>(e.getMessage(), CONFLICT);
	}

	// 잘못된 인자 전달
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
		return new ResponseEntity<>(ex.getMessage(), BAD_REQUEST);
	}

	// 알람 카테고리가 존재하지 않을 때 오류
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
