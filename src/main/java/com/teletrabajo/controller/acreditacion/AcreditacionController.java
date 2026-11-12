package com.teletrabajo.controller.acreditacion;
import com.teletrabajo.entity.acreditacion.*;
import com.teletrabajo.repository.acreditacion.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/acreditacion")
public class AcreditacionController {
 private final AcrProcessRepository processes;
 private final AcrApplicationRepository applications;
 private final AcrRequirementRepository requirements;
 private final AcrAuditEventRepository audits;
 private final com.teletrabajo.repository.UserRepository users;
 public AcreditacionController(AcrProcessRepository p,AcrApplicationRepository a,AcrRequirementRepository r,AcrAuditEventRepository e,com.teletrabajo.repository.UserRepository u){processes=p;applications=a;requirements=r;audits=e;users=u;}
 private Integer uid(Authentication auth){
  if(auth==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  String principal=auth.getName();
  return users.findByEmailIgnoreCase(principal).or(()->users.findByUsername(principal)).orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN,"Identidad no encontrada")).getId();
 }
 private void audit(String action,Long process,Long application,Integer actor){AcrAuditEvent e=new AcrAuditEvent();e.action=action;e.processId=process;e.applicationId=application;e.actorUserId=actor;e.occurredAt=LocalDateTime.now();audits.save(e);}
 @GetMapping("/convocatorias") public List<AcrProcess> list(){return processes.findAll();}
 @PostMapping("/convocatorias") public AcrProcess create(@RequestBody AcrProcess request,Authentication auth){
  if(request.year==null||request.title==null||request.title.isBlank()||request.opensAt==null||request.closesAt==null||!request.closesAt.isAfter(request.opensAt))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Convocatoria o fechas inválidas");
  AcrProcess p=new AcrProcess();p.year=request.year;p.title=request.title;p.legalBasis=request.legalBasis;p.opensAt=request.opensAt;p.closesAt=request.closesAt;p.status="PROGRAMADA";p.createdBy=uid(auth);p=processes.save(p);audit("CREAR_CONVOCATORIA",p.id,null,p.createdBy);return p;
 }
 @GetMapping("/convocatorias/{id}/requisitos") public List<AcrRequirement> req(@PathVariable Long id){if(!processes.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND);return requirements.findByProcessId(id);}
 @PostMapping("/convocatorias/{id}/requisitos") public AcrRequirement addReq(@PathVariable Long id,@RequestBody AcrRequirement req,Authentication auth){
  AcrProcess p=processes.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(!"PROGRAMADA".equals(p.status))throw new ResponseStatusException(HttpStatus.CONFLICT,"Convocatoria no editable");
  AcrRequirement r=new AcrRequirement();r.processId=id;r.modality=req.modality;r.professionCode=req.professionCode;r.requirementKey=req.requirementKey;r.title=req.title;r.required=req.required;r.orderIndex=req.orderIndex;r.validFrom=req.validFrom;r.validTo=req.validTo;r=requirements.save(r);audit("CREAR_REQUISITO",id,null,uid(auth));return r;
 }
 @GetMapping("/mis-postulaciones") public List<AcrApplication> mine(Authentication auth){return applications.findByApplicantUserId(uid(auth));}
 @PostMapping("/convocatorias/{id}/postulaciones") public AcrApplication draft(@PathVariable Long id,@RequestBody AcrApplication request,Authentication auth){
  AcrProcess p=processes.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));LocalDateTime now=LocalDateTime.now();
  if(!"ABIERTA".equals(p.status)||now.isBefore(p.opensAt)||!now.isBefore(p.closesAt))throw new ResponseStatusException(HttpStatus.CONFLICT,"Convocatoria fuera de plazo");
  if(!Set.of("OBLIGACION","EXCELENCIA").contains(request.modality)||request.professionCode==null||request.professionCode.isBlank())throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Modalidad o profesión inválida");
  Integer actor=uid(auth);if(applications.existsByProcessIdAndApplicantUserIdAndModalityAndStatusNot(id,actor,request.modality,"ANULADA"))throw new ResponseStatusException(HttpStatus.CONFLICT,"Postulación ya registrada");
  AcrApplication a=new AcrApplication();a.processId=id;a.applicantUserId=actor;a.professionCode=request.professionCode;a.modality=request.modality;a.status="BORRADOR";a.eligibilityStatus="PENDIENTE";a.trackingCode="ACR-"+p.year+"-"+UUID.randomUUID().toString().substring(0,12).toUpperCase(Locale.ROOT);a=applications.save(a);audit("CREAR_BORRADOR",id,a.id,actor);return a;
 }
 @GetMapping("/postulaciones/{id}") public AcrApplication getMine(@PathVariable Long id,Authentication auth){AcrApplication a=applications.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(!a.applicantUserId.equals(uid(auth)))throw new ResponseStatusException(HttpStatus.FORBIDDEN);return a;}
}
