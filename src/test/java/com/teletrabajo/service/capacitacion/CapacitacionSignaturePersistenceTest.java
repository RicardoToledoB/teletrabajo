package com.teletrabajo.service.capacitacion;

import com.teletrabajo.entity.capacitacion.CapSignatory;
import com.teletrabajo.repository.capacitacion.CapSignatoryRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties={
 "app.data-seeder.enabled=false",
 "spring.datasource.url=jdbc:h2:mem:cap_signature_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE",
 "spring.jpa.hibernate.ddl-auto=create-drop",
 "spring.jpa.properties.hibernate.hbm2ddl.halt_on_error=true",
 "capacitacion.official-issuance-enabled=true",
 "capacitacion.official-operators=qa@example.test"
})
@ActiveProfiles("h2") @Transactional
class CapacitacionSignaturePersistenceTest {
 @Autowired CapSignatoryRepository signatories;
 @Autowired CapacitacionService service;
 @Autowired CapacitacionDocumentsService documents;
 @Autowired EntityManager em;
 @TempDir Path dir;
 @BeforeEach void setup(){ReflectionTestUtils.setField(documents,"signatureDirectory",dir.toString());authenticate("qa@example.test");}
 @AfterEach void cleanup(){SecurityContextHolder.clearContext();}
 private void authenticate(String name){SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(name,"",List.of()));}
 private Long signer(){CapSignatory s=new CapSignatory();s.fullName="Firmante QA";s.positionTitle="Cargo QA";s.signaturePosition="IZQUIERDA";s.validFrom=LocalDate.of(2026,1,1);return signatories.saveAndFlush(s).id;}
 private MockMultipartFile file(String format,int width,int height)throws Exception{
  BufferedImage img=new BufferedImage(width,height,format.equals("png")?BufferedImage.TYPE_INT_ARGB:BufferedImage.TYPE_INT_RGB);
  img.setRGB(0,0,0xff123456); ByteArrayOutputStream out=new ByteArrayOutputStream();assertTrue(ImageIO.write(img,format,out));
  return new MockMultipartFile("file","firma."+format,"image/"+format,out.toByteArray());
 }
 @Test void cargaJpgReemplazoPngYReferenciaPersistente()throws Exception{
  Long id=signer();CapSignatory first=service.uploadSignature(id,file("jpeg",80,30));String old=first.signatureImagePath;
  assertTrue(old.endsWith(".png"));assertEquals(old,first.getSignatureAsset());byte[] previous=documents.loadSignature(old);
  em.clear();assertEquals(old,signatories.findById(id).orElseThrow().signatureImagePath);
  CapSignatory second=service.uploadSignature(id,file("png",100,40));String next=second.signatureImagePath;
  assertNotEquals(old,next);assertEquals(id,second.id);assertEquals("Firmante QA",second.fullName);assertEquals("Cargo QA",second.positionTitle);
  assertEquals("IZQUIERDA",second.signaturePosition);assertEquals(LocalDate.of(2026,1,1),second.validFrom);assertTrue(second.active);
  em.clear();assertEquals(next,signatories.findById(id).orElseThrow().getSignatureAsset());
  assertArrayEquals(previous,documents.loadSignature(old));assertTrue(Files.isRegularFile(dir.resolve(next)));
 }
 @Test void rechazaArchivosInvalidosYDimensionesSinCambiarReferencia()throws Exception{
  Long id=signer();String old=service.uploadSignature(id,file("png",80,30)).signatureImagePath;
  for(var invalid:List.of(new MockMultipartFile("file","f.png","image/png",new byte[]{1,2,3}),file("png",2001,1),new MockMultipartFile("file","f.png","image/png",new byte[2_000_001]))){
   assertEquals(400,assertThrows(ResponseStatusException.class,()->documents.storeSignature(invalid)).getStatusCode().value());
  }
  em.clear();assertEquals(old,signatories.findById(id).orElseThrow().signatureImagePath);
  try(var files=Files.list(dir)){assertEquals(1,files.count());}
 }
 @Test void exigeOperadorAutorizadoYFirmanteExistente()throws Exception{
  Long id=signer();var image=file("png",80,30);
  // Las excepciones del servicio transaccional marcan rollback; no se realizan escrituras posteriores.
  authenticate("otro@example.test");assertEquals(403,assertThrows(ResponseStatusException.class,()->service.uploadSignature(id,image)).getStatusCode().value());
  authenticate("qa@example.test");assertEquals(404,assertThrows(ResponseStatusException.class,()->service.uploadSignature(Long.MAX_VALUE,image)).getStatusCode().value());
  try(var files=Files.list(dir)){assertEquals(0,files.count());}
 }
}
