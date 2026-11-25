package com.teletrabajo.controller.acreditacion;

import com.teletrabajo.entity.acreditacion.AcrApplication;
import com.teletrabajo.entity.acreditacion.AcrProcess;
import com.teletrabajo.repository.acreditacion.AcrApplicationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.*;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;

/** Contratos R02. No emite actos oficiales sin habilitacion institucional expresa. */
@RestController
@RequestMapping("/api/v1/acreditacion")
public class AcreditacionActuacionesR02Controller {
 private final AcreditacionController core;
 private final AcrApplicationRepository apps;
 private final JdbcTemplate db;
 @Value("${acreditacion.official-decisions-enabled:false}") private boolean decisionsEnabled;

 public AcreditacionActuacionesR02Controller(AcreditacionController core,AcrApplicationRepository apps,JdbcTemplate db){this.core=core;this.apps=apps;this.db=db;}
 static ResponseStatusException bad(String message){return new ResponseStatusException(HttpStatus.BAD_REQUEST,message);}
 static ResponseStatusException conflict(String message){return new ResponseStatusException(HttpStatus.CONFLICT,message);}
 static ResponseStatusException notFound(){return new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro inexistente");}
 private void official(Authentication auth){core.requireOfficial(auth);}
 private void officialDecision(Authentication auth){official(auth);if(!decisionsEnabled)throw conflict("Actuaciones oficiales deshabilitadas hasta aprobar reglas institucionales (acreditacion.official-decisions-enabled)");}
 private AcrApplication application(Long id){return apps.findById(id).orElseThrow(AcreditacionActuacionesR02Controller::notFound);}
 private AcrApplication readable(Long id,Authentication auth){AcrApplication a=application(id);if(!Objects.equals(a.applicantUserId,core.uid(auth))&&!core.official(auth))throw notFound();return a;}
 private AcrApplication filed(Long id,Authentication auth){official(auth);AcrApplication a=application(id);if("BORRADOR".equals(a.status)||"ANULADA".equals(a.status))throw conflict("La postulacion no ha sido presentada");return a;}
 private AcrApplication lockFiled(Long id,Authentication auth){AcrApplication a=filed(id,auth);List<Map<String,Object>> rows=db.queryForList("SELECT id,status FROM acr_applications WHERE id=? FOR UPDATE",id);if(rows.isEmpty()||"BORRADOR".equals(rows.get(0).get("status")))throw conflict("Expediente no presentado");return a;}
 private void processRead(Long id,Authentication auth){AcrProcess p=core.process(id);if(p.publishedAt==null)core.requireEditor(p,auth);}
 private void processOfficial(Long id,Authentication auth){official(auth);core.process(id);}
 private static final Map<String,String> TABLES=Map.of(
  "admisibilidad","acr_admissibility_reviews", "asignaciones","acr_commission_assignments",
  "evaluaciones","acr_evaluations", "apelaciones","acr_appeals", "resoluciones","acr_resolutions",
  "certificados","acr_certificates", "criterios","acr_evaluation_criteria", "comision","acr_commission_members",
  "notificaciones","acr_notifications");
 private static final Map<String,String> FIELDS=Map.of(
  "admisibilidad","decision reason", "asignaciones","memberId",
  "evaluaciones","observations actReference", "apelaciones","grounds challengedDecision cause requestedAction consent challengedResolutionId",
  "resoluciones","appealId resolutionNumber resolutionType outcome documentReference signedAt decisionReason modifiedOutcome",
  "certificados","resolutionId certificateNumber pdfReference certificateType periodStart periodEnd holderName profession",
  "criterios","parentId areaCode factorCode title maxPoints minPoints calculationRule orderIndex professionCode modality versionNumber",
  "comision","userId functionCode activeFrom activeTo appointmentReference",
  "notificaciones","recipientUserId eventCode channel subject content scheduledAt");
 private static final Set<String> DATES=Set.of("activeFrom","activeTo","periodStart","periodEnd");
 private static final Set<String> TIMES=Set.of("signedAt","scheduledAt");
 private static final Set<String> NUMBERS=Set.of("maxPoints","minPoints");
 private static final Set<String> INTEGERS=Set.of("userId","recipientUserId","versionNumber","orderIndex");
 private static final Set<String> LONGS=Set.of("memberId","parentId","appealId","resolutionId","challengedResolutionId");
 private static final Set<String> BOOLS=Set.of("consent");
 static String snake(String key){return key.replaceAll("([a-z0-9])([A-Z])","$1_$2").toLowerCase(Locale.ROOT);}
 private String table(String section){String t=TABLES.get(section);if(t==null)throw notFound();return t;}
 private Map<String,Object> fields(String section,Map<String,Object> payload){
  if(payload==null||payload.isEmpty())throw bad("JSON vacio");
  Set<String> allowed=Set.of(FIELDS.get(section).split(" "));Map<String,Object> result=new LinkedHashMap<>();
  for(var e:payload.entrySet()){
   String key=e.getKey();if(!allowed.contains(key))throw bad("Campo no editable: "+key);
   Object value=e.getValue();if(value==null){result.put(snake(key),null);continue;}
   try{
    if(DATES.contains(key))value=LocalDate.parse(value.toString());
    else if(TIMES.contains(key))value=LocalDateTime.parse(value.toString());
    else if(NUMBERS.contains(key)){BigDecimal x=new BigDecimal(value.toString());if(x.scale()>2||x.precision()>10||x.signum()<0)throw bad("Valor numerico invalido: "+key);value=x;}
    else if(INTEGERS.contains(key)){int x=Integer.parseInt(value.toString());if(x<0)throw bad("Numero invalido");value=x;}
    else if(LONGS.contains(key)){long x=Long.parseLong(value.toString());if(x<=0)throw bad("Referencia invalida");value=x;}
    else if(BOOLS.contains(key)){if(!(value instanceof Boolean))throw bad("Booleano invalido");}
    else if(!(value instanceof String)||value.toString().length()>4000)throw bad("Texto invalido: "+key);
   }catch(java.time.DateTimeException|NumberFormatException ex){throw bad("Formato invalido: "+key);}
   result.put(snake(key),value);
  }return result;
 }
 private Long insert(String table,Map<String,Object> values){
  String sql="INSERT INTO "+table+" ("+String.join(",",values.keySet())+") VALUES ("+String.join(",",Collections.nCopies(values.size(),"?"))+")";
  List<Object> v=new ArrayList<>(values.values());GeneratedKeyHolder keys=new GeneratedKeyHolder();
  db.update(connection->{PreparedStatement ps=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);for(int i=0;i<v.size();i++)ps.setObject(i+1,v.get(i));return ps;},keys);
  return Objects.requireNonNull(keys.getKey()).longValue();
 }
 private Map<String,Object> row(String table,Long id){List<Map<String,Object>> r=db.queryForList("SELECT * FROM "+table+" WHERE id=?",id);if(r.isEmpty())throw notFound();return r.get(0);}
 private List<Map<String,Object>> list(String table,String key,Long id){return db.queryForList("SELECT * FROM "+table+" WHERE "+key+"=? ORDER BY id",id);}
 private long count(String table,String clause,Object...params){Long n=db.queryForObject("SELECT COUNT(*) FROM "+table+" WHERE "+clause,Long.class,params);return n==null?0:n;}
 private void audit(String action,AcrApplication a,Authentication auth){core.audit(action,a.processId,a.id,core.uid(auth),null,null);}
 private void auditProcess(String action,Long id,Authentication auth){core.audit(action,id,null,core.uid(auth),null,null);}
 private static String tail(HttpServletRequest request){String uri=request.getRequestURI();return uri.substring(uri.lastIndexOf('/')+1);}
 private static String str(Object obj){return obj==null?"":obj.toString();}
 private void ensure(Map<String,Object> values,String key){if(str(values.get(key)).isBlank())throw bad("Campo requerido: "+key);}
 private void reviewAccess(String section,Long id,Authentication auth){if(Set.of("admisibilidad","asignaciones","evaluaciones","resoluciones").contains(section))official(auth);else readable(id,auth);}

