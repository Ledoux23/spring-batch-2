package com.spring_batch_2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spring_batch_2.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}