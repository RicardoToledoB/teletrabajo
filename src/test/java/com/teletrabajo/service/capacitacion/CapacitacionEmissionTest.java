package com.teletrabajo.service.capacitacion;
import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.capacitacion.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
class CapacitacionEmissionTest {
 @TempDir Path dir;
 CapParticipantRepository participants=mock(CapParticipantRepository.class);
 CapCertificateRepository certificates=mock(CapCertificateRepository.class);
 CapSignatoryRepository signatories=mock(CapSignatoryRepository.class);
 CapCertificateDocumentRepository stored=mock(CapCertificateDocumentRepository.class);
 CapacitacionDocumentsService documents=new CapacitacionDocumentsService(certificates,stored);
 CapacitacionService service=new CapacitacionService(participants,certificates,signatories,stored,documents);
 CapParticipant p; CapSignatory left,right;
 @BeforeEach void setup() throws Exception {
  ReflectionTestUtils.setField(documents,"signatureDirectory",dir.toString());
  ReflectionTestUtils.setField(service,"verificationBaseUrl","https://example.test/verificar");
  ReflectionTestUtils.setField(service,"issuanceEnabled",true);
  ReflectionTestUtils.setField(service,"officialOperators","qa@example.test");
  SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("qa@example.test","",List.of()));
  p=new CapParticipant();p.id=1L;p.rut="PRUEBA";p.fullName="Persona de prueba";p.result="APROBADO";p.status="COMPLETADO";p.activity=new CapActivity();p.activity.title="Curso de prueba";
  left=signer(1L,"IZQUIERDA","izquierda.png");right=signer(2L,"DERECHA","derecha.png");
  when(participants.findForIssue(1L)).thenReturn(Optional.of(p));
  when(certificates.findFirstByParticipantIdAndRevokedAtIsNullOrderByIdDesc(1L)).thenReturn(Optional.empty());
  when(signatories.findAll()).thenReturn(List.of(left,right));
  when(certificates.saveAndFlush(any(CapCertificate.class))).thenAnswer(i->{CapCertificate c=i.getArgument(0);c.id=7L;return c;});
  when(certificates.save(any(CapCertificate.class))).thenAnswer(i->i.getArgument(0));
  when(stored.save(any(CapCertificateDocument.class))).thenAnswer(i->{CapCertificateDocument d=i.getArgument(0);when(stored.findById(d.certificateId)).thenReturn(Optional.of(d));return d;});
 }
 @AfterEach void cleanup(){SecurityContextHolder.clearContext();}
 private CapSignatory signer(long id,String position,String file)throws Exception{
  CapSignatory s=new CapSignatory();s.id=id;s.fullName="Firmante "+id;s.positionTitle="Cargo QA";s.signaturePosition=position;s.signatureImagePath=file;s.validFrom=LocalDate.of(2020,1,1);
  ImageIO.write(new BufferedImage(80,30,BufferedImage.TYPE_INT_RGB),"PNG",dir.resolve(file).toFile());return s;
 }
 @Test void pdfInmutableConFirmasHashYRevocacion() throws Exception {
  CapCertificate c=service.issue(1L);assertEquals("EMITIDO",c.status);assertEquals("Firmante 1",c.firstSignerName);assertEquals("Firmante 2",c.secondSignerName);assertNotNull(c.certificateNumber);
  when(certificates.findById(7L)).thenReturn(Optional.of(c));when(certificates.findByVerificationCode(c.verificationCode)).thenReturn(Optional.of(c));when(stored.existsById(7L)).thenReturn(true);
  byte[] before=documents.pdf(7L,"https://example.test/verificar");assertEquals(c.pdfSha256,CapacitacionDocumentsService.sha256(before));
  try(var pdf=Loader.loadPDF(before)){String text=new PDFTextStripper().getText(pdf);assertTrue(text.contains("Firmante 1"));assertTrue(text.contains("Firmante 2"));assertTrue(text.contains("Persona de prueba"));assertTrue(text.contains(c.certificateNumber));}
  p.fullName="Nombre modificado";left.fullName="Otro firmante";assertArrayEquals(before,documents.pdf(7L,"https://example.test/verificar"));
  assertEquals(true,service.verify(c.verificationCode).get("valid"));
  when(certificates.findFirstByParticipantIdAndRevokedAtIsNullOrderByIdDesc(1L)).thenReturn(Optional.of(c));assertSame(c,service.issue(1L));
  service.revoke(7L);assertEquals("REVOCADO",c.status);assertEquals(false,service.verify(c.verificationCode).get("valid"));assertArrayEquals(before,documents.pdf(7L,"https://example.test/verificar"));
 }
 @Test void rechazaSinOperadorOSinFirmantesVigentes(){
  ReflectionTestUtils.setField(service,"issuanceEnabled",false);assertEquals(409,assertThrows(ResponseStatusException.class,()->service.issue(1L)).getStatusCode().value());
  ReflectionTestUtils.setField(service,"issuanceEnabled",true);ReflectionTestUtils.setField(service,"officialOperators","otro@example.test");assertEquals(403,assertThrows(ResponseStatusException.class,()->service.issue(1L)).getStatusCode().value());
  ReflectionTestUtils.setField(service,"officialOperators","qa@example.test");right.validTo=LocalDate.of(2020,1,2);assertEquals(409,assertThrows(ResponseStatusException.class,()->service.issue(1L)).getStatusCode().value());
  verify(certificates,never()).saveAndFlush(any());
 }
}
