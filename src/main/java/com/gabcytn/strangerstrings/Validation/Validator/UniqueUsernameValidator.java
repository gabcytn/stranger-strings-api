package com.gabcytn.strangerstrings.Validation.Validator;

import com.gabcytn.strangerstrings.DAO.UserDao;
import com.gabcytn.strangerstrings.Validation.UniqueUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
  private final UserDao userDAO;

  public UniqueUsernameValidator(UserDao userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
    return userDAO.findByUsername(username).isEmpty();
  }
}
