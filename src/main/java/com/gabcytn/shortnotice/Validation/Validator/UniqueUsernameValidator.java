package com.gabcytn.shortnotice.Validation.Validator;

import com.gabcytn.shortnotice.DAO.UserDAO;
import com.gabcytn.shortnotice.Validation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
  private final UserDAO userDAO;

  public UniqueUsernameValidator(UserDAO userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
    return userDAO.findByUsername(username).isEmpty();
  }
}
