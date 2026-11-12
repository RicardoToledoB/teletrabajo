package com.teletrabajo.controller.capacitacion;
import com.teletrabajo.dto.capacitacion.*;
import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.capacitacion.*;
import com.teletrabajo.repository.UserRepository;
import com.teletrabajo.service.capacitacion.CapacitacionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api/v1/capacitacion")
public class CapacitacionController {
 private final CapActivityRepository activities; private final CapParticipantRepository participants;
 private final CapSignatoryRepository signatories; private final CapCertificateRepository certificates;
 private final CapacitacionService service; private final UserRepository users;
 public CapacitacionController(CapActivityRepository a,CapParticipantRepository p,CapSignatoryRepository s,CapCertificateRepository c,CapacitacionService service,UserRepository users){activities=a;participants=p;signatories=s;certificates=c;this.service=service;this.users=users;}
 @GetMapping("/actividades") public List<CapActivity> activities(){return activities.findAll();}
 @PostMapping("/actividades") public CapActivity createActivity(@Valid @RequestBody CapActivityRequest a){return saveActivity(new CapActivity(),a);}
 @PutMapping("/actividades/{id}") public CapActivity updateActivity(@PathVariable Long id,@Valid @RequestBody CapActivityRequest a){return saveActivity(activities.findById(id).orElseThrow(()->missing("Actividad")),a);}
 private CapActivity saveActivity(CapActivity a,CapActivityRequest q){
  if(q.startDate()!=null&&q.endDate()!=null&&q.endDate().isBefore(q.startDate()))throw bad("Fechas de actividad invalidas");
  if(q.executionNumber()!=null){
   if(q.code()==null||q.code().isBlank())throw bad("Se requiere codigo para identificar la ejecucion");
   boolean duplicate=a.id==null?activities.existsByCodeAndExecutionNumber(q.code(),q.executionNumber()):activities.existsByCodeAndExecutionNumberAndIdNot(q.code(),q.executionNumber(),a.id);
   if(duplicate)throw new ResponseStatusException(HttpStatus.CONFLICT,"La ejecucion ya existe para el codigo");
  }
  a.title=q.title();a.code=q.code();a.provider=q.provider();a.startDate=q.startDate();a.endDate=q.endDate();a.hours=q.hours();a.active=q.active()==null?Boolean.TRUE:q.active();
  a.modality=choice(q.modality(),"PRESENCIAL","VIRTUAL","MIXTA");a.executionNumber=q.executionNumber();return activities.save(a);
 }
 @GetMapping("/participantes") public List<CapParticipant> participants(){return participants.findAll();}
 @PostMapping("/participantes") public CapParticipant createParticipant(@Valid @RequestBody CapParticipantRequest p){return saveParticipant(new CapParticipant(),p);}
 @PutMapping("/participantes/{id}") public CapParticipant updateParticipant(@PathVariable Long id,@Valid @RequestBody CapParticipantRequest p){return saveParticipant(participants.findById(id).orElseThrow(()->missing("Participante")),p);}
 private CapParticipant saveParticipant(CapParticipant p,CapParticipantRequest q){
  Long nested=q.activity()==null?null:q.activity().id();
  if(q.activityId()!=null&&nested!=null&&!q.activityId().equals(nested))throw bad("activity y activityId no coinciden");
  Long id=q.activityId()!=null?q.activityId():nested;
  if(id!=null)p.activity=activities.findById(id).orElseThrow(()->missing("Actividad"));else p.activity=null;
  if(q.userId()!=null&&!users.existsById(q.userId()))throw missing("Usuario");
  p.rut=q.rut();p.fullName=q.fullName();p.email=q.email();p.result=q.result();p.userId=q.userId();p.attendancePercent=q.attendancePercent();p.grade=q.grade();
  p.status=choice(q.status(),"INSCRITO","EN_CURSO","COMPLETADO","RETIRADO");return participants.save(p);
 }
 @GetMapping("/firmantes") public List<CapSignatory> signatories(){return signatories.findAll();}
 @PostMapping("/firmantes") public CapSignatory createSignatory(@Valid @RequestBody CapSignatoryRequest s){return saveSignatory(new CapSignatory(),s);}
 @PutMapping("/firmantes/{id}") public CapSignatory updateSignatory(@PathVariable Long id,@Valid @RequestBody CapSignatoryRequest s){return saveSignatory(signatories.findById(id).orElseThrow(()->missing("Firmante")),s);}
 private CapSignatory saveSignatory(CapSignatory s,CapSignatoryRequest q){
  service.requireOfficialOperator();
  if(q.validFrom()!=null&&q.validTo()!=null&&q.validTo().isBefore(q.validFrom()))throw bad("Vigencia invalida");
  if(q.signatureAsset()!=null&&q.signatureImagePath()!=null&&!q.signatureAsset().equals(q.signatureImagePath()))throw bad("Referencias de firma no coinciden");
  String asset=q.signatureAsset()!=null?q.signatureAsset():q.signatureImagePath();
  if(asset!=null&&!asset.matches("[A-Za-z0-9_-]{1,100}\\.png"))throw bad("Firma: use un nombre PNG autorizado, sin rutas");
  s.fullName=q.fullName();s.positionTitle=q.positionTitle();s.signatureImagePath=asset;s.active=q.active()==null?Boolean.TRUE:q.active();
  s.validFrom=q.validFrom();s.validTo=q.validTo();s.signaturePosition=choice(q.signaturePosition(),"IZQUIERDA","DERECHA");return signatories.save(s);
 }
 @GetMapping("/certificados") public List<CapCertificate> certificates(){return certificates.findAll();}
 @PostMapping("/certificados/emitir/{participantId}") public CapCertificate issue(@PathVariable Long participantId){return service.issue(participantId);}
 @PostMapping("/certificados/{id}/revocar") public CapCertificate revoke(@PathVariable Long id){return service.revoke(id);}
 @GetMapping("/certificados/{id}") public CapCertificate get(@PathVariable Long id){return certificates.findById(id).orElseThrow(()->missing("Certificado"));}
 private String choice(String value,String... choices){if(value==null||value.isBlank())return null;String v=value.trim().toUpperCase(Locale.ROOT);if(!Arrays.asList(choices).contains(v))throw bad("Valor no admitido: "+value);return v;}
 private ResponseStatusException bad(String message){return new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
 private ResponseStatusException missing(String name){return new ResponseStatusException(HttpStatus.NOT_FOUND,name+" no encontrado");}
}
