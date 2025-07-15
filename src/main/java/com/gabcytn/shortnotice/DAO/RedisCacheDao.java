package com.gabcytn.shortnotice.DAO;

import com.gabcytn.shortnotice.DTO.CacheData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisCacheDao extends CrudRepository<CacheData, String> {}
