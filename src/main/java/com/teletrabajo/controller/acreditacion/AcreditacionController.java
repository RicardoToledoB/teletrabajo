package com.teletrabajo.controller.acreditacion;

import com.teletrabajo.dto.acreditacion.AcrProcessRequest;
import com.teletrabajo.entity.acreditacion.*;
import com.teletrabajo.repository.acreditacion.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.*;
import java.util.*;
import java.text.Normalizer;

@RestController
@RequestMapping("/api/v1/acreditacion")
public class AcreditacionController {
 private final AcrProcessRepository processes;
 private final AcrApplicationRepository applications;
 private final AcrRequirementRepository requirements;
 private final AcrAuditEventRepository audits;
 private final com.teletrabajo.repository.UserRepository users;
 @Value("${acreditacion.official-operators:}") private String officialOperators = "";
 public AcreditacionController(AcrProcessRepository p,AcrApplicationRepository a,AcrRequirementRepository r,AcrAuditEventRepository e,com.teletrabajo.repository.UserRepository u){processes=p;applications=a;requirements=r;audits=e;users=u;}
 static LocalDateTime now(){return LocalDateTime.now(ZoneId.of("America/Punta_Arenas"));}
 Integer uid(Authentication auth){
  if(auth==null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
  String principal=auth.getName();
  return users.findByEmailIgnoreCase(principal).or(()->users.findByUsername(principal)).orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN,"Identidad no encontrada")).getId();
 }
 boolean official(Authentication auth){return auth!=null&&Arrays.stream(officialOperators.split(",")).map(String::trim).anyMatch(n->!n.isEmpty()&&n.equalsIgnoreCase(auth.getName()));}
 void requireOfficial(Authentication auth){if(!official(auth))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Operador de acreditacion no autorizado para esta actuacion oficial");}
 void requireEditor(AcrProcess p,Authentication auth){if(!Objects.equals(p.createdBy,uid(auth))&&!official(auth))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Convocatoria de otro usuario");}
 static boolean editable(AcrProcess p){return p.publishedAt==null&&("BORRADOR".equals(p.status)||"PROGRAMADA".equals(p.status));}
 void requireEditable(AcrProcess p,Authentication auth){requireEditor(p,auth);if(!editable(p))throw conflict("Convocatoria publicada o no editable");}
 AcrProcess process(Long id){return processes.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Convocatoria no encontrada"));}
 AcrProcess locked(Long id){return processes.findForUpdate(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Convocatoria no encontrada"));}
 void audit(String action,Long process,Long application,Integer actor,String prior,String next){AcrAuditEvent e=new AcrAuditEvent();e.action=action;e.processId=process;e.applicationId=application;e.actorUserId=actor;e.occurredAt=now();e.priorState=prior;e.newState=next;audits.save(e);}
 static ResponseStatusException bad(String text){return new ResponseStatusException(HttpStatus.BAD_REQUEST,text);}
 static ResponseStatusException conflict(String text){return new ResponseStatusException(HttpStatus.CONFLICT,text);}
 static LocalDateTime date(String value,boolean end){
  if(value==null||value.isBlank())return null;
  try{if(value.length()==10){LocalDate d=LocalDate.parse(value);return end?d.atTime(23,59,59,999999000):d.atStartOfDay();}return LocalDateTime.parse(value);}catch(java.time.DateTimeException ex){throw bad("Fecha invalida: utilice YYYY-MM-DD o YYYY-MM-DDTHH:mm:ss local de Magallanes");}
 }
 static List<String> options(List<String> values){
  if(values==null)return new ArrayList<>();
  if(values.size()>100)throw bad("Demasiadas opciones");
  LinkedHashSet<String> unique=new LinkedHashSet<>();
  for(String s:values){if(s==null||s.isBlank()||s.length()>255)throw bad("Opcion vacia o demasiado larga");unique.add(s.trim());}
  return new ArrayList<>(unique);
 }
 static String normalized(String s){return s==null?"":Normalizer.normalize(s.trim(),Normalizer.Form.NFD).replaceAll("\\p{M}","").toUpperCase(Locale.ROOT);}
 static void dates(AcrProcess p){
  if(p.opensAt==null||p.closesAt==null||!p.closesAt.isAfter(p.opensAt))throw bad("Fechas de apertura y cierre invalidas");
  if(p.reviewStartsAt!=null&&p.reviewDeadlineAt!=null&&p.reviewDeadlineAt.isBefore(p.reviewStartsAt))throw bad("Periodo de revision invalido");
  if(p.appealOpensAt!=null&&p.appealClosesAt!=null&&p.appealClosesAt.isBefore(p.appealOpensAt))throw bad("Periodo de apelacion invalido");
 }
 static void copy(AcrProcess p,AcrProcessRequest r){
  if(r.year()==null||r.year()<1900||r.year()>9999||r.title()==null||r.title().isBlank()||r.title().length()>255)throw bad("Ano o titulo invalido");
  if(r.legalBasis()!=null&&r.legalBasis().length()>15000||r.notice()!=null&&r.notice().length()>15000)throw bad("Texto demasiado largo");
  p.year=r.year();p.title=r.title().trim();p.legalBasis=r.legalBasis();p.opensAt=date(r.opensAt(),false);p.closesAt=date(r.closesAt(),true);
  p.reviewStartsAt=date(r.reviewStartsAt(),false);p.reviewDeadlineAt=date(r.reviewDeadlineAt(),true);p.appealOpensAt=date(r.appealOpensAt(),false);p.appealClosesAt=date(r.appealClosesAt(),true);
  dates(p);
  p.professions.clear();p.professions.addAll(options(r.professions()));p.modalities.clear();p.modalities.addAll(options(r.modalities()));
  if(r.admissibilityDays()!=null&&r.admissibilityDays()<1)throw bad("Plazo de admisibilidad debe ser positivo");
  String dayType=normalized(r.admissibilityDayType()).toLowerCase(Locale.ROOT);
  if(!dayType.isEmpty()&&!Set.of("habiles","corridos").contains(dayType))throw bad("Tipo de dias invalido");
  if((r.admissibilityDays()==null)!=dayType.isEmpty())throw bad("Indique plazo y tipo de dias conjuntamente");
  p.admissibilityDays=r.admissibilityDays();p.admissibilityDayType=dayType.isEmpty()?null:dayType;p.notice=r.notice();
 }
 @GetMapping("/convocatorias") public List<AcrProcess> list(){return processes.findAll();}
 @GetMapping("/convocatorias/{id}") public AcrProcess get(@PathVariable Long id){return process(id);}
 @Transactional @PostMapping("/convocatorias") public AcrProcess create(@RequestBody AcrProcessRequest request,Authentication auth){
  AcrProcess p=new AcrProcess();copy(p,request);String status=request.status()==null?"PROGRAMADA":normalized(request.status());
  if(!Set.of("BORRADOR","PROGRAMADA").contains(status))throw bad("Estado inicial invalido");
  p.status=status;p.createdBy=uid(auth);p=processes.save(p);audit("CREAR_CONVOCATORIA",p.id,null,p.createdBy,null,p.status);return p;
 }
 @Transactional @PutMapping("/convocatorias/{id}") public AcrProcess update(@PathVariable Long id,@RequestBody AcrProcessRequest request,Authentication auth){
  AcrProcess p=locked(id);requireEditable(p,auth);if(request.status()!=null&&!p.status.equals(normalized(request.status())))throw conflict("Use las acciones de cambio de estado");
  copy(p,request);audit("EDITAR_CONVOCATORIA",p.id,null,uid(auth),p.status,p.status);return processes.save(p);
 }
 @Transactional @PostMapping("/convocatorias/{id}/publicar") public AcrProcess publish(@PathVariable Long id,Authentication auth){
  requireOfficial(auth);AcrProcess p=locked(id);if(!editable(p))throw conflict("Convocatoria no publicable");dates(p);
  if(p.professions.isEmpty()||p.modalities.isEmpty()||p.admissibilityDays==null||p.admissibilityDayType==null||p.legalBasis==null||p.legalBasis.isBlank())throw conflict("Complete profesiones, modalidades, admisibilidad y normativa antes de publicar");
  if(p.modalities.stream().map(AcreditacionController::normalized).anyMatch(m->!Set.of("OBLIGACION","EXCELENCIA").contains(m)))throw conflict("Modalidad no reconocida: utilice OBLIGACION o EXCELENCIA hasta validar el catalogo institucional");
  LocalDateTime time=now();if(!time.isBefore(p.closesAt))throw conflict("Plazo de convocatoria vencido");String prior=p.status;p.publishedAt=time;p.status=time.isBefore(p.opensAt)?"PROGRAMADA":"ABIERTA";audit("PUBLICAR_CONVOCATORIA",id,null,uid(auth),prior,p.status);return processes.save(p);
 }
 @Transactional @PostMapping("/convocatorias/{id}/abrir") public AcrProcess open(@PathVariable Long id,Authentication auth){
  requireOfficial(auth);AcrProcess p=locked(id);LocalDateTime time=now();if(!"PROGRAMADA".equals(p.status)||p.publishedAt==null||p.opensAt==null||p.closesAt==null||time.isBefore(p.opensAt)||!time.isBefore(p.closesAt))throw conflict("Convocatoria no disponible para apertura");
  p.status="ABIERTA";audit("ABRIR_CONVOCATORIA",id,null,uid(auth),"PROGRAMADA",p.status);return processes.save(p);
 }
 @Transactional @PostMapping("/convocatorias/{id}/cerrar") public AcrProcess close(@PathVariable Long id,Authentication auth){
  requireOfficial(auth);AcrProcess p=locked(id);if((!"ABIERTA".equals(p.status)&&!("PROGRAMADA".equals(p.status)&&p.publishedAt!=null))||p.closesAt==null||now().isBefore(p.closesAt))throw conflict("Solo se puede cerrar una convocatoria publicada cuyo plazo haya vencido");
  String prior=p.status;p.status="CERRADA";audit("CERRAR_CONVOCATORIA",id,null,uid(auth),prior,p.status);return processes.save(p);
 }
 @GetMapping("/convocatorias/{id}/requisitos") public List<AcrRequirement> req(@PathVariable Long id){if(!processes.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND);return requirements.findByProcessId(id);}
 static void copyReq(AcrRequirement r,AcrRequirement req){if(req.title==null||req.title.isBlank()||req.title.length()>255)throw bad("Titulo de requisito invalido");if(req.validFrom!=null&&req.validTo!=null&&req.validTo.isBefore(req.validFrom))throw bad("Vigencia de requisito invalida");r.modality=req.modality;r.professionCode=req.professionCode;r.requirementKey=req.requirementKey;r.title=req.title;r.required=req.required;r.orderIndex=req.orderIndex;r.validFrom=req.validFrom;r.validTo=req.validTo;}
 @Transactional @PostMapping("/convocatorias/{id}/requisitos") public AcrRequirement addReq(@PathVariable Long id,@RequestBody AcrRequirement req,Authentication auth){
  AcrProcess p=locked(id);requireEditable(p,auth);AcrRequirement r=new AcrRequirement();r.processId=id;copyReq(r,req);r=requirements.save(r);audit("CREAR_REQUISITO",id,null,uid(auth),null,null);return r;
 }
 @Transactional @PutMapping("/convocatorias/{id}/requisitos/{requirementId}") public AcrRequirement updateReq(@PathVariable Long id,@PathVariable Long requirementId,@RequestBody AcrRequirement req,Authentication auth){
  AcrProcess p=locked(id);requireEditable(p,auth);AcrRequirement r=requirements.findById(requirementId).filter(x->Objects.equals(x.processId,id)).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));copyReq(r,req);audit("EDITAR_REQUISITO",id,null,uid(auth),null,null);return requirements.save(r);
 }
 @GetMapping("/mis-postulaciones") public List<AcrApplication> mine(Authentication auth){return applications.findByApplicantUserId(uid(auth));}
 @Transactional @PostMapping("/convocatorias/{id}/postulaciones") public AcrApplication draft(@PathVariable Long id,@RequestBody AcrApplication request,Authentication auth){
  AcrProcess p=locked(id);LocalDateTime time=now();
  if(!"ABIERTA".equals(p.status)||p.opensAt==null||p.closesAt==null||time.isBefore(p.opensAt)||!time.isBefore(p.closesAt))throw conflict("Convocatoria fuera de plazo");
  String modality=normalized(request.modality);if(!Set.of("OBLIGACION","EXCELENCIA").contains(modality)||request.professionCode==null||request.professionCode.isBlank())throw bad("Modalidad o profesion invalida");
  if(!p.modalities.isEmpty()&&p.modalities.stream().map(AcreditacionController::normalized).noneMatch(modality::equals))throw bad("Modalidad no habilitada");
  // Sin equivalencias inventadas: el código debe coincidir con la opción guardada en F01.
  if(!p.professions.isEmpty()&&p.professions.stream().map(AcreditacionController::normalized).noneMatch(normalized(request.professionCode)::equals))throw bad("Profesion no habilitada; use el valor configurado en la convocatoria");
  Integer actor=uid(auth);if(applications.existsByProcessIdAndApplicantUserIdAndModalityAndStatusNot(id,actor,modality,"ANULADA"))throw conflict("Postulacion ya registrada");
  AcrApplication a=new AcrApplication();a.processId=id;a.applicantUserId=actor;a.professionCode=request.professionCode;a.modality=modality;a.status="BORRADOR";a.eligibilityStatus="PENDIENTE";a.trackingCode="ACR-"+p.year+"-"+UUID.randomUUID().toString().substring(0,12).toUpperCase(Locale.ROOT);a=applications.save(a);audit("CREAR_BORRADOR",id,a.id,actor,null,a.status);return a;
 }
 @GetMapping("/postulaciones/{id}") public AcrApplication getMine(@PathVariable Long id,Authentication auth){AcrApplication a=applications.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(!Objects.equals(a.applicantUserId,uid(auth)))throw new ResponseStatusException(HttpStatus.FORBIDDEN);return a;}
}
