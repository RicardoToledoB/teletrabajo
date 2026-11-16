package com.teletrabajo.service.capacitacion;

import com.teletrabajo.controller.capacitacion.CapacitacionController;
import com.teletrabajo.dto.capacitacion.CapSignatoryRequest;
import com.teletrabajo.entity.capacitacion.CapSignatory;
import com.teletrabajo.repository.capacitacion.CapSignatoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Regresión del POST 409: registrar firmantes NO requiere habilitar emisión de certificados. */
@SpringBootTest(properties={
 "app.data-seeder.enabled=false",
 "spring.datasource.url=jdbc:h2:mem:cap_signatory_administration_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
 "spring.jpa.hibernate.ddl-auto=create-drop",
 "spring.jpa.properties.hibernate.hbm2ddl.halt_on_error=true",
 "capacitacion.official-issuance-enabled=false",
 "capacitacion.official-operators=qa@example.test"
})
@ActiveProfiles("h2") @Transactional
class CapSignatoryAdministrationTest {
 @Autowired CapacitacionController controller;
 @Autowired CapSignatoryRepository signatories;
 @Autowired CapacitacionService service;

 @BeforeEach void authenticate(){
  SecurityContextHolder.getContext().setAuthentication(
   new UsernamePasswordAuthenticationToken("qa@example.test", "", List.of()));
 }
 @AfterEach void cleanup(){ SecurityContextHolder.clearContext(); }

 private CapSignatoryRequest request(String name,String position,boolean active,LocalDate from,LocalDate to){
  return new CapSignatoryRequest(name,"Cargo institucional",null,active,from,to,position,null);
 }

 @Test void permiteCrearConsultarYEditarSinHabilitarEmisionYConservaHistorial(){
  var from=LocalDate.of(2026,1,1);
  CapSignatory former=controller.createSignatory(request("Firmante anterior","IZQUIERDA",false,from,from.plusDays(30)));
  CapSignatory current=controller.createSignatory(request("Firmante actual","IZQUIERDA",true,from.plusDays(31),null));
  assertNotNull(former.id);assertNotNull(current.id);assertNotEquals(former.id,current.id);
  assertEquals(2,controller.signatories().size()); // GET no filtra inactivos ni históricos.
  var edited=controller.updateSignatory(current.id,request("Firmante actual corregido","IZQUIERDA",true,from.plusDays(31),null));
  assertEquals(current.id,edited.id);assertEquals("Firmante actual corregido",edited.fullName);
  assertEquals(2,signatories.count());
  assertEquals("Firmante anterior",signatories.findById(former.id).orElseThrow().fullName);
  // La emisión oficial sigue bloqueada expresamente.
  var prohibited=assertThrows(ResponseStatusException.class,()->service.issue(1L));
  assertEquals(409,prohibited.getStatusCode().value());
 }

 @Test void preservaAutorizacionYContratoDePosiciones(){
  SecurityContextHolder.getContext().setAuthentication(
   new UsernamePasswordAuthenticationToken("sin-permiso@example.test","",List.of()));
  var denied=assertThrows(ResponseStatusException.class,
   ()->controller.createSignatory(request("No autorizado","DERECHA",false,null,null)));
  assertEquals(403,denied.getStatusCode().value());
  authenticate();
  var invalid=assertThrows(ResponseStatusException.class,
   ()->controller.createSignatory(request("Posicion invalida","1",true,null,null)));
  assertEquals(400,invalid.getStatusCode().value());
  assertEquals(0,signatories.count());
 }
}
