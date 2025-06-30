package com.hhgs.Attendances.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hhgs.Attendances.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	 Optional<User> findByUsername(String username);
	    Optional<User> findByEmail(String email);
	    Optional<User> findByEmpId(String empId);

	    boolean existsByUsername(String username);
	    boolean existsByEmail(String email);
	    boolean existsByEmpId(String empId);

	    List<User> findByRole(String role);
}