 @GetMapping({"/postulaciones/{id}/admisibilidad","/postulaciones/{id}/asignaciones","/postulaciones/{id}/evaluaciones","/postulaciones/{id}/apelaciones","/postulaciones/{id}/resoluciones","/postulaciones/{id}/certificados"})
 public List<Map<String,Object>> applicationList(@PathVariable Long id,HttpServletRequest request,Authentication auth){String section=tail(request);reviewAccess(section,id,auth);return list(table(section),"application_id",id);}

 @Transactional @PostMapping({"/postulaciones/{id}/admisibilidad","/postulaciones/{id}/asignaciones","/postulaciones/{id}/evaluaciones","/postulaciones/{id}/apelaciones","/postulaciones/{id}/resoluciones","/postulaciones/{id}/certificados"})
 public Map<String,Object> applicationCreate(@PathVariable Long id,@RequestBody Map<String,Object> body,HttpServletRequest request,Authentication auth){String section=tail(request);
  AcrApplication a;Map<String,Object> values=fields(section,body);
  if(section.equals("apelaciones")){
   a=readable(id,auth);if("BORRADOR".equals(a.status))throw conflict("No se apela una postulacion en borrador");
   if(!Objects.equals(a.applicantUserId,core.uid(auth)))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Apelacion reservada al titular");
   ensure(values,"grounds");if(count("acr_appeals","application_id=? AND status IN ('BORRADOR','PRESENTADA')",id)>0)throw conflict("Ya existe apelacion activa");
   AcrProcess p=core.process(a.processId);if(p.appealOpensAt==null||p.appealClosesAt==null||AcreditacionController.now().isBefore(p.appealOpensAt)||AcreditacionController.now().isAfter(p.appealClosesAt))throw conflict("Fuera de plazo de apelacion");
   if(values.get("challenged_resolution_id")!=null&&count("acr_resolutions","id=? AND application_id=?",values.get("challenged_resolution_id"),id)==0)throw bad("Resolucion impugnada ajena al expediente");
   values.put("submitted_by",a.applicantUserId);values.put("status","BORRADOR");
   if(!Boolean.TRUE.equals(values.get("consent")))throw bad("Consentimiento expreso obligatorio");
   if(count("acr_evaluations","application_id=? AND published_at IS NOT NULL",id)==0&&count("acr_resolutions","application_id=? AND signed_at IS NOT NULL",id)==0)throw conflict("No hay un resultado notificado que apelar");
  }else{
   if(Set.of("resoluciones","certificados").contains(section))officialDecision(auth);else official(auth);
   a=lockFiled(id,auth);
   switch(section){
    case "admisibilidad" -> {officialDecision(auth);ensure(values,"decision");if(!Set.of("ADMISIBLE","INADMISIBLE","OBSERVADA").contains(str(values.get("decision")).toUpperCase(Locale.ROOT)))throw bad("Decision desconocida");values.put("decision",str(values.get("decision")).toUpperCase(Locale.ROOT));values.put("reviewer_id",core.uid(auth));values.put("reviewed_at",AcreditacionController.now());values.put("is_current",true);db.update("UPDATE acr_admissibility_reviews SET is_current=0 WHERE application_id=?",id);}
    case "asignaciones" -> {ensure(values,"member_id");if(count("acr_commission_members","id=? AND process_id=?",values.get("member_id"),a.processId)==0)throw bad("Miembro ajeno a convocatoria");if(count("acr_commission_assignments","application_id=? AND member_id=? AND active=1",id,values.get("member_id"))>0)throw conflict("Asignacion ya activa");values.put("assigned_at",AcreditacionController.now());values.put("active",true);}
    case "evaluaciones" -> {if(count("acr_admissibility_reviews","application_id=? AND decision='ADMISIBLE' AND is_current=1",id)==0)throw conflict("Admisibilidad aprobada requerida");values.put("commission_status","BORRADOR");values.put("is_current",true);values.put("version_number",(int)count("acr_evaluations","application_id=?",id)+1);db.update("UPDATE acr_evaluations SET is_current=0 WHERE application_id=?",id);}
    case "resoluciones" -> {if(values.get("appeal_id")!=null&&count("acr_appeals","id=? AND application_id=?",values.get("appeal_id"),id)==0)throw bad("Apelacion ajena al expediente");ensure(values,"resolution_number");ensure(values,"outcome");ensure(values,"document_reference");ensure(values,"signed_at");if(count("acr_evaluations","application_id=? AND published_at IS NOT NULL",id)==0)throw conflict("No existen resultados publicados");values.put("issued_at",AcreditacionController.now());values.put("issued_by",core.uid(auth));}
    case "certificados" -> {ensure(values,"resolution_id");ensure(values,"pdf_reference");if(count("acr_resolutions","id=? AND application_id=? AND signed_at IS NOT NULL",values.get("resolution_id"),id)==0)throw conflict("Resolucion firmada del expediente requerida");values.put("status","PENDIENTE_VALIDACION");values.put("issued_at",null);values.put("verification_token",UUID.randomUUID().toString());}
   }
  }
  values.put("application_id",id);Long key=insert(table(section),values);audit("R02_CREAR_"+section.toUpperCase(Locale.ROOT),a,auth);return row(table(section),key);
 }

