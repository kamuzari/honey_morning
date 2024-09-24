package com.sf.honeymorning.exception;

import com.sf.honeymorning.exception.user.AlarmCategoryNotFoundException;
import com.sf.honeymorning.exception.user.DuplicateException;
import com.sf.honeymorning.exception.user.UserNotFoundException;
import com.sf.honeymorning.exception.user.UserUpdateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // @Valid, @Validated 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    // 트랜잭션 에러 처리
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<String> handleTransactionSystemException(TransactionSystemException ex) {
        Throwable rootCause = ex.getRootCause();
        String errorMessage = "데이터베이스 트랜잭션 처리 중 오류가 발생했습니다.";

        if (rootCause != null) {
            errorMessage = "원인 " + rootCause.getMessage();
        }

        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 유저가 존재하지 않는 오류
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 중복값 오류
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<String> handleDuplicateException(final DuplicateException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    // 업데이트 오류
    @ExceptionHandler(UserUpdateException.class)
    public ResponseEntity<String> handleUserUpdateException(final UserUpdateException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    // 잘못된 인자 전달
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 알람 카테고리가 존재하지 않을 때 오류
    @ExceptionHandler(AlarmCategoryNotFoundException.class)
    public ResponseEntity<String> handleAlarmCategoryNotFoundException(final AlarmCategoryNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 그 외 모든 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
