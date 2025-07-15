package com.gabcytn.shortnotice.Validation.Validator;

import com.gabcytn.shortnotice.DTO.RegisterRequestDto;
import com.gabcytn.shortnotice.Validation.PasswordsMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {
  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
    if (obj instanceof RegisterRequestDto dto)
      return dto.getPassword().equals(dto.getConfirmPassword());

    return false;
  }
}
