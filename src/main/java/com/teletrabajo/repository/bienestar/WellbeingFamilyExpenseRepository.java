package com.teletrabajo.repository.bienestar;

import com.teletrabajo.entity.bienestar.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface WellbeingFamilyExpenseRepository extends JpaRepository<WellbeingFamilyExpenseEntity, Long> {

    List<WellbeingFamilyExpenseEntity> findByPostulationIdOrderByIdAsc(Long postulationId);
    List<WellbeingFamilyExpenseEntity> findByPostulationIdAndCategory(Long postulationId, BienestarEnums.ExpenseCategory category);
    @Query("SELECT COALESCE(SUM(e.amount), 0) FROM WellbeingFamilyExpenseEntity e WHERE e.postulation.id = :postulationId AND e.category = :category")
    BigDecimal sumByPostulationIdAndCategory(@Param("postulationId") Long postulationId, @Param("category") BienestarEnums.ExpenseCategory category);

}
