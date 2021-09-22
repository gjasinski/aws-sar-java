package com.gjasinski.awssarjava.repositories;

import com.gjasinski.awssarjava.entity.SarSubFunction;
import com.gjasinski.awssarjava.entity.SarSubFunctionEventDetected;
import org.hibernate.query.criteria.internal.expression.function.SubstringFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SarSubfunctionEventRepository extends JpaRepository<SarSubFunctionEventDetected, Integer> {
    List<SarSubFunctionEventDetected> findBySubFunction(SarSubFunction subFunction);
}
