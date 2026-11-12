package com.teletrabajo.repository.capacitacion;
import com.teletrabajo.entity.capacitacion.CapCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CapCertificateRepository extends JpaRepository<CapCertificate,Long> {
 Optional<CapCertificate> findByVerificationCode(String code);
 Optional<CapCertificate> findFirstByParticipantIdAndRevokedAtIsNullOrderByIdDesc(Long participantId);
}
