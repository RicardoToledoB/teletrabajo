package com.teletrabajo.controller.acreditacion;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.entity.acreditacion.*;
import com.teletrabajo.repository.UserRepository;
import com.teletrabajo.repository.acreditacion.AcrApplicationRepository;
import org.apache.pdfbox.Loader;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;

/** Expediente de postulante F02-F13. No habilita automaticamente los actos oficiales F14-F22. */
@RestController
@RequestMapping("/api/v1/acreditacion/postulaciones/{id}")
public class AcreditacionExpedienteR01Controller {
  private final AcreditacionController accreditation;
  private final AcrApplicationRepository apps;
  private final UserRepository users;
  private final JdbcTemplate db;
  private final ObjectMapper mapper;

  private static final Map<String,String> TABLES=Map.of(
    "cargos","acr_application_jobs", "capacitaciones","acr_training",
    "antecedentes-academicos","acr_academic_evidence", "antecedentes-clinicos","acr_clinical_evidence",
    "situaciones-criticas","acr_critical_evidence", "antecedentes-organizacionales","acr_org_evidence",
    "reconocimientos","acr_recognitions");
  private static final Map<String,String> ALLOWED=Map.of(
    "cargos","positionNumber establishment unitName hours jobSpecialty appointmentType",
    "capacitaciones","activityName modality provider startDate endDate academicHours hasEvaluation gradeResult specializationType documentId trainingValidation",
    "antecedentes-academicos","evidenceType institution roleName description periodStart periodEnd teachingHours researchCount documentId observations",
    "antecedentes-clinicos","activityType establishment unitName periodStart periodEnd hours quantitativeIndicator qualitativeEvaluation certifier documentId observations",
    "situaciones-criticas","eventType eventDate establishment description participation certifier documentId",
    "antecedentes-organizacionales","evidenceType description relevance periodStart periodEnd unitName establishment roleName contributionCount documentId certifier",
    "reconocimientos","recognitionType institution recognitionDate reason resolutionNumber documentId");
  private static final Set<String> DATES=Set.of("startDate","endDate","periodStart","periodEnd","eventDate","recognitionDate");
  private static final Set<String> NUMBERS=Set.of("positionNumber","researchCount","contributionCount","hours","academicHours","teachingHours");
  private static final Set<String> FICHA=Set.of("firstNames","paternalSurname","maternalSurname","birthDate","nationality","address","commune","city","email","phone","degreeTitle","degreeDate","specialtyAuthority","specialtyDate","profession","establishment","contractType","hours","specialty");

