package com.hhgs.Attendances.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.hhgs.Attendances.model.Employee;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    boolean existsByEmail(String email);

	Optional<Employee> findByEmail(String email);
}
