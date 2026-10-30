package com.teletrabajo.service.capacitacion;

import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.capacitacion.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CapacitacionDocumentsService {
 private final CapCertificateRepository certificates;
 public CapacitacionDocumentsService(CapCertificateRepository certificates){this.certificates=certificates;}
 private CapCertificate get(Long id){return certificates.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Certificado no encontrado"));}
 public byte[] qr(Long id, String verificationBaseUrl){
  CapCertificate c=get(id);
  if(c.revokedAt!=null)throw new ResponseStatusException(HttpStatus.CONFLICT,"Certificado revocado");
  try{
   String url=verificationBaseUrl.replaceAll("/+$", "")+"/"+c.verificationCode;
   ByteArrayOutputStream out=new ByteArrayOutputStream();
   MatrixToImageWriter.writeToStream(new MultiFormatWriter().encode(url,BarcodeFormat.QR_CODE,300,300),"PNG",out);
   return out.toByteArray();
  }catch(Exception ex){throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No se pudo generar QR",ex);}
 }
 public byte[] pdf(Long id,String verificationBaseUrl){
  CapCertificate c=get(id);
  if(c.revokedAt!=null)throw new ResponseStatusException(HttpStatus.CONFLICT,"Certificado revocado");
  try(PDDocument doc=new PDDocument();ByteArrayOutputStream out=new ByteArrayOutputStream()){
   PDPage page=new PDPage();doc.addPage(page);
   try(PDPageContentStream stream=new PDPageContentStream(doc,page)){
    PDType1Font title=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    PDType1Font body=new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    line(stream,title,18,65,715,"CERTIFICADO DE CAPACITACION");
    line(stream,body,12,65,655,"Se certifica la participacion de:");
    line(stream,title,15,65,625,safe(c.participant.fullName));
    line(stream,body,12,65,598,"RUT: "+safe(c.participant.rut));
    line(stream,body,12,65,560,"Actividad: "+safe(c.participant.activity==null?null:c.participant.activity.title));
    line(stream,body,12,65,532,"Resultado: "+safe(c.participant.result));
    line(stream,body,11,65,485,"Codigo de verificacion: "+safe(c.verificationCode));
    line(stream,body,11,65,462,"Fecha de emision: "+c.issuedAt);
    line(stream,body,10,65,430,"Verificacion: "+verificationBaseUrl.replaceAll("/+$", "")+"/"+c.verificationCode);
    // No insertar firmas sin validacion institucional de autorizacion.
    line(stream,body,9,65,80,"Documento generado digitalmente. Verifique su autenticidad con el codigo indicado.");
   }
   doc.save(out);return out.toByteArray();
  }catch(IOException ex){throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No se pudo generar PDF",ex);}
 }
 private void line(PDPageContentStream s,PDType1Font f,int size,int x,int y,String text)throws IOException{
  String normalized=java.text.Normalizer.normalize(text,java.text.Normalizer.Form.NFD).replaceAll("\\p{M}","").replaceAll("[^\\x20-\\x7E]","?");
  if(normalized.length()>90)normalized=normalized.substring(0,90);
  s.beginText();s.setFont(f,size);s.newLineAtOffset(x,y);s.showText(normalized);s.endText();
 }
 private String safe(String s){return s==null?"Sin informacion":s;}
}
