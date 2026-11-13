package com.teletrabajo.controller.acreditacion;
import com.teletrabajo.dto.acreditacion.AcrProcessDocumentDto;
import com.teletrabajo.entity.acreditacion.*;
import com.teletrabajo.repository.acreditacion.*;
import org.apache.pdfbox.Loader;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.*;

@RestController
@RequestMapping("/api/v1/acreditacion/convocatorias/{id}/documentos")
public class AcreditacionProcessDocumentController {
 private final AcrProcessDocumentRepository documents;
 private final AcreditacionController accreditation;
 public AcreditacionProcessDocumentController(AcrProcessDocumentRepository documents,AcreditacionController accreditation){this.documents=documents;this.accreditation=accreditation;}
 private AcrProcessDocumentDto dto(AcrProcessDocument d){return new AcrProcessDocumentDto(d.id,d.processId,d.filename,d.sha256,d.uploadedBy,d.uploadedAt,d.active);}
 private AcrProcessDocument find(Long id,Long documentId){return documents.findByIdAndProcessIdAndActiveTrue(documentId,id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Documento no encontrado"));}
 private void readAccess(Long id,Authentication auth){AcrProcess p=accreditation.process(id);if(p.publishedAt==null)accreditation.requireEditor(p,auth);}
 static String sha256(byte[] bytes){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));}catch(java.security.NoSuchAlgorithmException e){throw new IllegalStateException(e);}}
 @GetMapping public List<AcrProcessDocumentDto> list(@PathVariable Long id,Authentication auth){readAccess(id,auth);return documents.listActive(id);}
 @Transactional @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
 public AcrProcessDocumentDto upload(@PathVariable Long id,@RequestParam("file") MultipartFile file,Authentication auth){
  AcrProcess p=accreditation.locked(id);accreditation.requireEditable(p,auth);
  if(file.isEmpty()||file.getSize()>10_000_000L)throw AcreditacionController.bad("PDF obligatorio, maximo 10 MB");
  String name=file.getOriginalFilename();if(name==null||name.length()>255||name.contains("/")||name.contains("\\")||name.chars().anyMatch(c->c<32)||!name.toLowerCase(Locale.ROOT).endsWith(".pdf"))throw AcreditacionController.bad("Nombre de PDF invalido");
  byte[] bytes;try{bytes=file.getBytes();if(bytes.length<5||!new String(bytes,0,5,java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-"))throw new IOException("Cabecera invalida");try(var parsed=Loader.loadPDF(bytes)){if(parsed.isEncrypted()||parsed.getNumberOfPages()<1)throw new IOException("PDF cifrado o vacio");}}catch(IOException e){throw AcreditacionController.bad("PDF invalido o cifrado");}
  AcrProcessDocument d=new AcrProcessDocument();d.processId=id;d.filename=name;d.pdfBytes=bytes;d.sha256=sha256(bytes);d.uploadedAt=AcreditacionController.now();d.uploadedBy=accreditation.uid(auth);d=documents.save(d);accreditation.audit("AGREGAR_BASE_ANEXO",id,null,d.uploadedBy,null,null);return dto(d);
 }
 @GetMapping(value="/{documentId}/pdf",produces=MediaType.APPLICATION_PDF_VALUE)
 public ResponseEntity<byte[]> download(@PathVariable Long id,@PathVariable Long documentId,Authentication auth){readAccess(id,auth);AcrProcessDocument d=find(id,documentId);if(!sha256(d.pdfBytes).equals(d.sha256))throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Integridad del documento invalida");return ResponseEntity.ok().cacheControl(CacheControl.noStore()).header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=base-acreditacion-"+documentId+".pdf").body(d.pdfBytes);}
 @Transactional @PostMapping("/{documentId}/retirar") public AcrProcessDocumentDto withdraw(@PathVariable Long id,@PathVariable Long documentId,Authentication auth){AcrProcess p=accreditation.locked(id);accreditation.requireEditable(p,auth);AcrProcessDocument d=find(id,documentId);d.active=false;accreditation.audit("RETIRAR_BASE_ANEXO",id,null,accreditation.uid(auth),null,null);return dto(documents.save(d));}
}
