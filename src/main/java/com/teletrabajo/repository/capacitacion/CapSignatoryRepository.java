package com.teletrabajo.repository.capacitacion;
import com.teletrabajo.entity.capacitacion.CapSignatory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface CapSignatoryRepository extends JpaRepository<CapSignatory,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select s from CapSignatory s where s.id = :id")
 Optional<CapSignatory> findForUpdate(@Param("id") Long id);
}
