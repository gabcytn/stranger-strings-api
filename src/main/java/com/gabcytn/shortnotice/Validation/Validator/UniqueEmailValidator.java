package com.gabcytn.shortnotice.Validation.Validator;

import com.gabcytn.shortnotice.DAO.UserDao;
import com.gabcytn.shortnotice.Validation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
  private final UserDao userDAO;

  public UniqueEmailValidator(UserDao userDAO) {
    this.userDAO = userDAO;
  }

  @Override
  public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
    return userDAO.findByEmail(email).isEmpty();
  }
}
