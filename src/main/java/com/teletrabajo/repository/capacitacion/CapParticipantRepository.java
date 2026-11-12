package com.teletrabajo.repository.capacitacion;
import com.teletrabajo.entity.capacitacion.CapParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface CapParticipantRepository extends JpaRepository<CapParticipant,Long> {
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("select p from CapParticipant p where p.id = :id")
 Optional<CapParticipant> findForIssue(@Param("id") Long id);
}
