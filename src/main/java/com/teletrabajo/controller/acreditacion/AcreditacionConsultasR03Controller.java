package com.teletrabajo.controller.acreditacion;

import com.teletrabajo.entity.acreditacion.AcrApplication;
import com.teletrabajo.repository.acreditacion.AcrApplicationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

/** R03: consultas y vinculación de respaldos sobre tablas R01 existentes. */
@RestController
@RequestMapping("/api/v1/acreditacion/postulaciones/{id}")
public class AcreditacionConsultasR03Controller {
    private final JdbcTemplate db;
    private final AcrApplicationRepository applications;
    private final AcreditacionController core;
    public AcreditacionConsultasR03Controller(JdbcTemplate db,AcrApplicationRepository applications,AcreditacionController core){
        this.db=db;this.applications=applications;this.core=core;
    }
    private static ResponseStatusException missing(){return new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado");}
    private AcrApplication readable(Long id,Authentication auth){
        AcrApplication a=applications.findById(id).orElseThrow(AcreditacionConsultasR03Controller::missing);
        if(!Objects.equals(a.applicantUserId,core.uid(auth))&&!core.official(auth))throw missing();
        return a;
    }
    private void official(Long id,Authentication auth){core.requireOfficial(auth);applications.findById(id).orElseThrow(AcreditacionConsultasR03Controller::missing);}
    private void belongs(String table, String relation,Long id,Long child){
        Long count=db.queryForObject("SELECT COUNT(*) FROM "+table+" WHERE id=? AND "+relation+"=?",Long.class,child,id);
        if(count==null||count==0)throw missing();
    }
    @GetMapping("/admisibilidad/{reviewId}/items")
    public List<Map<String,Object>> reviewItems(@PathVariable Long id,@PathVariable Long reviewId,Authentication auth){
        official(id,auth);belongs("acr_admissibility_reviews","application_id",id,reviewId);
        return db.queryForList("SELECT * FROM acr_admissibility_items WHERE review_id=? ORDER BY id",reviewId);
    }
    @GetMapping("/evaluaciones/{evaluationId}/puntajes")
    public List<Map<String,Object>> scores(@PathVariable Long id,@PathVariable Long evaluationId,Authentication auth){
        official(id,auth);belongs("acr_evaluations","application_id",id,evaluationId);
        return db.queryForList("SELECT * FROM acr_evaluation_scores WHERE evaluation_id=? ORDER BY id",evaluationId);
    }
    @GetMapping("/apelaciones/{appealId}/documentos")
    public List<Map<String,Object>> appealDocuments(@PathVariable Long id,@PathVariable Long appealId,Authentication auth){
        readable(id,auth);belongs("acr_appeals","application_id",id,appealId);
        return db.queryForList("SELECT ad.id,ad.appeal_id,ad.document_id,ad.purpose FROM acr_appeal_documents ad " +
            "JOIN acr_documents d ON d.id=ad.document_id AND d.application_id=? AND COALESCE(d.active,1)=1 " +
            "WHERE ad.appeal_id=? ORDER BY ad.id",id,appealId);
    }
    public record AppealDocumentRequest(Long documentId,String purpose) {}
    @Transactional @PostMapping("/apelaciones/{appealId}/documentos")
    public Map<String,Object> linkAppealDocument(@PathVariable Long id,@PathVariable Long appealId,
                 @RequestBody AppealDocumentRequest request,Authentication auth){
        AcrApplication a=readable(id,auth);
        if(!Objects.equals(a.applicantUserId,core.uid(auth)))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo titular");
        if(request==null||request.documentId()==null||request.documentId()<=0||request.purpose()!=null&&request.purpose().length()>2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"documentId inválido o propósito demasiado largo");
        List<String> appeals=db.query("SELECT status FROM acr_appeals WHERE id=? AND application_id=? AND submitted_by=? FOR UPDATE",
            (rs,i)->rs.getString(1),appealId,id,a.applicantUserId);
        if(appeals.isEmpty()||!"BORRADOR".equals(appeals.get(0)))throw new ResponseStatusException(HttpStatus.CONFLICT,"Apelación no editable");
        Long docs=db.queryForObject("SELECT COUNT(*) FROM acr_documents WHERE id=? AND application_id=? AND COALESCE(active,1)=1",Long.class,request.documentId(),id);
        if(docs==null||docs==0)throw missing();
        Long exists=db.queryForObject("SELECT COUNT(*) FROM acr_appeal_documents WHERE appeal_id=? AND document_id=?",Long.class,appealId,request.documentId());
        if(exists!=null&&exists>0)throw new ResponseStatusException(HttpStatus.CONFLICT,"Documento ya vinculado");
        GeneratedKeyHolder keys=new GeneratedKeyHolder();
        db.update(con->{PreparedStatement ps=con.prepareStatement("INSERT INTO acr_appeal_documents (appeal_id,document_id,purpose) VALUES (?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1,appealId);ps.setLong(2,request.documentId());ps.setString(3,request.purpose());return ps;},keys);
        core.audit("R03_VINCULAR_DOCUMENTO_APELACION",a.processId,id,core.uid(auth),null,null);
        return db.queryForMap("SELECT id,appeal_id,document_id,purpose FROM acr_appeal_documents WHERE id=?",Objects.requireNonNull(keys.getKey()).longValue());
    }
}
