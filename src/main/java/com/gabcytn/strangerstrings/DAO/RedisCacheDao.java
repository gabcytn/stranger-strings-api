package com.gabcytn.strangerstrings.DAO;

import com.gabcytn.strangerstrings.DTO.CacheData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisCacheDao extends CrudRepository<CacheData, String> {}
