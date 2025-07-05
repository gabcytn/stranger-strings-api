package com.gabcytn.shortnotice.DAO;

import com.gabcytn.shortnotice.Entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDAO extends CrudRepository<User, UUID>
{
	Optional<User> findByUsername(String username);
	Optional<User> findByEmail(String email);
}
