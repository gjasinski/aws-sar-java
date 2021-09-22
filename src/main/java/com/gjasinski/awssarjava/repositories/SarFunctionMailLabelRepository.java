package com.gjasinski.awssarjava.repositories;

import com.gjasinski.awssarjava.entity.SarFunctionMainLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SarFunctionMailLabelRepository extends JpaRepository<SarFunctionMainLabel, Integer> {
}
