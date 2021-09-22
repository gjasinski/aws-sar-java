package com.gjasinski.awssarjava.repositories;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarSubFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SarSubfunctionRepository extends JpaRepository<SarSubFunction, Long> {
    Optional<SarSubFunction> findByPath(String path);

    List<SarSubFunction> findByFunctionMain(SarFunctionMain functionMain);


}
