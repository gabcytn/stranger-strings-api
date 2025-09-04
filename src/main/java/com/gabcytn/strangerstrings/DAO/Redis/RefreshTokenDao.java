package com.gabcytn.strangerstrings.DAO.Redis;

import com.gabcytn.strangerstrings.Model.RefreshTokenValidator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenDao extends CrudRepository<RefreshTokenValidator, String> {}
