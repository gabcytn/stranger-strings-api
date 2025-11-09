package com.gabcytn.strangerstrings.Validation.Validator;

import com.gabcytn.strangerstrings.DTO.RegisterRequestDto;
import com.gabcytn.strangerstrings.Validation.PasswordsMatch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, Object> {
  @Override
  public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
    if (obj instanceof RegisterRequestDto dto) {
      if (dto.getPassword() == null || dto.getConfirmPassword() == null) {
        return false;
      }
      return dto.getPassword().equals(dto.getConfirmPassword());
    }

    return false;
  }
}
