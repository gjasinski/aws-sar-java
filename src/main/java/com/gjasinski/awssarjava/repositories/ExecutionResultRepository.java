package com.gjasinski.awssarjava.repositories;

import com.gjasinski.awssarjava.entity.ExecutionResult;
import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarSubFunction;
import com.gjasinski.awssarjava.entity.TestExecution;
import org.aspectj.weaver.ast.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExecutionResultRepository extends JpaRepository<ExecutionResult, Long> {
    List<ExecutionResult> findAllBySubFunctionInAndTestExecution(List<SarSubFunction> subFunctions, TestExecution testExecution);
    List<ExecutionResult> findAllBySubFunctionAndTestExecution(SarSubFunction subFunction, TestExecution testExecution);
    List<ExecutionResult> findAllByTestExecution(TestExecution testExecution);
}