  public AcreditacionExpedienteR01Controller(AcreditacionController accreditation,AcrApplicationRepository apps,UserRepository users,JdbcTemplate db,ObjectMapper mapper){
    this.accreditation=accreditation;this.apps=apps;this.users=users;this.db=db;this.mapper=mapper;
  }
  private static String section(HttpServletRequest request,boolean itemPath){
    String[] path=request.getRequestURI().split("/");
    int i=path.length-(itemPath?2:1);
    if(i<0)return "";
    return path[i];
  }
  private static ResponseStatusException bad(String s){return new ResponseStatusException(HttpStatus.BAD_REQUEST,s);}
  private static ResponseStatusException conflict(String s){return new ResponseStatusException(HttpStatus.CONFLICT,s);}
  private static String col(String k){return k.replaceAll("([a-z0-9])([A-Z])","$1_$2").toLowerCase(Locale.ROOT);}
  private String table(String section){String t=TABLES.get(section);if(t==null)throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Seccion desconocida");return t;}
  AcrApplication own(Long id,Authentication auth){
    AcrApplication a=apps.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Expediente inexistente"));
    if(!Objects.equals(a.applicantUserId,accreditation.uid(auth))&&!accreditation.official(auth))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Expediente inexistente");
    return a;
  }
  AcrApplication editable(Long id,Authentication auth){
    AcrApplication a=own(id,auth);
    if(!Objects.equals(a.applicantUserId,accreditation.uid(auth)))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo el titular modifica su borrador");
    if(!"BORRADOR".equals(a.status))throw conflict("El expediente presentado es inmutable; subsanaciones requieren procedimiento separado");
    // Las mutaciones usan transacciones; el bloqueo asegura que no se edite tras presentar concurrentemente.
    String status=db.queryForObject("SELECT status FROM acr_applications WHERE id=? AND applicant_user_id=? FOR UPDATE",String.class,id,a.applicantUserId);
    if(!"BORRADOR".equals(status))throw conflict("El expediente ha cambiado de estado");
    return a;
  }
  private void audit(String action,AcrApplication a,Authentication auth){accreditation.audit(action,a.processId,a.id,accreditation.uid(auth),null,null);}
  private Map<String,Object> existing(String table,Long id,Long item){
    return db.query("SELECT * FROM "+table+" WHERE id=? AND application_id=?",rs->{if(!rs.next())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado");return new ColumnMapRowMapper().mapRow(rs,0);},item,id);
  }
  private Map<String,Object> values(String section,Map<String,Object> body,Long id){
    if(body==null||body.isEmpty())throw bad("Cuerpo JSON vacio");
    Set<String> valid=Set.of(ALLOWED.get(section).split(" "));Map<String,Object> values=new LinkedHashMap<>();
    for(var e:body.entrySet()){
      String field=e.getKey();if(!valid.contains(field))throw bad("Campo desconocido o no editable: "+field);
      Object v=e.getValue();if(v==null){values.put(col(field),null);continue;}
      try{
        if(field.equals("documentId")){
          long doc=Long.parseLong(v.toString());Integer n=db.queryForObject("SELECT COUNT(*) FROM acr_documents WHERE id=? AND application_id=? AND COALESCE(active,1)=1",Integer.class,doc,id);
          if(n==null||n==0)throw bad("Documento no pertenece al expediente");values.put(col(field),doc);
        }else if(DATES.contains(field))values.put(col(field),LocalDate.parse(v.toString()));
        else if(field.equals("hasEvaluation")){
          if(!(v instanceof Boolean))throw bad("hasEvaluation debe ser booleano");values.put(col(field),v);
        }else if(NUMBERS.contains(field)){
          BigDecimal n=new BigDecimal(v.toString());if(n.signum()<0||n.precision()>8||n.scale()>2||Set.of("positionNumber","researchCount","contributionCount").contains(field)&&n.scale()>0)throw bad("Numero fuera de rango: "+field);
          values.put(col(field),n);
        }else{
          if(!(v instanceof String)||v.toString().length()>4000)throw bad("Texto invalido o excesivo: "+field);values.put(col(field),v.toString().trim());
        }
      }catch(java.time.format.DateTimeParseException|NumberFormatException ex){throw bad("Formato invalido: "+field);}
    }
    return values;
  }
  @GetMapping({"/cargos","/capacitaciones","/antecedentes-academicos","/antecedentes-clinicos","/situaciones-criticas","/antecedentes-organizacionales","/reconocimientos"}) public List<Map<String,Object>> list(@PathVariable Long id,HttpServletRequest request,Authentication auth){String section=section(request,false);
    own(id,auth);return db.queryForList("SELECT * FROM "+table(section)+" WHERE application_id=? ORDER BY id",id);
  }
  @Transactional @PostMapping({"/cargos","/capacitaciones","/antecedentes-academicos","/antecedentes-clinicos","/situaciones-criticas","/antecedentes-organizacionales","/reconocimientos"}) public Map<String,Object> create(@PathVariable Long id,@RequestBody Map<String,Object> body,HttpServletRequest request,Authentication auth){String section=section(request,false);
    AcrApplication a=editable(id,auth);String table=table(section);Map<String,Object> fields=values(section,body,id);
    if(section.equals("cargos")){Integer n=db.queryForObject("SELECT COUNT(*) FROM acr_application_jobs WHERE application_id=?",Integer.class,id);if(n!=null&&n>=2)throw conflict("Se permiten maximo dos cargos");}
    String names=String.join(",",fields.keySet()),marks=String.join(",",Collections.nCopies(fields.size(),"?"));
    String sql="INSERT INTO "+table+" (application_id,"+names+") VALUES (?,"+marks+")";
    List<Object> args=new ArrayList<>();args.add(id);args.addAll(fields.values());GeneratedKeyHolder keys=new GeneratedKeyHolder();
    db.update(connection->{PreparedStatement ps=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);for(int i=0;i<args.size();i++)ps.setObject(i+1,args.get(i));return ps;},keys);
    Long item=Objects.requireNonNull(keys.getKey()).longValue();audit("CREAR_"+table.toUpperCase(Locale.ROOT),a,auth);return existing(table,id,item);
  }
  @Transactional @PutMapping({"/cargos/{itemId}","/capacitaciones/{itemId}","/antecedentes-academicos/{itemId}","/antecedentes-clinicos/{itemId}","/situaciones-criticas/{itemId}","/antecedentes-organizacionales/{itemId}","/reconocimientos/{itemId}"}) public Map<String,Object> edit(@PathVariable Long id,@PathVariable Long itemId,@RequestBody Map<String,Object> body,HttpServletRequest request,Authentication auth){String section=section(request,true);
    AcrApplication a=editable(id,auth);String table=table(section);existing(table,id,itemId);
    Map<String,Object> fields=values(section,body,id);String updates=String.join(",",fields.keySet().stream().map(k->k+"=?").toList());
    List<Object> args=new ArrayList<>(fields.values());args.add(itemId);args.add(id);
    db.update("UPDATE "+table+" SET "+updates+" WHERE id=? AND application_id=?",args.toArray());audit("EDITAR_"+table.toUpperCase(Locale.ROOT),a,auth);return existing(table,id,itemId);
  }
  @Transactional @DeleteMapping({"/cargos/{itemId}","/capacitaciones/{itemId}","/antecedentes-academicos/{itemId}","/antecedentes-clinicos/{itemId}","/situaciones-criticas/{itemId}","/antecedentes-organizacionales/{itemId}","/reconocimientos/{itemId}"}) public ResponseEntity<Void> remove(@PathVariable Long id,@PathVariable Long itemId,HttpServletRequest request,Authentication auth){String section=section(request,true);
    AcrApplication a=editable(id,auth);String table=table(section);
    if(db.update("DELETE FROM "+table+" WHERE id=? AND application_id=?",itemId,id)==0)throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    audit("QUITAR_"+table.toUpperCase(Locale.ROOT),a,auth);return ResponseEntity.noContent().build();
  }

  @Transactional @PutMapping public AcrApplication updateDraft(@PathVariable Long id,@RequestBody Map<String,Object> payload,Authentication auth){
    AcrApplication a=editable(id,auth);
    if(payload==null||payload.isEmpty()||payload.keySet().stream().anyMatch(k->!Set.of("professionCode","modality").contains(k)))throw bad("Solo se permiten profesion y modalidad en el borrador");
    AcrProcess p=accreditation.process(a.processId);
    String modality=payload.get("modality")==null?a.modality:AcreditacionController.normalized(payload.get("modality").toString());
    String profession=payload.get("professionCode")==null?a.professionCode:payload.get("professionCode").toString();
    if(!Set.of("OBLIGACION","EXCELENCIA").contains(modality)||profession==null||profession.isBlank())throw bad("Profesion o modalidad invalida");
    if(p.modalities.stream().map(AcreditacionController::normalized).noneMatch(modality::equals)||p.professions.stream().map(AcreditacionController::normalized).noneMatch(AcreditacionController.normalized(profession)::equals))throw bad("Profesion o modalidad no habilitada");
    Integer actor=accreditation.uid(auth);
    boolean duplicate=apps.findByApplicantUserId(actor).stream().anyMatch(x->!Objects.equals(x.id,a.id)&&Objects.equals(x.processId,a.processId)&&modality.equals(x.modality)&&!"ANULADA".equals(x.status));
    if(duplicate)throw conflict("Ya existe una postulacion para esa modalidad");
    a.modality=modality;a.professionCode=profession;apps.save(a);audit("EDITAR_POSTULACION",a,auth);return a;
  }

  @GetMapping("/ficha") public Map<String,Object> ficha(@PathVariable Long id,Authentication auth){
    own(id,auth);List<String> rows=db.query("SELECT snapshot_json FROM acr_applicant_snapshots WHERE application_id=?",(rs,i)->rs.getString(1),id);
    if(rows.isEmpty()||rows.get(0)==null)return Map.of();try{return mapper.readValue(rows.get(0),new TypeReference<Map<String,Object>>(){});}catch(Exception e){throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Ficha ilegible");}
  }
  @Transactional @PutMapping("/ficha") public Map<String,Object> saveFicha(@PathVariable Long id,@RequestBody Map<String,Object> body,Authentication auth){
    AcrApplication a=editable(id,auth);if(body==null||body.isEmpty()||!FICHA.containsAll(body.keySet()))throw bad("Campos de ficha no permitidos");
    Map<String,Object> ficha=new LinkedHashMap<>(ficha(id,auth));
    for(var e:body.entrySet()){
      Object v=e.getValue();if(v!=null){
        if(e.getKey().equals("hours")){try{BigDecimal h=new BigDecimal(v.toString());if(h.signum()<0||h.scale()>2)throw bad("Horas invalidas");}catch(NumberFormatException ex){throw bad("Horas invalidas");}}
        else if(!(v instanceof String)||v.toString().length()>500)throw bad("Valor invalido: "+e.getKey());
        if(e.getKey().endsWith("Date")){try{LocalDate.parse(v.toString());}catch(Exception ex){throw bad("Fecha invalida: "+e.getKey());}}
      }ficha.put(e.getKey(),v);
    }
    UserEntity u=users.findById(a.applicantUserId).orElseThrow(()->conflict("Cuenta titular inexistente"));
    ficha.put("rutNormalized",u.getRut());ficha.put("userId",a.applicantUserId);ficha.put("accountEmail",u.getEmail());
    String encoded;try{encoded=mapper.writeValueAsString(ficha);}catch(Exception e){throw bad("Ficha ilegible");}
    if(encoded.length()>30000)throw bad("Ficha demasiado extensa");
    int found=db.queryForObject("SELECT COUNT(*) FROM acr_applicant_snapshots WHERE application_id=?",Integer.class,id);
    if(found==0)db.update("INSERT INTO acr_applicant_snapshots (application_id,rut_normalized,full_name,profession,establishment,snapshot_json) VALUES (?,?,?,?,?,?)",id,u.getRut(),u.getFull_name(),ficha.get("profession"),ficha.get("establishment"),encoded);
    else db.update("UPDATE acr_applicant_snapshots SET snapshot_json=?,profession=?,establishment=? WHERE application_id=? AND captured_at IS NULL",encoded,ficha.get("profession"),ficha.get("establishment"),id);
    audit("GUARDAR_FICHA",a,auth);return ficha;
  }

  @GetMapping("/declaracion") public Map<String,Object> declaration(@PathVariable Long id,Authentication auth){
    own(id,auth);List<Map<String,Object>> rows=db.queryForList("SELECT * FROM acr_declarations WHERE application_id=?",id);return rows.isEmpty()?Map.of():rows.get(0);
  }
  @Transactional @PutMapping("/declaracion") public Map<String,Object> declarationUpdate(@PathVariable Long id,@RequestBody Map<String,Object> body,Authentication auth){
    AcrApplication a=editable(id,auth);
    if(body==null||body.keySet().stream().anyMatch(k->!Set.of("declarationText","place","accepted","signatureMethod","documentId").contains(k)))throw bad("Campos de declaracion desconocidos");
    if(!(body.get("accepted") instanceof Boolean))throw bad("accepted debe ser booleano");
    String text=Objects.toString(body.get("declarationText"),""),place=Objects.toString(body.get("place"),"");
    if(text.length()>15000||place.length()>255||Boolean.TRUE.equals(body.get("accepted"))&&text.isBlank())throw bad("Declaracion incompleta");
    String method=Objects.toString(body.get("signatureMethod"),"");
    if(!method.isBlank()&&!Set.of("DOCUMENTO_ADJUNTO","CONFIRMACION_PLATAFORMA").contains(method))throw bad("Mecanismo de firma invalido");
    Long document=null;
    if(body.get("documentId")!=null){try{document=Long.valueOf(body.get("documentId").toString());}catch(NumberFormatException ex){throw bad("Documento invalido");}
      int count=db.queryForObject("SELECT COUNT(*) FROM acr_documents WHERE id=? AND application_id=? AND COALESCE(active,1)=1",Integer.class,document,id);if(count==0)throw bad("Documento ajeno al expediente");}
    LocalDateTime date=Boolean.TRUE.equals(body.get("accepted"))?AcreditacionController.now():null;
    int found=db.queryForObject("SELECT COUNT(*) FROM acr_declarations WHERE application_id=?",Integer.class,id);
    if(found==0)db.update("INSERT INTO acr_declarations (application_id,declaration_text,place,accepted,signature_method,document_id,declared_at) VALUES (?,?,?,?,?,?,?)",id,text,place,body.get("accepted"),method,document,date);
    else db.update("UPDATE acr_declarations SET declaration_text=?,place=?,accepted=?,signature_method=?,document_id=?,declared_at=? WHERE application_id=?",text,place,body.get("accepted"),method,document,date,id);
    audit("DECLARACION",a,auth);return declaration(id,auth);
  }

  private static String sha(byte[] bytes){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));}catch(NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
  @GetMapping("/documentos") public List<Map<String,Object>> documents(@PathVariable Long id,Authentication auth){
    own(id,auth);return db.queryForList("SELECT id,application_id,requirement_id,criterion_id,filename,sha256,uploaded_at,validation_status,active,version_number FROM acr_documents WHERE application_id=? ORDER BY id",id);
  }
  @Transactional @PostMapping(value="/documentos",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
  public Map<String,Object> upload(@PathVariable Long id,@RequestParam("file") MultipartFile file,@RequestParam(required=false) Long requirementId,@RequestParam(required=false) Long criterionId,Authentication auth){
    AcrApplication a=editable(id,auth);
    if(file.isEmpty()||file.getSize()>10_000_000L)throw bad("PDF obligatorio; limite 10000000 bytes");
    String name=file.getOriginalFilename();if(name==null||name.length()>255||name.contains("/")||name.contains("\\")||!name.toLowerCase(Locale.ROOT).endsWith(".pdf"))throw bad("Nombre de PDF invalido");
    if(requirementId!=null){int n=db.queryForObject("SELECT COUNT(*) FROM acr_requirements WHERE id=? AND process_id=?",Integer.class,requirementId,a.processId);if(n==0)throw bad("Requisito ajeno a convocatoria");}
    if(criterionId!=null){int n=db.queryForObject("SELECT COUNT(*) FROM acr_evaluation_criteria WHERE id=? AND process_id=?",Integer.class,criterionId,a.processId);if(n==0)throw bad("Criterio ajeno a convocatoria");}
    byte[] bytes;try{bytes=file.getBytes();if(bytes.length<5||!new String(bytes,0,5,StandardCharsets.US_ASCII).equals("%PDF-"))throw new IOException("Cabecera PDF invalida");try(var pdf=Loader.loadPDF(bytes)){if(pdf.isEncrypted()||pdf.getNumberOfPages()<1)throw new IOException("PDF cifrado o vacio");}}catch(IOException e){throw bad("PDF invalido o cifrado");}
    String hash=sha(bytes);GeneratedKeyHolder key=new GeneratedKeyHolder();Integer userId=accreditation.uid(auth);
    db.update(c->{PreparedStatement ps=c.prepareStatement("INSERT INTO acr_documents (application_id,requirement_id,criterion_id,filename,sha256,uploaded_by,uploaded_at,validation_status,active,version_number) VALUES (?,?,?,?,?,?,?,'PENDIENTE',1,1)",Statement.RETURN_GENERATED_KEYS);ps.setObject(1,id);ps.setObject(2,requirementId);ps.setObject(3,criterionId);ps.setString(4,name);ps.setString(5,hash);ps.setObject(6,userId);ps.setObject(7,AcreditacionController.now());return ps;},key);
    Long doc=Objects.requireNonNull(key.getKey()).longValue();db.update("INSERT INTO acr_document_files(document_id,pdf_bytes) VALUES (?,?)",doc,bytes);audit("ADJUNTAR_DOCUMENTO",a,auth);
    return db.queryForMap("SELECT id,application_id,filename,sha256,uploaded_at,active FROM acr_documents WHERE id=?",doc);
  }
  @GetMapping(value="/documentos/{documentId}/pdf",produces=MediaType.APPLICATION_PDF_VALUE)
  public ResponseEntity<byte[]> download(@PathVariable Long id,@PathVariable Long documentId,Authentication auth){
    own(id,auth);List<Map<String,Object>> rows=db.queryForList("SELECT d.sha256,b.pdf_bytes FROM acr_documents d JOIN acr_document_files b ON d.id=b.document_id WHERE d.id=? AND d.application_id=? AND COALESCE(d.active,1)=1",documentId,id);
    if(rows.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Documento inexistente");byte[] bytes=(byte[])rows.get(0).get("pdf_bytes");if(!sha(bytes).equals(rows.get(0).get("sha256")))throw conflict("Huella documental incorrecta");
    return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=expediente-"+documentId+".pdf").body(bytes);
  }
  @Transactional @PostMapping("/documentos/{documentId}/retirar") public Map<String,Object> removeDocument(@PathVariable Long id,@PathVariable Long documentId,Authentication auth){
    AcrApplication a=editable(id,auth);if(db.update("UPDATE acr_documents SET active=0 WHERE id=? AND application_id=? AND COALESCE(active,1)=1",documentId,id)==0)throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    audit("RETIRAR_DOCUMENTO",a,auth);return Map.of("documentId",documentId,"active",false);
  }
  @Transactional @PostMapping("/presentar") public Map<String,Object> submit(@PathVariable Long id,Authentication auth){
    Integer user=accreditation.uid(auth);
    List<Map<String,Object>> states=db.queryForList("SELECT status,submitted_at,tracking_code FROM acr_applications WHERE id=? AND applicant_user_id=? FOR UPDATE",id,user);
    if(states.isEmpty())throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Expediente inexistente");
    Map<String,Object> state=states.get(0);
    if("PRESENTADA".equals(state.get("status"))){
      Object sent=state.get("submitted_at"),folio=state.get("tracking_code");
      if(sent==null||folio==null)throw conflict("Expediente presentado sin comprobante valido");
      return Map.of("applicationId",id,"trackingCode",folio,"status","PRESENTADA","submittedAt",sent,"message","Recepcion ya registrada; no acredita aprobacion");
    }
    if(!"BORRADOR".equals(state.get("status")))throw conflict("La postulacion no se encuentra en borrador");
    AcrApplication a=own(id,auth);if(a.trackingCode==null)throw conflict("Expediente sin folio");
    AcrProcess p=accreditation.process(a.processId);LocalDateTime now=AcreditacionController.now();
    if(!"ABIERTA".equals(p.status)||p.publishedAt==null||p.opensAt==null||p.closesAt==null||now.isBefore(p.opensAt)||!now.isBefore(p.closesAt))throw conflict("Convocatoria fuera de plazo");
    if(ficha(id,auth).isEmpty())throw conflict("Falta ficha del postulante");
    int jobs=db.queryForObject("SELECT COUNT(*) FROM acr_application_jobs WHERE application_id=?",Integer.class,id);if(jobs<1||jobs>2)throw conflict("Debe registrar entre uno y dos cargos");
    List<Map<String,Object>> declarations=db.queryForList("SELECT accepted,document_id FROM acr_declarations WHERE application_id=?",id);
    if(declarations.isEmpty()||!(Boolean.TRUE.equals(declarations.get(0).get("accepted"))||"1".equals(Objects.toString(declarations.get(0).get("accepted")))))throw conflict("Declaracion no aceptada");
    Object signed=declarations.get(0).get("document_id");if(signed==null)throw conflict("Requiere declaracion firmada adjunta en PDF");
    int signedCount=db.queryForObject("SELECT COUNT(*) FROM acr_documents WHERE id=? AND application_id=? AND COALESCE(active,1)=1",Integer.class,signed,id);if(signedCount==0)throw conflict("Declaracion firmada no disponible");
    List<Map<String,Object>> required=db.queryForList("SELECT id,modality,profession_code FROM acr_requirements WHERE process_id=? AND required=1",a.processId);
    for(var r:required){String modality=Objects.toString(r.get("modality"),""),profession=Objects.toString(r.get("profession_code"),"");
      if(!modality.isBlank()&&!modality.equalsIgnoreCase(a.modality)||!profession.isBlank()&&!profession.equalsIgnoreCase(a.professionCode))continue;
      int count=db.queryForObject("SELECT COUNT(*) FROM acr_documents WHERE application_id=? AND requirement_id=? AND COALESCE(active,1)=1",Integer.class,id,r.get("id"));
      if(count==0)throw conflict("Falta documento de requisito: "+r.get("id"));
    }
    db.update("UPDATE acr_applicant_snapshots SET captured_at=? WHERE application_id=? AND captured_at IS NULL",now,id);
    db.update("UPDATE acr_applications SET status='PRESENTADA',submitted_at=? WHERE id=? AND status='BORRADOR'",now,id);
    accreditation.audit("PRESENTAR_POSTULACION",a.processId,a.id,user,"BORRADOR","PRESENTADA");
    return Map.of("applicationId",id,"trackingCode",a.trackingCode,"status","PRESENTADA","submittedAt",now,"message","Recepcion registrada; no acredita aprobacion");
  }
  @GetMapping("/trayectoria") public List<Map<String,Object>> timeline(@PathVariable Long id,Authentication auth){
    own(id,auth);return db.queryForList("SELECT occurred_at,action,prior_state,new_state FROM acr_audit_events WHERE application_id=? ORDER BY occurred_at,id",id);
  }
}
