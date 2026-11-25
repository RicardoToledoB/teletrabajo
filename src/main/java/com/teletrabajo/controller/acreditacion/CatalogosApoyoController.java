package com.teletrabajo.controller.acreditacion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/** R03: catálogo de apoyo aislado; no escribe en fuentes de Beneficio Postulación ni RDA-SM. */
@RestController
@RequestMapping("/api/v1/catalogos-apoyo")
public class CatalogosApoyoController {
    private static final Map<String, String> NAMES = new LinkedHashMap<>();
    static {
        NAMES.put("profesiones", "Profesiones");
        NAMES.put("nacionalidades", "Nacionalidades");
        NAMES.put("paises", "Países");
        NAMES.put("establecimientos", "Establecimientos");
        NAMES.put("unidades-servicios", "Unidades y servicios");
        NAMES.put("calidades-juridicas", "Calidades jurídicas");
        NAMES.put("titulos-profesionales", "Títulos profesionales");
        NAMES.put("especialidades", "Especialidades");
        NAMES.put("autoridades-certificadoras", "Autoridades certificadoras");
        NAMES.put("universidades", "Universidades");
        NAMES.put("regiones", "Regiones de Chile");
        NAMES.put("comunas", "Comunas de Chile");
    }
    private static final Set<String> CONTROLLED = Set.of("regiones", "comunas");
    private final JdbcTemplate jdbc;
    private final AcreditacionController accreditation;
    @Value("${catalogos-apoyo.administrators:}") private String administrators;

