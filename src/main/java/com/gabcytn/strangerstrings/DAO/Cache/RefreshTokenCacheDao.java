package com.gabcytn.strangerstrings.DAO.Cache;

import com.gabcytn.strangerstrings.Model.RefreshTokenValidator;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenCacheDao extends CrudRepository<RefreshTokenValidator, String> {}
