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
public interface WellbeingPostulationRepository extends JpaRepository<WellbeingPostulationEntity, Long> {

    Optional<WellbeingPostulationEntity> findByCode(String code);

    @Query("""
        SELECT p FROM WellbeingPostulationEntity p
        WHERE (:userId IS NULL OR p.user.id = :userId)
          AND (:periodYear IS NULL OR p.periodYear = :periodYear)
          AND (:status IS NULL OR p.status = :status)
        ORDER BY p.id DESC
    """)
    Page<WellbeingPostulationEntity> search(@Param("userId") Integer userId,
                                             @Param("periodYear") Integer periodYear,
                                             @Param("status") BienestarEnums.PostulationStatus status,
                                             Pageable pageable);

    List<WellbeingPostulationEntity> findByUserIdAndStatusAndDeletedAtIsNullOrderByUpdatedAtDescCreatedAtDesc(
            Integer userId,
            BienestarEnums.PostulationStatus status
    );


    List<WellbeingPostulationEntity> findByUserIdAndStatusAndDeletedAtIsNullOrderByCurrentStepDescUpdatedAtDescCreatedAtDesc(
            Integer userId,
            BienestarEnums.PostulationStatus status
    );

    List<WellbeingPostulationEntity> findByUserIdAndStatusInAndDeletedAtIsNullOrderByUpdatedAtDescCreatedAtDesc(
            Integer userId,
            List<BienestarEnums.PostulationStatus> statuses
    );

    Optional<WellbeingPostulationEntity> findByIdAndUserIdAndDeletedAtIsNull(
            Long id,
            Integer userId
    );

    Optional<WellbeingPostulationEntity> findFirstByUserIdAndPeriodYearAndStatusAndDeletedAtIsNullOrderByCurrentStepDescUpdatedAtDescCreatedAtDesc(
            Integer userId,
            Integer periodYear,
            BienestarEnums.PostulationStatus status
    );

    @Query(value = "SELECT * FROM wellbeing_postulations WHERE id = :id", nativeQuery = true)
    Optional<WellbeingPostulationEntity> findAnyByIdIncludingDeleted(@Param("id") Long id);

    @Query(value = "SELECT * FROM wellbeing_postulations WHERE deleted_at IS NOT NULL ORDER BY deleted_at DESC, id DESC", nativeQuery = true)
    List<WellbeingPostulationEntity> findAllDeletedIncludingSoftDeleted();

}