    public CatalogosApoyoController(JdbcTemplate jdbc, AcreditacionController accreditation) {
        this.jdbc = jdbc;
        this.accreditation = accreditation;
    }
    public record Catalog(String code, String name, boolean dynamic) {}
    public record RecordInput(
        @NotBlank @Size(max=300) String name,
        @Size(max=100) String code,
        Long parentId, Long countryId, Long communeId,
        @Size(max=200) String city,
        Boolean active,
        @Size(max=300) String source,
        String reviewStatus
    ) {}
    public record Record(Long id, String catalog, String name, String code, Long parentId,
        Long countryId, Long communeId, String city, Boolean active, String source,
        String reviewStatus, LocalDateTime createdAt, LocalDateTime updatedAt) {}
    private static ResponseStatusException error(HttpStatus status, String message) {return new ResponseStatusException(status,message);}
    private static void allowed(String code) {if (!NAMES.containsKey(code)) throw error(HttpStatus.NOT_FOUND,"Catálogo no registrado");}
    private void logged(Authentication auth) {accreditation.uid(auth);}
    private void administer(Authentication auth) {
        logged(auth);
        if (auth == null || administrators == null || Arrays.stream(administrators.split(","))
            .map(String::trim).filter(s -> !s.isEmpty()).noneMatch(s -> s.equalsIgnoreCase(auth.getName())))
            throw error(HttpStatus.FORBIDDEN, "Mantenimiento reservado a administradores de catálogos");
    }
    private void readAccess(Authentication auth) {logged(auth);}
    private Catalog catalog(String code) {allowed(code);return new Catalog(code,NAMES.get(code),!CONTROLLED.contains(code));}
    @GetMapping
    public List<Catalog> listCatalogs(Authentication auth) {
        readAccess(auth);return NAMES.keySet().stream().map(this::catalog).toList();
    }
    @GetMapping("/{catalog}")
    public Catalog getCatalog(@PathVariable String catalog, Authentication auth) {readAccess(auth);return catalog(catalog);}
    private static final RowMapper<Record> RECORD_MAPPER=(rs,i)-> {
        Timestamp created=rs.getTimestamp("created_at"),updated=rs.getTimestamp("updated_at");
        return new Record(rs.getLong("id"),rs.getString("catalog"),rs.getString("name"),rs.getString("code"),
            (Long)rs.getObject("parent_id"), (Long)rs.getObject("country_id"), (Long)rs.getObject("commune_id"),
            rs.getString("city"),rs.getBoolean("active"),rs.getString("source"),rs.getString("review_status"),
            created==null?null:created.toLocalDateTime(),updated==null?null:updated.toLocalDateTime());
    };
    @GetMapping("/{catalog}/registros")
    public List<Record> listRecords(@PathVariable String catalog,@RequestParam(required=false) Long parentId,
              @RequestParam(required=false) Long countryId,@RequestParam(defaultValue="false") boolean includeInactive,
              Authentication auth) {
        readAccess(auth);allowed(catalog);
        StringBuilder sql=new StringBuilder("SELECT * FROM acr_support_catalog_records WHERE catalog=?");
        List<Object> args=new ArrayList<>();args.add(catalog);
        if(parentId!=null){sql.append(" AND parent_id=?");args.add(parentId);}
        if(countryId!=null){sql.append(" AND country_id=?");args.add(countryId);}
        if(!includeInactive)sql.append(" AND active=1");
        sql.append(" ORDER BY name,id LIMIT 1000");
        return jdbc.query(sql.toString(),RECORD_MAPPER,args.toArray());
    }
    @GetMapping("/{catalog}/registros/{id}")
    public Record getRecord(@PathVariable String catalog,@PathVariable Long id,Authentication auth) {
        readAccess(auth);allowed(catalog);
        return jdbc.query("SELECT * FROM acr_support_catalog_records WHERE catalog=? AND id=?",RECORD_MAPPER,catalog,id)
            .stream().findFirst().orElseThrow(()->error(HttpStatus.NOT_FOUND,"Registro no encontrado"));
    }
    private void related(String targetCatalog, Long id, String field) {
        if(id==null)return;
        if(id<=0)throw error(HttpStatus.BAD_REQUEST,"Identificador invalido: "+field);
        Integer n=jdbc.queryForObject("SELECT COUNT(*) FROM acr_support_catalog_records WHERE id=? AND catalog=? AND active=1",Integer.class,id,targetCatalog);
        if(n==null||n==0)throw error(HttpStatus.BAD_REQUEST,"Referencia no encontrada o inactiva: "+field);
    }
    private void validate(String catalog,RecordInput input,Long id) {
        allowed(catalog);
        if(input==null||input.name()==null||input.name().isBlank())throw error(HttpStatus.BAD_REQUEST,"Nombre obligatorio");
        if(input.code()!=null&&input.code().isBlank())throw error(HttpStatus.BAD_REQUEST,"Código vacío");
        if(input.parentId()!=null&&input.parentId().equals(id))throw error(HttpStatus.BAD_REQUEST,"Referencia circular");
        Map<String,String> parents=Map.of("comunas","regiones","unidades-servicios","establecimientos",
           "titulos-profesionales","profesiones","especialidades","profesiones");
        if(input.parentId()!=null && !parents.containsKey(catalog))throw error(HttpStatus.BAD_REQUEST,"Catálogo sin jerarquía padre");
        if(parents.containsKey(catalog)){
            if(input.parentId()==null)throw error(HttpStatus.BAD_REQUEST,"parentId obligatorio para "+catalog);
            related(parents.get(catalog),input.parentId(),"parentId");
        }
        if(input.countryId()!=null)related("paises",input.countryId(),"countryId");
        if(input.communeId()!=null)related("comunas",input.communeId(),"communeId");
        if(input.reviewStatus()!=null&&!Set.of("PENDIENTE","VALIDADO","RECHAZADO").contains(input.reviewStatus()))
            throw error(HttpStatus.BAD_REQUEST,"reviewStatus no permitido");
    }
    private void unique(String catalog,String code,Long id){
        if(code==null)return;
        Integer n=jdbc.queryForObject("SELECT COUNT(*) FROM acr_support_catalog_records WHERE catalog=? AND code=? AND (? IS NULL OR id<>?)",Integer.class,catalog,code,id,id);
        if(n!=null&&n>0)throw error(HttpStatus.CONFLICT,"Código ya existente");
    }
    @Transactional @PostMapping("/{catalog}/registros")
    @ResponseStatus(HttpStatus.CREATED)
    public Record create(@PathVariable String catalog,@Valid @RequestBody RecordInput input,Authentication auth) {
        administer(auth);if(CONTROLLED.contains(catalog))throw error(HttpStatus.FORBIDDEN,"Regiones y comunas requieren carga oficial controlada");
        validate(catalog,input,null);unique(catalog,input.code(),null);
        GeneratedKeyHolder key=new GeneratedKeyHolder();
        jdbc.update(con->{PreparedStatement ps=con.prepareStatement("INSERT INTO acr_support_catalog_records " +
            "(catalog,name,code,parent_id,country_id,commune_id,city,active,source,review_status,created_by) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setString(1,catalog);ps.setString(2,input.name().trim());ps.setString(3,input.code());
            ps.setObject(4,input.parentId());ps.setObject(5,input.countryId());ps.setObject(6,input.communeId());
            ps.setString(7,input.city());ps.setBoolean(8,input.active()==null||input.active());ps.setString(9,input.source());
            ps.setString(10,"PENDIENTE");ps.setInt(11,accreditation.uid(auth));return ps;},key);
        return getRecord(catalog,Objects.requireNonNull(key.getKey()).longValue(),auth);
    }
    @Transactional @PutMapping("/{catalog}/registros/{id}")
    public Record update(@PathVariable String catalog,@PathVariable Long id,@Valid @RequestBody RecordInput input,Authentication auth){
        administer(auth);if(CONTROLLED.contains(catalog))throw error(HttpStatus.FORBIDDEN,"Regiones y comunas requieren carga oficial controlada");
        getRecord(catalog,id,auth);validate(catalog,input,id);unique(catalog,input.code(),id);
        jdbc.update("UPDATE acr_support_catalog_records SET name=?,code=?,parent_id=?,country_id=?,commune_id=?,city=?,source=?,updated_at=CURRENT_TIMESTAMP WHERE id=? AND catalog=?",
            input.name().trim(),input.code(),input.parentId(),input.countryId(),input.communeId(),input.city(),input.source(),id,catalog);
        return getRecord(catalog,id,auth);
    }
    @Transactional @PatchMapping("/{catalog}/registros/{id}/estado")
    public Record changeStatus(@PathVariable String catalog,@PathVariable Long id,@RequestBody Map<String,Object> body,Authentication auth){
        administer(auth);if(CONTROLLED.contains(catalog))throw error(HttpStatus.FORBIDDEN,"Regiones y comunas requieren carga oficial controlada");
        getRecord(catalog,id,auth);
        if(body==null||body.size()!=1||!(body.get("active") instanceof Boolean active))throw error(HttpStatus.BAD_REQUEST,"Se requiere active booleano");
        jdbc.update("UPDATE acr_support_catalog_records SET active=?,updated_at=CURRENT_TIMESTAMP WHERE id=? AND catalog=?",active,id,catalog);
        return getRecord(catalog,id,auth);
    }
}
