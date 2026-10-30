package com.teletrabajo.repository.acreditacion;
import com.teletrabajo.entity.acreditacion.AcrRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AcrRequirementRepository extends JpaRepository<AcrRequirement,Long> {
  java.util.List<AcrRequirement> findByProcessId(Long processId);
}
