package com.teletrabajo.repository.acreditacion;
import com.teletrabajo.entity.acreditacion.AcrAuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AcrAuditEventRepository extends JpaRepository<AcrAuditEvent,Long> {
}
