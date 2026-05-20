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
public interface WellbeingFamilyIncomeRepository extends JpaRepository<WellbeingFamilyIncomeEntity, Long> {

    List<WellbeingFamilyIncomeEntity> findByPostulationIdOrderByIdAsc(Long postulationId);
    @Query("SELECT COALESCE(SUM(i.amount), 0) FROM WellbeingFamilyIncomeEntity i WHERE i.postulation.id = :postulationId")
    BigDecimal sumByPostulationId(@Param("postulationId") Long postulationId);

}
