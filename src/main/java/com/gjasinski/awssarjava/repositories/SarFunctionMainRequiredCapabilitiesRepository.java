package com.gjasinski.awssarjava.repositories;

import com.gjasinski.awssarjava.entity.SarFunctionMainRequiredCapabilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SarFunctionMainRequiredCapabilitiesRepository extends JpaRepository<SarFunctionMainRequiredCapabilities, Integer> {
}
