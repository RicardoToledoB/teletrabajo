package com.teletrabajo.repository.acreditacion;
import com.teletrabajo.entity.acreditacion.AcrCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AcrCertificateRepository extends JpaRepository<AcrCertificate,Long> {
  java.util.Optional<AcrCertificate> findByVerificationToken(String verificationToken);
}
