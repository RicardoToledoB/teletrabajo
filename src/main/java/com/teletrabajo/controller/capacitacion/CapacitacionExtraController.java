package com.teletrabajo.controller.capacitacion;
import com.teletrabajo.service.capacitacion.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;
@RestController
@RequestMapping("/api/v1/capacitacion")
public class CapacitacionExtraController {
 private final CapacitacionDocumentsService documents;
 private final CapacitacionImportService imports;
 @Value("${capacitacion.verification-base-url:https://teletrabajo.dssm.cl/api/v1/public/capacitacion/verificar}") private String verificationBaseUrl;
 public CapacitacionExtraController(CapacitacionDocumentsService d,CapacitacionImportService i){documents=d;imports=i;}
 @GetMapping(value="/certificados/{id}/pdf",produces=MediaType.APPLICATION_PDF_VALUE)
 public ResponseEntity<byte[]> pdf(@PathVariable Long id){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=certificado-"+id+".pdf").body(documents.pdf(id,verificationBaseUrl));}
 @GetMapping(value="/certificados/{id}/qr",produces=MediaType.IMAGE_PNG_VALUE)
 public byte[] qr(@PathVariable Long id){return documents.qr(id,verificationBaseUrl);}
 @PostMapping(value="/actividades/{activityId}/importar-participantes",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
 public Map<String,Object> csv(@PathVariable Long activityId,@RequestParam("file") MultipartFile file,Authentication authentication)throws IOException{return imports.importParticipants(activityId,file,authentication==null?"desconocido":authentication.getName());}
}
