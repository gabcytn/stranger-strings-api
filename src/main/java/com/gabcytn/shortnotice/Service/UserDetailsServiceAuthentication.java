package com.gabcytn.shortnotice.Service;

import com.gabcytn.shortnotice.DAO.UserDAO;
import com.gabcytn.shortnotice.DTO.UserPrincipal;
import com.gabcytn.shortnotice.Entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceAuthentication implements UserDetailsService
{
	private final UserDAO userDAO;

	public UserDetailsServiceAuthentication (UserDAO userDAO)
	{
		this.userDAO = userDAO;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		Optional<User> user = userDAO.findByUsername(username);
		if (user.isPresent()) {
			return new UserPrincipal(user.get());
		}

		System.err.println("The username '" + username + "' was not found!");
		throw new UsernameNotFoundException("Username not found.");
	}
}
