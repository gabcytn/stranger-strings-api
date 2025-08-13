package com.gabcytn.strangerstrings.Validation;

import com.gabcytn.strangerstrings.Validation.Validator.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
  String message() default "Email already exists.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
