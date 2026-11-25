package com.teletrabajo.service.impl;

import com.teletrabajo.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;

/** Validaciones aditivas: no toca tablas de origen ni convierte IDs heredados. */
@Component
public class UserProfileFieldValidator {
    private final JdbcTemplate jdbc;
    public UserProfileFieldValidator(JdbcTemplate jdbc) { this.jdbc=jdbc; }

    public void validate(UserDTO dto) {
        reference("establecimientos",dto.getEstablishmentId(),"establishmentId");
        reference("unidades-servicios",dto.getUnitId(),"unitId");
        reference("profesiones",dto.getProfessionId(),"professionId");
        reference("nacionalidades",dto.getNationalityId(),"nationalityId");
        reference("titulos-profesionales",dto.getProfessionalTitleId(),"professionalTitleId");
        length(dto.getPosition(),250,"position");
        length(dto.getStaffCategory(),100,"staffCategory");
        length(dto.getGrade(),20,"grade");
        length(dto.getApplicableLaw(),50,"applicableLaw");
        length(dto.getEmploymentStatus(),50,"employmentStatus");
        length(dto.getPhone(),30,"phone");
        length(dto.getPersonalEmail(),254,"personalEmail");
        BigDecimal hours=dto.getContractHours();
        if(hours!=null && (hours.signum()<0 || hours.compareTo(new BigDecimal("999.99"))>0 || hours.scale()>2))
            bad("contractHours debe estar entre 0 y 999.99, con hasta dos decimales");
    }
    private void length(String value,int max,String name) {
        if(value!=null && value.length()>max)bad(name+" excede longitud "+max);
    }
    private void reference(String catalog,Long id,String field) {
        if(id==null)return;
        if(id<=0)bad(field+" debe ser positivo");
        Integer found=jdbc.queryForObject("SELECT COUNT(*) FROM acr_support_catalog_records WHERE id=? AND catalog=? AND active=1",Integer.class,id,catalog);
        if(found==null || found==0)bad(field+" no corresponde a un registro vigente de "+catalog);
    }
    private void bad(String msg) {throw new ResponseStatusException(HttpStatus.BAD_REQUEST,msg);}
}
