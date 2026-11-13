package com.teletrabajo.repository.acreditacion;
import com.teletrabajo.entity.acreditacion.AcrProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface AcrProcessRepository extends JpaRepository<AcrProcess,Long> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select p from AcrProcess p where p.id = :id")
  Optional<AcrProcess> findForUpdate(@Param("id") Long id);
}
