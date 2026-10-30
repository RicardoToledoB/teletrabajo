package com.teletrabajo.repository.acreditacion;
import com.teletrabajo.entity.acreditacion.AcrApplication;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AcrApplicationRepository extends JpaRepository<AcrApplication,Long> {
  java.util.List<AcrApplication> findByApplicantUserId(Integer applicantUserId);
  boolean existsByProcessIdAndApplicantUserIdAndModalityAndStatusNot(Long processId,Integer applicantUserId,String modality,String status);
}
