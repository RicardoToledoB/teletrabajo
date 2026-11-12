package com.teletrabajo.service.capacitacion;

import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.capacitacion.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.time.ZoneId;
import java.util.*;

@Service
public class CapacitacionDocumentsService {
 private final CapCertificateRepository certificates;
 private final CapCertificateDocumentRepository documents;
 @Value("${capacitacion.signature-directory:/var/lib/teletrabajo/capacitacion/firmas}") private String signatureDirectory;
 public CapacitacionDocumentsService(CapCertificateRepository certificates,CapCertificateDocumentRepository documents){this.certificates=certificates;this.documents=documents;}
 private CapCertificate get(Long id){return certificates.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Certificado no encontrado"));}
 public byte[] qr(Long id,String verificationBaseUrl){return qrCode(get(id).verificationCode,verificationBaseUrl);}
 private byte[] qrCode(String code,String base){
  try{ByteArrayOutputStream out=new ByteArrayOutputStream();MatrixToImageWriter.writeToStream(new MultiFormatWriter().encode(base.replaceAll("/+$","")+"/"+code,BarcodeFormat.QR_CODE,300,300),"PNG",out);return out.toByteArray();}
  catch(Exception ex){throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"No se pudo generar QR",ex);}
 }
 public byte[] pdf(Long id,String verificationBaseUrl){
  CapCertificate c=get(id);
  CapCertificateDocument document=documents.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.CONFLICT,"Certificado sin PDF historico; no regenerar"));
  if(c.pdfSha256==null||!c.pdfSha256.equals(sha256(document.pdfBytes)))throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Integridad del PDF no valida");
  return document.pdfBytes;
 }
 public byte[] loadSignature(String asset){
  if(asset==null||!asset.matches("[A-Za-z0-9_-]{1,100}\\.png"))throw new ResponseStatusException(HttpStatus.CONFLICT,"Firma PNG autorizada no configurada");
  try{
   Path root=Path.of(signatureDirectory).toRealPath();Path file=root.resolve(asset).toRealPath();
   if(!file.startsWith(root)||!Files.isRegularFile(file)||Files.size(file)>2_000_000)throw new IOException("Firma fuera de directorio o limite");
   byte[] bytes=Files.readAllBytes(file);
   try(ImageInputStream input=ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))){
    var readers=ImageIO.getImageReaders(input);if(!readers.hasNext())throw new IOException("Imagen invalida");var reader=readers.next();
    try{reader.setInput(input);if(!reader.getFormatName().equalsIgnoreCase("png")||reader.getWidth(0)>2000||reader.getHeight(0)>2000)throw new IOException("PNG invalido o demasiado grande");reader.read(0);}finally{reader.dispose();}
   }
   return bytes;
  }catch(IOException|RuntimeException ex){throw new ResponseStatusException(HttpStatus.CONFLICT,"No se pudo cargar la firma autorizada",ex);}
 }
 public static String sha256(byte[] bytes){try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));}catch(Exception e){throw new IllegalStateException(e);}}
 public byte[] render(CapCertificate c,byte[] first,byte[] second,String verificationBaseUrl){
  try(PDDocument doc=new PDDocument();ByteArrayOutputStream out=new ByteArrayOutputStream()){
   PDPage page=new PDPage();doc.addPage(page);
   try(PDPageContentStream stream=new PDPageContentStream(doc,page)){
    PDType1Font title=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),body=new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    int y=735;line(stream,title,17,60,y,"CERTIFICADO DE CAPACITACIÓN"); y-=32;
    y=wrapped(stream,body,11,60,y,492,"Servicio de Salud Magallanes | N° "+c.certificateNumber);
    y-=16;y=wrapped(stream,body,12,60,y,492,"Se certifica la participación de: "+c.participant.fullName);
    y=wrapped(stream,body,11,60,y,492,"RUT: "+c.participant.rut);
    CapActivity a=c.participant.activity;
    y=wrapped(stream,body,11,60,y,492,"Actividad: "+a.title);
    y=wrapped(stream,body,11,60,y,492,"Código: "+safe(a.code)+" | Ejecución: "+safe(a.executionNumber)+" | Modalidad: "+safe(a.modality));
    y=wrapped(stream,body,11,60,y,492,"Fechas: "+safe(a.startDate)+" a "+safe(a.endDate)+" | Horas: "+safe(a.hours));
    y=wrapped(stream,body,11,60,y,492,"Resultado: "+safe(c.participant.result)+" | Asistencia: "+safe(c.participant.attendancePercent)+" | Nota: "+safe(c.participant.grade));
    y=wrapped(stream,body,11,60,y,492,"Emisión: "+c.issuedAt.atZone(ZoneId.of("America/Punta_Arenas")).toLocalDate());
    int signY=Math.min(y-86,365);
    if(signY<175)throw new IOException("Datos exceden el espacio del certificado");
    image(doc,stream,first,70,signY,210,65);image(doc,stream,second,330,signY,210,65);
    int left=wrapped(stream,title,10,65,signY-14,230,c.firstSignerName);wrapped(stream,body,10,65,left,230,c.firstSignerTitle);
    int right=wrapped(stream,title,10,325,signY-14,230,c.secondSignerName);wrapped(stream,body,10,325,right,230,c.secondSignerTitle);
    image(doc,stream,qrCode(c.verificationCode,verificationBaseUrl),65,52,85,85);
    wrapped(stream,body,9,165,120,380,"Verificación: "+verificationBaseUrl.replaceAll("/+$","")+"/"+c.verificationCode);
    wrapped(stream,body,9,165,70,380,"Documento generado digitalmente. Las imágenes de firma no constituyen firma electrónica avanzada.");
   }
   doc.save(out);return out.toByteArray();
  }catch(IOException ex){throw new ResponseStatusException(HttpStatus.CONFLICT,"No se pudo generar el PDF definitivo",ex);}
 }
 private void image(PDDocument doc,PDPageContentStream stream,byte[] bytes,int x,int y,int w,int h)throws IOException{
  PDImageXObject im=PDImageXObject.createFromByteArray(doc,bytes,"imagen");float scale=Math.min((float)w/im.getWidth(),(float)h/im.getHeight());stream.drawImage(im,x+(w-im.getWidth()*scale)/2,y,im.getWidth()*scale,im.getHeight()*scale);
 }
 private String safe(Object s){return s==null?"Sin información":s.toString();}
 private String encodable(PDType1Font f,String text){StringBuilder result=new StringBuilder();text.codePoints().forEach(cp->{String ch=new String(Character.toChars(cp));try{f.encode(ch);result.append(ch);}catch(Exception e){result.append('?');}});return result.toString();}
 private int wrapped(PDPageContentStream s,PDType1Font f,int size,int x,int y,int width,String text)throws IOException{
  String clean=encodable(f,text);StringBuilder part=new StringBuilder();
  for(int i=0;i<clean.length();i++){char ch=clean.charAt(i);String candidate=part.toString()+ch;if(f.getStringWidth(candidate)*size/1000>width&&part.length()>0){line(s,f,size,x,y,part.toString());y-=size+4;part.setLength(0);}part.append(ch);}
  if(part.length()>0){line(s,f,size,x,y,part.toString());y-=size+4;}return y;
 }
 private void line(PDPageContentStream s,PDType1Font f,int size,int x,int y,String text)throws IOException{s.beginText();s.setFont(f,size);s.newLineAtOffset(x,y);s.showText(encodable(f,text));s.endText();}
}
