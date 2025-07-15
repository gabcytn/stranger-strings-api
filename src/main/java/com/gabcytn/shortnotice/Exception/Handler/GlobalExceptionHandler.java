package com.gabcytn.shortnotice.Exception.Handler;

import com.gabcytn.shortnotice.Exception.AuthenticationException;
import com.gabcytn.shortnotice.Exception.RefreshTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
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

		if (exception instanceof AuthenticationException) {
			errorDetail =
							ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
			errorDetail.setProperty("description", "Failed to authenticate.");
		}

		if (exception instanceof RefreshTokenException) {
			errorDetail =
							ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
			errorDetail.setProperty("description", "Your refresh token is invalid.");
		}

		if (errorDetail == null) {
			errorDetail =
							ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
			errorDetail.setProperty("description", "Unknown internal server error.");
		}

		return errorDetail;
	}
}
