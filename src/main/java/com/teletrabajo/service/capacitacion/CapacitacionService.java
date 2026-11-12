package com.teletrabajo.service.capacitacion;
import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.capacitacion.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.*;
import java.util.*;
@Service
public class CapacitacionService {
 private final CapParticipantRepository participants;
 private final CapCertificateRepository certificates;
 private final CapSignatoryRepository signatories;
 private final CapCertificateDocumentRepository storedDocuments;
 private final CapacitacionDocumentsService documents;
 @Value("${capacitacion.verification-base-url:https://teletrabajo.dssm.cl/api/v1/public/capacitacion/verificar}") private String verificationBaseUrl;
 @Value("${capacitacion.official-issuance-enabled:false}") private boolean issuanceEnabled;
 @Value("${capacitacion.official-operators:}") private String officialOperators;
 public CapacitacionService(CapParticipantRepository p,CapCertificateRepository c,CapSignatoryRepository s,CapCertificateDocumentRepository sd,CapacitacionDocumentsService d){participants=p;certificates=c;signatories=s;storedDocuments=sd;documents=d;}
 public String requireOfficialOperator(){
  var a=SecurityContextHolder.getContext().getAuthentication();String name=a==null?"":a.getName();
  if(!issuanceEnabled)throw new ResponseStatusException(HttpStatus.CONFLICT,"Emision oficial no habilitada");
  if(a==null||!a.isAuthenticated()||name.isBlank()||Arrays.stream(officialOperators.split(",")).map(String::trim).noneMatch(n->n.equalsIgnoreCase(name)))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Operador no autorizado para certificados oficiales");
  return name;
 }
 @Transactional public CapCertificate issue(Long participantId){
   String operator=requireOfficialOperator();
   CapParticipant p=participants.findForIssue(participantId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Participante no encontrado"));
   var existing=certificates.findFirstByParticipantIdAndRevokedAtIsNullOrderByIdDesc(participantId);
   if(existing.isPresent())return existing.get();
   if(p.activity==null||!"APROBADO".equalsIgnoreCase(p.result)||"RETIRADO".equalsIgnoreCase(p.status)||"INSCRITO".equalsIgnoreCase(p.status)||"EN_CURSO".equalsIgnoreCase(p.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"La participacion debe estar aprobada y completada");
   Instant now=Instant.now();LocalDate date=now.atZone(ZoneId.of("America/Punta_Arenas")).toLocalDate();
   CapSignatory first=select("IZQUIERDA",date),second=select("DERECHA",date);
   if(first.id.equals(second.id))throw new ResponseStatusException(HttpStatus.CONFLICT,"Se requieren dos firmantes distintos");
   byte[] left=documents.loadSignature(first.signatureImagePath),right=documents.loadSignature(second.signatureImagePath);
   CapCertificate c=new CapCertificate();c.participant=p;c.verificationCode=UUID.randomUUID().toString();c.issuedAt=now;c.issuedBy=operator;c.status="EMITIDO";
   c.firstSignatory=first;c.secondSignatory=second;
   c.firstSignerName=first.fullName;c.firstSignerTitle=first.positionTitle;c.firstSignerPosition=first.signaturePosition;c.firstSignerAsset=first.signatureImagePath;c.firstSignerSha256=CapacitacionDocumentsService.sha256(left);
   c.secondSignerName=second.fullName;c.secondSignerTitle=second.positionTitle;c.secondSignerPosition=second.signaturePosition;c.secondSignerAsset=second.signatureImagePath;c.secondSignerSha256=CapacitacionDocumentsService.sha256(right);
   c=certificates.saveAndFlush(c);c.certificateNumber=String.format(Locale.ROOT,"CAP-%d-%08d",date.getYear(),c.id);c.documentReference="db:cap_certificate_documents/"+c.id;
   byte[] pdf=documents.render(c,left,right,verificationBaseUrl);c.pdfSha256=CapacitacionDocumentsService.sha256(pdf);
   CapCertificateDocument stored=new CapCertificateDocument();stored.certificateId=c.id;stored.pdfBytes=pdf;storedDocuments.save(stored);
   return certificates.save(c);
 }
 private CapSignatory select(String position,LocalDate date){
  var list=signatories.findAll().stream().filter(s->Boolean.TRUE.equals(s.active)&&position.equals(s.signaturePosition)&&s.validFrom!=null&&!date.isBefore(s.validFrom)&&(s.validTo==null||!date.isAfter(s.validTo))&&s.fullName!=null&&!s.fullName.isBlank()&&s.positionTitle!=null&&!s.positionTitle.isBlank()).toList();
  if(list.size()!=1)throw new ResponseStatusException(HttpStatus.CONFLICT,"Se requiere exactamente un firmante vigente en posicion "+position);return list.get(0);
 }
 @Transactional public CapCertificate revoke(Long id){String operator=requireOfficialOperator();CapCertificate c=certificates.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(c.revokedAt==null){c.revokedAt=Instant.now();c.revokedBy=operator;c.status="REVOCADO";}return certificates.save(c);}
 public Map<String,Object> verify(String code){
  return certificates.findByVerificationCode(code).map(c->{Map<String,Object> m=new LinkedHashMap<>();m.put("valid",c.revokedAt==null&&c.pdfSha256!=null&&storedDocuments.existsById(c.id));m.put("code",c.verificationCode);m.put("issuedAt",c.issuedAt);m.put("status",c.revokedAt!=null?"REVOCADO":c.pdfSha256!=null?"EMITIDO":"SIN_DOCUMENTO");m.put("certificateNumber",c.certificateNumber);m.put("pdfSha256",c.pdfSha256);return m;}).orElseGet(()->Map.of("valid",false));
 }
}
