package com.gabcytn.strangerstrings.Validation;

import com.gabcytn.strangerstrings.Validation.Validator.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = UniqueUsernameValidator.class)
public @interface UniqueUsername {
  String message() default "Username already exists.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
