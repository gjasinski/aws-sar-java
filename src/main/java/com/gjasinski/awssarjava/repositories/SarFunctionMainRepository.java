package com.gjasinski.awssarjava.repositories;

import com.gjasinski.awssarjava.entity.SarFunctionMain;
import com.gjasinski.awssarjava.entity.SarSubFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SarFunctionMainRepository extends JpaRepository<SarFunctionMain, Integer> {
    boolean existsByExternalId(String externalId);

    Optional<SarFunctionMain> findByExternalId(String externalId);

    List<SarFunctionMain> getAllByDeploymentCountIsGreaterThanAndHomePageUrlContains(Integer moreDeploymentsThan, String contains);

    List<SarFunctionMain> getAllByDeploymentCountIsGreaterThanAndHomePageUrlContainsOrderByIdAsc(Integer moreDeploymentsThan, String contains);
    List<SarFunctionMain> getAllByDeploymentCountIsGreaterThan(int i);

    @Query(value = "select count(*) from execution_result where test_execution_id = :executionId", nativeQuery = true)
    Long countAllExecutions(int executionId);

    @Query(value = "select count(*) from execution_result where test_execution_id = :executionId and execution_result_result = true", nativeQuery = true)
    Long countAllSuccessfulExecutions(int executionId);

    @Query(value = "select distinct sub_function_id from execution_result where test_execution_id = :executionId and execution_result_result = true", nativeQuery = true)
    List<Long> getAllSucessfulFunctions(int executionId);

    @Query(value = "select distinct sub_function_id from execution_result where test_execution_id = :executionId and sub_function_id not in (select distinct sub_function_id from execution_result where test_execution_id = :executionId and execution_result_result = true)", nativeQuery = true)
    List<Long> getAllNotSucessfulFunctions(int executionId);

    @Query(value = "select is_verified_author from public.sar_function_main where id = (select mian_function_id from sar_sub_functions where sub_function_id = :subFunctionId)", nativeQuery = true)
    Boolean isSubFunctionOfVerifiedAuthor(Long subFunctionId);

    @Query(value = "select * from public.sar_function_main where id = (select mian_function_id from sar_sub_functions where sub_function_id = :subFunctionId)", nativeQuery = true)
    SarFunctionMain findBySubFunction(Long subFunctionId);
}
