package com.gabcytn.shortnotice.Validation.Validator;

import com.gabcytn.shortnotice.DAO.UserDAO;
import com.gabcytn.shortnotice.Validation.UniqueEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String>
{
	private final UserDAO userDAO;

	public UniqueEmailValidator(UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	@Override
	public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
		return userDAO.findByEmail(email).isEmpty();
	}
}
