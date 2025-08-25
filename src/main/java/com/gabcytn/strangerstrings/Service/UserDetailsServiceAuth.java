package com.gabcytn.strangerstrings.Service;

import com.gabcytn.strangerstrings.DAO.UserDao;
import com.gabcytn.strangerstrings.DTO.UserPrincipal;
import com.gabcytn.strangerstrings.Entity.User;
import com.gabcytn.strangerstrings.Exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceAuth implements UserDetailsService {
  private final UserDao userDao;

  public UserDetailsServiceAuth(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userDao.findByUsername(username).orElseThrow(UserNotFoundException::new);
    return new UserPrincipal(user);
  }
}
