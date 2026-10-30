package com.teletrabajo.service.capacitacion;
import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.capacitacion.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class CapacitacionImportService {
 private final CapParticipantRepository participants;
 private final CapActivityRepository activities;
 private final CapImportBatchRepository batches;
 public CapacitacionImportService(CapParticipantRepository p,CapActivityRepository a,CapImportBatchRepository b){participants=p;activities=a;batches=b;}
 @Transactional
 public Map<String,Object> importParticipants(Long activityId,MultipartFile file,String username)throws IOException{
  if(file.isEmpty()||file.getSize()>5_000_000)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"CSV vacio o mayor a 5 MB");
  CapActivity activity=activities.findById(activityId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Actividad no encontrada"));
  int total=0,ok=0;List<String> errors=new ArrayList<>();
  try(BufferedReader reader=new BufferedReader(new InputStreamReader(file.getInputStream(),StandardCharsets.UTF_8))){
   String header=reader.readLine();
   if(header==null||!header.replace("\uFEFF","").trim().equalsIgnoreCase("rut,nombre,email,resultado"))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cabecera requerida: rut,nombre,email,resultado");
   String line;while((line=reader.readLine())!=null){total++;if(line.isBlank())continue;
    String[] fields=line.split(",",-1);
    if(fields.length!=4||fields[0].isBlank()||fields[1].isBlank()){errors.add("Fila "+(total+1)+": formato o datos invalidos");continue;}
    CapParticipant participant=new CapParticipant();participant.rut=fields[0].trim();participant.fullName=fields[1].trim();participant.email=fields[2].trim();participant.result=fields[3].trim();participant.activity=activity;
    participants.save(participant);ok++;
   }
  }
  CapImportBatch batch=new CapImportBatch();batch.fileName=file.getOriginalFilename();batch.totalRows=total;batch.successfulRows=ok;batch.failedRows=total-ok;batch.importedAt=Instant.now();batch.importedBy=username;batches.save(batch);
  return Map.of("batchId",batch.id,"totalRows",total,"successfulRows",ok,"failedRows",total-ok,"errors",errors);
 }
}
