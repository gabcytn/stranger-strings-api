package com.gabcytn.shortnotice.Exception.Handler;

import com.gabcytn.shortnotice.DTO.ValidationErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class ValidationExceptionsHandler
{
	@ExceptionHandler({MethodArgumentNotValidException.class})
	public ResponseEntity<ValidationErrorResponseDTO> handle(MethodArgumentNotValidException exception) {
		// get all error messages in the validation
		List<String> errors = new ArrayList<>();
		errors.addAll(
						exception.
										getBindingResult()
										.getFieldErrors()
										.stream()
										.map(FieldError::getDefaultMessage)
										.toList());
		errors.addAll(
						exception
										.getBindingResult()
										.getGlobalErrors()
										.stream()
										.map(ObjectError::getDefaultMessage)
										.toList());

		// create a DTO of response errors/messages
		ValidationErrorResponseDTO errorResponseDTO = new ValidationErrorResponseDTO(errors);
		return new ResponseEntity<>(errorResponseDTO, HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
