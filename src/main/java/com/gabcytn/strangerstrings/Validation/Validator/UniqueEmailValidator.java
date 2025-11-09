package com.gabcytn.strangerstrings.Validation.Validator;

import com.gabcytn.strangerstrings.DAO.UserDao;
import com.gabcytn.strangerstrings.Validation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
  private final UserDao userDAO;

  public UniqueEmailValidator(UserDao userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
    if (email == null) {
      return false;
    }
    return userDAO.findByEmail(email).isEmpty();
  }
}
