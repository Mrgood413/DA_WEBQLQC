package com.example.WebCafe.controller;

import com.example.WebCafe.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fe.getField(), fe.getDefaultMessage());
		}
		return ResponseEntity.badRequest().body(new ErrorResponse(
				"VALIDATION_ERROR",
				"Dữ liệu không hợp lệ",
				fieldErrors));
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(
				"UNAUTHORIZED",
				ex.getMessage() != null ? ex.getMessage() : "Đăng nhập thất bại",
				null));
	}

	@ExceptionHandler(UnsupportedOperationException.class)
	public ResponseEntity<ErrorResponse> handleNotImplemented(UnsupportedOperationException ex) {
		return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ErrorResponse(
				"NOT_IMPLEMENTED",
				ex.getMessage() != null ? ex.getMessage() : "Chức năng chưa triển khai",
				null));
	}
}
