package com.gabcytn.shortnotice.Service;

import com.gabcytn.shortnotice.DAO.UserDAO;
import com.gabcytn.shortnotice.DTO.UserRegisterDTO;
import com.gabcytn.shortnotice.Entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  private final UserDAO userDAO;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationService(UserDAO userDAO, PasswordEncoder passwordEncoder) {
    this.userDAO = userDAO;
    this.passwordEncoder = passwordEncoder;
  }

  public void register(UserRegisterDTO userRegisterDTO) throws Exception {
    try {
      User user = new User();
      user.setEmail(userRegisterDTO.getEmail());
      user.setUsername(userRegisterDTO.getUsername());
      user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
      userDAO.save(user);
    } catch (Exception e) {
      throw new Exception(e);
    }
  }
}
