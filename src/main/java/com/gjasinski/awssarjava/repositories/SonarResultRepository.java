package com.gjasinski.awssarjava.repositories;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarSubFunction;
import com.gjasinski.awssarjava.entity.SarSubFunctionEventDetected;
import com.gjasinski.awssarjava.entity.SonarResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SonarResultRepository extends JpaRepository<SonarResult, Long> {
    List<SonarResult> findByFunctionMain(SarFunctionMain f);
}
