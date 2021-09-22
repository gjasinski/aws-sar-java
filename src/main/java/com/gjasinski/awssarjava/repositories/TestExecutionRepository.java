package com.gjasinski.awssarjava.repositories;

import com.gjasinski.awssarjava.entity.ExecutionResult;
import com.gjasinski.awssarjava.entity.TestExecution;
import org.aspectj.weaver.ast.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestExecutionRepository extends JpaRepository<TestExecution, Long> {
    Optional<TestExecution> findById(Long id);
}
