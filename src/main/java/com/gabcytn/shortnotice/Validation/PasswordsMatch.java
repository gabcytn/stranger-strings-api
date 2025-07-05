package com.gabcytn.shortnotice.Validation;

import com.gabcytn.shortnotice.Validation.Validator.PasswordsMatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Constraint(validatedBy = PasswordsMatchValidator.class)
public @interface PasswordsMatch
{
	String message () default "Passwords do not match.";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
