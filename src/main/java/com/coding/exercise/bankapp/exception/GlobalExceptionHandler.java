package com.coding.exercise.bankapp.exception;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<Object> handleCustomerNotFound(CustomerNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(AccountNotFoundException.class)
	public ResponseEntity<Object> handleAccountNotFound(AccountNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(InsufficientFundsException.class)
	public ResponseEntity<Object> handleInsufficientFunds(InsufficientFundsException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {

		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", status.value());
		body.put("error", "Validation Failed");

		List<Map<String, String>> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(fieldError -> {
					Map<String, String> error = new LinkedHashMap<>();
					error.put("field", fieldError.getField());
					error.put("message", fieldError.getDefaultMessage());
					return error;
				})
				.collect(Collectors.toList());

		body.put("errors", fieldErrors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
	}
}
