package com.teletrabajo.repository.capacitacion;
import com.teletrabajo.entity.capacitacion.CapActivity;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CapActivityRepository extends JpaRepository<CapActivity,Long> {
 boolean existsByCodeAndExecutionNumber(String code,Integer executionNumber);
 boolean existsByCodeAndExecutionNumberAndIdNot(String code,Integer executionNumber,Long id);
}
