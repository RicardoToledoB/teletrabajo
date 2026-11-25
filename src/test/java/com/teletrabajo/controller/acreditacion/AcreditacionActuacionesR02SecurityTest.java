package com.teletrabajo.controller.acreditacion;

import com.teletrabajo.entity.acreditacion.AcrApplication;
import com.teletrabajo.repository.acreditacion.AcrApplicationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AcreditacionActuacionesR02SecurityTest {
 private final AcreditacionController core=mock(AcreditacionController.class);
 private final AcrApplicationRepository apps=mock(AcrApplicationRepository.class);
 private final JdbcTemplate db=mock(JdbcTemplate.class);
 private final AcreditacionActuacionesR02Controller api=new AcreditacionActuacionesR02Controller(core,apps,db);
 private final UsernamePasswordAuthenticationToken jwt=new UsernamePasswordAuthenticationToken("prueba@example.test","x");
 private HttpServletRequest req(String path){HttpServletRequest r=mock(HttpServletRequest.class);when(r.getRequestURI()).thenReturn(path);return r;}
 private AcrApplication application(int owner,String state){AcrApplication a=new AcrApplication();a.id=7L;a.processId=3L;a.applicantUserId=owner;a.status=state;return a;}

 @Test void titularNoVeApelacionesAjenas(){
  when(core.uid(jwt)).thenReturn(5);when(apps.findById(7L)).thenReturn(Optional.of(application(6,"PRESENTADA")));
  assertEquals(404,assertThrows(ResponseStatusException.class,()->api.applicationList(7L,req("/api/v1/acreditacion/postulaciones/7/apelaciones"),jwt)).getStatusCode().value());
  verifyNoInteractions(db);
 }
 @Test void noSeDictaResolucionSinHabilitacion(){
  when(core.uid(jwt)).thenReturn(5);
  assertEquals(409,assertThrows(ResponseStatusException.class,()->api.applicationCreate(7L,Map.of("resolutionNumber","TEST"),req("/api/v1/acreditacion/postulaciones/7/resoluciones"),jwt)).getStatusCode().value());
  verifyNoInteractions(db);
 }
 @Test void modoTransversalAceptaFuncionarioAutenticadoSinPerfilOficial(){
  var users=mock(com.teletrabajo.repository.UserRepository.class);
  var real=new AcreditacionController(mock(com.teletrabajo.repository.acreditacion.AcrProcessRepository.class),
   mock(AcrApplicationRepository.class),mock(com.teletrabajo.repository.acreditacion.AcrRequirementRepository.class),
   mock(com.teletrabajo.repository.acreditacion.AcrAuditEventRepository.class),users);
  org.springframework.test.util.ReflectionTestUtils.setField(real,"integrationOpenAccessEnabled",true);
  var person=com.teletrabajo.entity.UserEntity.builder().id(5).build();
  when(users.findByEmailIgnoreCase("prueba@example.test")).thenReturn(Optional.of(person));
  assertFalse(real.official(jwt)); // No adquiere identidad ni facultades de operador oficial.
  assertDoesNotThrow(()->real.requireOfficial(jwt));
  assertEquals(401,assertThrows(ResponseStatusException.class,()->real.requireOfficial(null)).getStatusCode().value());
  org.springframework.test.util.ReflectionTestUtils.setField(real,"integrationOpenAccessEnabled",false);
  assertEquals(403,assertThrows(ResponseStatusException.class,()->real.requireOfficial(jwt)).getStatusCode().value());
 }
 @Test void tokenDeVerificacionInvalidoNoAccedeABase(){
  assertEquals(404,assertThrows(ResponseStatusException.class,()->api.verify("abc")).getStatusCode().value());
  verifyNoInteractions(db);
 }
}
