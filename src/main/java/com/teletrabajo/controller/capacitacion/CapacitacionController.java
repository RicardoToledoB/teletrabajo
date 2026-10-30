package com.teletrabajo.controller.capacitacion;
import com.teletrabajo.entity.capacitacion.*;
import com.teletrabajo.repository.capacitacion.*;
import com.teletrabajo.service.capacitacion.CapacitacionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
@RestController @RequestMapping("/api/v1/capacitacion")
public class CapacitacionController {
 private final CapActivityRepository activities; private final CapParticipantRepository participants;
 private final CapSignatoryRepository signatories;private final CapCertificateRepository certificates;
 private final CapacitacionService service;
 public CapacitacionController(CapActivityRepository a,CapParticipantRepository p,CapSignatoryRepository s,CapCertificateRepository c,CapacitacionService service){activities=a;participants=p;signatories=s;certificates=c;this.service=service;}
 @GetMapping("/actividades") public List<CapActivity> activities(){return activities.findAll();}
 @PostMapping("/actividades") @PreAuthorize("hasRole('ADMIN')") public CapActivity createActivity(@RequestBody CapActivity a){a.id=null;return activities.save(a);}
 @GetMapping("/participantes") public List<CapParticipant> participants(){return participants.findAll();}
 @PostMapping("/participantes") @PreAuthorize("hasRole('ADMIN')") public CapParticipant createParticipant(@RequestBody CapParticipant p){p.id=null;return participants.save(p);}
 @GetMapping("/firmantes") public List<CapSignatory> signatories(){return signatories.findAll();}
 @PostMapping("/firmantes") @PreAuthorize("hasRole('ADMIN')") public CapSignatory createSignatory(@RequestBody CapSignatory s){s.id=null;return signatories.save(s);}
 @GetMapping("/certificados") public List<CapCertificate> certificates(){return certificates.findAll();}
 @PostMapping("/certificados/emitir/{participantId}") @PreAuthorize("hasRole('ADMIN')") public CapCertificate issue(@PathVariable Long participantId){return service.issue(participantId);}
 @PostMapping("/certificados/{id}/revocar") @PreAuthorize("hasRole('ADMIN')") public CapCertificate revoke(@PathVariable Long id){return service.revoke(id);}
 @GetMapping("/certificados/{id}") public CapCertificate get(@PathVariable Long id){return certificates.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
}
