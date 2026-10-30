package com.teletrabajo.service.capacitacion;
import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.capacitacion.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.Instant;
import java.util.UUID;
@Service
public class CapacitacionService {
 private final CapParticipantRepository participants;
 private final CapCertificateRepository certificates;
 public CapacitacionService(CapParticipantRepository p,CapCertificateRepository c){participants=p;certificates=c;}
 @Transactional public CapCertificate issue(Long participantId){
   CapParticipant p=participants.findById(participantId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Participante no encontrado"));
   CapCertificate c=new CapCertificate();c.participant=p;c.verificationCode=UUID.randomUUID().toString();c.issuedAt=Instant.now();return certificates.save(c);
 }
 @Transactional public CapCertificate revoke(Long id){CapCertificate c=certificates.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));c.revokedAt=Instant.now();return certificates.save(c);}
 public java.util.Map<String,Object> verify(String code){
  return certificates.findAll().stream().filter(c->code.equals(c.verificationCode)).findFirst()
  .map(c->java.util.Map.<String,Object>of("valid",c.revokedAt==null,"code",c.verificationCode,"issuedAt",c.issuedAt.toString()))
  .orElse(java.util.Map.of("valid",false));
 }
}