 @GetMapping({"/convocatorias/{id}/criterios","/convocatorias/{id}/comision","/convocatorias/{id}/notificaciones"})
 public List<Map<String,Object>> processList(@PathVariable Long id,HttpServletRequest request,Authentication auth){String section=tail(request);processOfficial(id,auth);return list(table(section),"process_id",id);}

 @Transactional @PostMapping({"/convocatorias/{id}/criterios","/convocatorias/{id}/comision","/convocatorias/{id}/notificaciones"})
 public Map<String,Object> processCreate(@PathVariable Long id,@RequestBody Map<String,Object> body,HttpServletRequest request,Authentication auth){String section=tail(request);
  processOfficial(id,auth);Map<String,Object> values=fields(section,body);AcrProcess p=core.process(id);
  if(section.equals("criterios")){
   if(p.publishedAt!=null)throw conflict("Criterios solo editables antes de publicar convocatoria");ensure(values,"title");ensure(values,"max_points");
   if(values.get("min_points")!=null&&((BigDecimal)values.get("min_points")).compareTo((BigDecimal)values.get("max_points"))>0)throw bad("Minimo superior a maximo");
   if(values.get("parent_id")!=null&&count("acr_evaluation_criteria","id=? AND process_id=?",values.get("parent_id"),id)==0)throw bad("Criterio padre ajeno");
   values.putIfAbsent("version_number",1);
  }else if(section.equals("comision")){
   ensure(values,"user_id");ensure(values,"function_code");
   if(count("users","id=? AND deleted_at IS NULL",values.get("user_id"))==0)throw bad("Funcionario inexistente");
  }else{
   ensure(values,"recipient_user_id");ensure(values,"event_code");
   if(count("users","id=? AND deleted_at IS NULL",values.get("recipient_user_id"))==0)throw bad("Destinatario inexistente");
   values.put("delivery_status","PENDIENTE"); // No afirma que se envio correo.
  }
  values.put("process_id",id);Long key=insert(table(section),values);auditProcess("R02_CREAR_"+section.toUpperCase(Locale.ROOT),id,auth);return row(table(section),key);
 }

