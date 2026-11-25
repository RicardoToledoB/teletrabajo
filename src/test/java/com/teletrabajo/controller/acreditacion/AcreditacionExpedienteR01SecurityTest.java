package com.teletrabajo.controller.acreditacion;

import tools.jackson.databind.ObjectMapper;
import com.teletrabajo.entity.acreditacion.AcrApplication;
import com.teletrabajo.repository.UserRepository;
import com.teletrabajo.repository.acreditacion.AcrApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcreditacionExpedienteR01SecurityTest {
  AcreditacionController owner=mock(AcreditacionController.class);
  AcrApplicationRepository repo=mock(AcrApplicationRepository.class);
  JdbcTemplate db=mock(JdbcTemplate.class);
  AcreditacionExpedienteR01Controller api=new AcreditacionExpedienteR01Controller(owner,repo,mock(UserRepository.class),db,new ObjectMapper());
  UsernamePasswordAuthenticationToken jwt=new UsernamePasswordAuthenticationToken("alguien@example.test","n/a");
  AcrApplication a(int uid,String state){AcrApplication a=new AcrApplication();a.id=42L;a.processId=1L;a.applicantUserId=uid;a.status=state;return a;}

  HttpServletRequest request(String section){HttpServletRequest r=mock(HttpServletRequest.class);when(r.getRequestURI()).thenReturn("/api/v1/acreditacion/postulaciones/42/"+section);return r;}
  @Test void expedienteAjenoNoEsVisible(){
    when(owner.uid(jwt)).thenReturn(10);when(repo.findById(42L)).thenReturn(Optional.of(a(20,"BORRADOR")));
    assertEquals(404,assertThrows(ResponseStatusException.class,()->api.list(42L,request("cargos"),jwt)).getStatusCode().value());
    verifyNoInteractions(db);
  }
  @Test void presentadoNoSeModifica(){
    when(owner.uid(jwt)).thenReturn(10);when(repo.findById(42L)).thenReturn(Optional.of(a(10,"PRESENTADA")));
    assertEquals(409,assertThrows(ResponseStatusException.class,()->api.create(42L,Map.of("establishment","PRUEBA"),request("cargos"),jwt)).getStatusCode().value());
    verifyNoInteractions(db);
  }
  @Test void seccionNoPermitidaNoPermiteLecturaArbitraria(){
    when(owner.uid(jwt)).thenReturn(10);when(repo.findById(42L)).thenReturn(Optional.of(a(10,"BORRADOR")));
    assertEquals(404,assertThrows(ResponseStatusException.class,()->api.list(42L,request("users"),jwt)).getStatusCode().value());
    verifyNoInteractions(db);
  }
}
