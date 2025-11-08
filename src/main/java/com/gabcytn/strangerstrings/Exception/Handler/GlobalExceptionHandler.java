package com.gabcytn.strangerstrings.Exception.Handler;

import com.gabcytn.strangerstrings.Exception.DuplicateUserUniqueConstraintException;
import com.gabcytn.strangerstrings.Exception.RefreshTokenException;
import com.gabcytn.strangerstrings.Exception.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.List;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleSecurityException(Exception exception) {
    ProblemDetail errorDetail = null;
    exception.printStackTrace();

    if (exception instanceof BadCredentialsException) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
      errorDetail.setProperty("description", "The username or password is incorrect");

      return errorDetail;
    }

    if (exception instanceof AccountStatusException) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The account is locked");
    }

    if (exception instanceof AccessDeniedException) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "You are not authorized to access this resource");
    }

    if (exception instanceof SignatureException) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The JWT signature is invalid");
    }

    if (exception instanceof ExpiredJwtException) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The JWT token has expired");
    }

    if (exception instanceof MalformedJwtException) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), exception.getMessage());
      errorDetail.setProperty("description", "Incorrect token format.");
    }

    if (exception instanceof UserNotFoundException) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
      errorDetail.setProperty("description", "Failed to authenticate.");
    }

    if (exception instanceof DuplicateUserUniqueConstraintException) {
      errorDetail =
              ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
      errorDetail.setProperty("description", "Username and/or email already exists.");
    }

    if (exception instanceof RefreshTokenException) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "Your refresh token is invalid.");
    }

    if (exception instanceof MethodArgumentNotValidException) {
      List<String> errors =
          ((MethodArgumentNotValidException) exception)
              .getBindingResult().getAllErrors().stream()
                  .map(DefaultMessageSourceResolvable::getDefaultMessage)
                  .toList();

      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed.");
      errorDetail.setProperty("description", errors);
    }

    if (exception instanceof HttpMessageNotReadableException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage());
      errorDetail.setProperty("description", "Include a request body.");
    }

    if (errorDetail == null) {
      errorDetail =
          ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
      errorDetail.setProperty("description", "Unknown internal server error.");
    }

    return errorDetail;
  }
}