 @Transactional @PostMapping("/postulaciones/{id}/admisibilidad/{reviewId}/items")
 public Map<String,Object> admissibilityItem(@PathVariable Long id,@PathVariable Long reviewId,@RequestBody Map<String,Object> body,Authentication auth){
  AcrApplication a=lockFiled(id,auth);if(count("acr_admissibility_reviews","id=? AND application_id=? AND is_current=1",reviewId,id)==0)throw notFound();
  Map<String,Object> values=allow(body,"requirementId documentId result observation");ensure(values,"requirement_id");ensure(values,"result");
  if(count("acr_requirements","id=? AND process_id=?",values.get("requirement_id"),a.processId)==0)throw bad("Requisito ajeno");
  if(values.get("document_id")!=null)document(a,values.get("document_id"));values.put("review_id",reviewId);
  Long key=insert("acr_admissibility_items",values);audit("R02_ADMISIBILIDAD_ITEM",a,auth);return row("acr_admissibility_items",key);
 }
 @Transactional @PostMapping("/postulaciones/{id}/evaluaciones/{evaluationId}/puntajes")
 public Map<String,Object> score(@PathVariable Long id,@PathVariable Long evaluationId,@RequestBody Map<String,Object> body,Authentication auth){
  AcrApplication a=lockFiled(id,auth);if(count("acr_evaluations","id=? AND application_id=? AND finalized_at IS NULL",evaluationId,id)==0)throw conflict("Evaluacion cerrada o inexistente");
  Map<String,Object> v=allow(body,"criterionId assignedScore rationale");ensure(v,"criterion_id");ensure(v,"assigned_score");
  List<Map<String,Object>> criteria=db.queryForList("SELECT min_points,max_points FROM acr_evaluation_criteria WHERE id=? AND process_id=? AND (profession_code IS NULL OR profession_code=? OR profession_code='') AND (modality IS NULL OR modality=? OR modality='')",v.get("criterion_id"),a.processId,a.professionCode,a.modality);
  if(criteria.isEmpty())throw bad("Criterio ajeno");BigDecimal score=(BigDecimal)v.get("assigned_score");Map<String,Object> c=criteria.get(0);
  if(c.get("max_points")!=null&&score.compareTo(new BigDecimal(c.get("max_points").toString()))>0||c.get("min_points")!=null&&score.compareTo(new BigDecimal(c.get("min_points").toString()))<0)throw bad("Puntaje fuera de rango");
  if(count("acr_commission_assignments ca JOIN acr_commission_members cm ON cm.id=ca.member_id","ca.application_id=? AND ca.active=1 AND cm.user_id=?",id,core.uid(auth))==0)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"No asignado a esta evaluacion");
  if(count("acr_evaluation_scores","evaluation_id=? AND criterion_id=?",evaluationId,v.get("criterion_id"))>0)throw conflict("Criterio ya puntuado");
  v.put("evaluation_id",evaluationId);v.put("assessed_by",core.uid(auth));v.put("assessed_at",AcreditacionController.now());Long key=insert("acr_evaluation_scores",v);audit("R02_REGISTRAR_PUNTAJE",a,auth);return row("acr_evaluation_scores",key);
 }
 private Map<String,Object> allow(Map<String,Object> payload,String allowed){
  if(payload==null||payload.isEmpty())throw bad("JSON vacio");Map<String,Object> out=new LinkedHashMap<>();Set<String> keys=Set.of(allowed.split(" "));
  for(var e:payload.entrySet()){if(!keys.contains(e.getKey()))throw bad("Campo no permitido: "+e.getKey());String k=e.getKey();Object v=e.getValue();if(v==null){out.put(snake(k),null);continue;}
   try{if(k.endsWith("Id"))v=Long.parseLong(v.toString());else if(k.equals("assignedScore")){BigDecimal n=new BigDecimal(v.toString());if(n.signum()<0||n.scale()>2)throw bad("Puntaje invalido");v=n;}
   else if(!(v instanceof String)||v.toString().length()>4000)throw bad("Texto invalido");}catch(NumberFormatException e2){throw bad("Numero invalido");}out.put(snake(k),v);}
  return out;
 }
 private void document(AcrApplication a,Object id){if(count("acr_documents","id=? AND application_id=? AND COALESCE(active,1)=1",id,a.id)==0)throw bad("Documento ajeno o retirado");}

 @Transactional @PostMapping("/postulaciones/{id}/evaluaciones/{evaluationId}/cerrar")
 public Map<String,Object> evaluationClose(@PathVariable Long id,@PathVariable Long evaluationId,@RequestBody(required=false) Map<String,Object> body,Authentication auth){
  AcrApplication a=lockFiled(id,auth);if(count("acr_evaluations","id=? AND application_id=? AND finalized_at IS NULL",evaluationId,id)==0)throw conflict("Evaluacion inexistente o cerrada");
  long expected=count("acr_evaluation_criteria","process_id=? AND (profession_code IS NULL OR profession_code=? OR profession_code='') AND (modality IS NULL OR modality=? OR modality='')",a.processId,a.professionCode,a.modality),actual=count("acr_evaluation_scores","evaluation_id=?",evaluationId);
  if(expected==0||actual!=expected)throw conflict("Faltan criterios evaluados: "+actual+" de "+expected);
  // Se calcula solamente la suma aritmetica. La decision sobre aprobacion requiere regla institucional.
  BigDecimal total=db.queryForObject("SELECT COALESCE(SUM(assigned_score),0) FROM acr_evaluation_scores WHERE evaluation_id=?",BigDecimal.class,evaluationId);
  db.update("UPDATE acr_evaluations SET total_score=?,commission_status='CERRADA',finalized_at=?,finalized_by=? WHERE id=? AND application_id=? AND finalized_at IS NULL",total,AcreditacionController.now(),core.uid(auth),evaluationId,id);
  audit("R02_CERRAR_EVALUACION",a,auth);return row("acr_evaluations",evaluationId);
 }
 @Transactional @PostMapping("/postulaciones/{id}/resultados/publicar")
 public Map<String,Object> publishResults(@PathVariable Long id,@RequestBody(required=false) Map<String,Object> body,Authentication auth){
  officialDecision(auth);AcrApplication a=lockFiled(id,auth);
  List<Map<String,Object>> evaluations=db.queryForList("SELECT id FROM acr_evaluations WHERE application_id=? AND finalized_at IS NOT NULL AND is_current=1",id);
  if(evaluations.size()!=1)throw conflict("Debe existir evaluacion actual cerrada unica");Long key=((Number)evaluations.get(0).get("id")).longValue();
  if(count("acr_evaluations","id=? AND published_at IS NOT NULL",key)>0)throw conflict("Resultados ya publicados");
  db.update("UPDATE acr_evaluations SET published_at=?,published_by=? WHERE id=?",AcreditacionController.now(),core.uid(auth),key);
  audit("R02_PUBLICAR_RESULTADO",a,auth);return row("acr_evaluations",key);
 }
 @Transactional @PostMapping("/postulaciones/{id}/apelaciones/{appealId}/presentar")
 public Map<String,Object> appealSubmit(@PathVariable Long id,@PathVariable Long appealId,@RequestBody(required=false) Map<String,Object> body,Authentication auth){
  AcrApplication a=readable(id,auth);if(!Objects.equals(a.applicantUserId,core.uid(auth)))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  AcrProcess p=core.process(a.processId);LocalDateTime now=AcreditacionController.now();if(p.appealOpensAt==null||p.appealClosesAt==null||now.isBefore(p.appealOpensAt)||now.isAfter(p.appealClosesAt))throw conflict("Fuera de plazo");
  int updated=db.update("UPDATE acr_appeals SET status='PRESENTADA',submitted_at=?,receipt_reference=? WHERE id=? AND application_id=? AND submitted_by=? AND status='BORRADOR' AND grounds IS NOT NULL AND TRIM(grounds)<>'' AND consent=1",now,"ACR-AP-"+UUID.randomUUID(),appealId,id,a.applicantUserId);
  if(updated==0)throw conflict("Apelacion no editable o sin fundamentos");audit("R02_PRESENTAR_APELACION",a,auth);return row("acr_appeals",appealId);
 }
 @Transactional @PostMapping("/postulaciones/{id}/apelaciones/{appealId}/resolver")
 public Map<String,Object> appealResolve(@PathVariable Long id,@PathVariable Long appealId,@RequestBody Map<String,Object> body,Authentication auth){
  officialDecision(auth);AcrApplication a=lockFiled(id,auth);if(body==null||!Set.of("decision","decisionReason").containsAll(body.keySet()))throw bad("Campos no permitidos");
  String decision=str(body.get("decision")).toUpperCase(Locale.ROOT),reason=str(body.get("decisionReason"));if(!Set.of("ACOGIDA","RECHAZADA","PARCIAL").contains(decision)||reason.isBlank()||reason.length()>4000)throw bad("Decision o fundamentacion invalida");
  if(db.update("UPDATE acr_appeals SET status='RESUELTA',decision=?,decision_reason=?,decided_by=?,decided_at=? WHERE id=? AND application_id=? AND status='PRESENTADA'",decision,reason,core.uid(auth),AcreditacionController.now(),appealId,id)==0)throw conflict("Apelacion no presentada o ya resuelta");
  audit("R02_RESOLVER_APELACION",a,auth);return row("acr_appeals",appealId);
 }
 @Transactional @PostMapping("/postulaciones/{id}/certificados/{certificateId}/revocar")
 public Map<String,Object> revoke(@PathVariable Long id,@PathVariable Long certificateId,@RequestBody Map<String,Object> body,Authentication auth){
  officialDecision(auth);AcrApplication a=lockFiled(id,auth);String reason=body==null?"":str(body.get("revokedReason"));if(body==null||!Set.of("revokedReason").containsAll(body.keySet())||reason.isBlank()||reason.length()>4000)throw bad("Fundamento requerido");
  if(db.update("UPDATE acr_certificates SET status='REVOCADO',revoked_at=?,revoked_reason=? WHERE id=? AND application_id=? AND status='VIGENTE'",AcreditacionController.now(),reason,certificateId,id)==0)throw conflict("Certificado no vigente");audit("R02_REVOCAR_CERTIFICADO",a,auth);return row("acr_certificates",certificateId);
 }
 @GetMapping("/verificacion/{token}")
 public Map<String,Object> verify(@PathVariable String token){
  if(token==null||!token.matches("[a-zA-Z0-9-]{30,80}"))throw notFound();
  List<Map<String,Object>> rows=db.queryForList("SELECT status,certificate_number,issued_at,revoked_at FROM acr_certificates WHERE verification_token=? AND status IN ('VIGENTE','REVOCADO')",token);
  if(rows.isEmpty())throw notFound();Map<String,Object> r=rows.get(0);Map<String,Object> safe=new LinkedHashMap<>();safe.put("certificateNumber",r.get("certificate_number"));safe.put("status",r.get("status"));safe.put("issuedAt",r.get("issued_at"));safe.put("revokedAt",r.get("revoked_at"));return safe;
 }
 @GetMapping("/convocatorias/{id}/reportes")
 public Map<String,Object> reports(@PathVariable Long id,Authentication auth){
  processOfficial(id,auth);Map<String,Object> r=new LinkedHashMap<>();r.put("processId",id);
  r.put("postulaciones",count("acr_applications","process_id=?",id));r.put("presentadas",count("acr_applications","process_id=? AND submitted_at IS NOT NULL",id));
  r.put("borradores",count("acr_applications","process_id=? AND status='BORRADOR'",id));r.put("evaluacionesCerradas",count("acr_evaluations e JOIN acr_applications a ON e.application_id=a.id","a.process_id=? AND e.finalized_at IS NOT NULL",id));
  r.put("apelacionesPresentadas",count("acr_appeals ap JOIN acr_applications a ON ap.application_id=a.id","a.process_id=? AND ap.submitted_at IS NOT NULL",id));return r;
 }
}
