package com.teletrabajo.repository.acreditacion;

import com.teletrabajo.entity.acreditacion.AcrProcess;
import com.teletrabajo.entity.acreditacion.AcrProcessDocument;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties={
 "app.data-seeder.enabled=false",
 "spring.datasource.url=jdbc:h2:mem:acreditacion_f01_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
 "spring.jpa.hibernate.ddl-auto=create-drop",
 "spring.jpa.properties.hibernate.hbm2ddl.halt_on_error=true"
})
@ActiveProfiles("h2")
@Transactional
class AcrF01PersistenceTest {
 @Autowired AcrProcessRepository processes;
 @Autowired AcrProcessDocumentRepository documents;
 @Autowired EntityManager em;
 @Test void roundTripF01(){
  AcrProcess p=new AcrProcess();p.year=2099;p.title="Prueba";p.status="BORRADOR";
  p.professions.addAll(List.of("Medicina","Odontología"));p.modalities.addAll(List.of("OBLIGACION","EXCELENCIA"));
  p.admissibilityDays=5;p.admissibilityDayType="habiles";p.notice="Aviso";p.reviewStartsAt=LocalDateTime.of(2099,4,1,0,0);p.appealClosesAt=LocalDateTime.of(2099,4,18,23,59);
  Long id=processes.saveAndFlush(p).id;
  AcrProcessDocument d=new AcrProcessDocument();d.processId=id;d.filename="base.pdf";d.sha256="a".repeat(64);d.pdfBytes=new byte[]{1,2,3};Long docId=documents.saveAndFlush(d).id;
  em.clear();AcrProcess saved=processes.findForUpdate(id).orElseThrow();
  assertEquals(List.of("Medicina","Odontología"),saved.professions);assertEquals(List.of("OBLIGACION","EXCELENCIA"),saved.modalities);assertEquals(5,saved.admissibilityDays);assertEquals("Aviso",saved.notice);assertEquals(LocalDateTime.of(2099,4,1,0,0),saved.reviewStartsAt);
  assertEquals(docId,documents.listActive(id).get(0).id());assertArrayEquals(new byte[]{1,2,3},documents.findByIdAndProcessIdAndActiveTrue(docId,id).orElseThrow().pdfBytes);
 }
}
