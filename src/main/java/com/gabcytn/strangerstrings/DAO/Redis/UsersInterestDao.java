package com.gabcytn.strangerstrings.DAO.Redis;

import com.gabcytn.strangerstrings.Model.UsersInterest;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersInterestDao extends CrudRepository<UsersInterest, UUID> {}
