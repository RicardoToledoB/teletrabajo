package com.teletrabajo.controller.calificaciones;
import com.teletrabajo.entity.calificaciones.QualCycle;
import com.teletrabajo.repository.calificaciones.QualCycleRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

/** API inicial y aislada: solo gestión de ciclos BORRADOR para usuarios autenticados. */
@RestController
@RequestMapping("/api/v1/calificaciones")
public class CalificacionesController {
 private final QualCycleRepository cycles;
 public CalificacionesController(QualCycleRepository cycles){this.cycles=cycles;}
 @GetMapping("/ciclos")
 public List<QualCycle> list(){return cycles.findAll();}
 @GetMapping("/ciclos/{id}")
 public QualCycle get(@PathVariable Long id){return cycles.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
 @PostMapping("/ciclos")
 public QualCycle draft(@RequestBody QualCycle input){
  if(input.year==null||input.year<2000||input.year>2200||input.startAt==null||input.endAt==null||!input.endAt.isAfter(input.startAt)||input.rulesVersion==null||input.rulesVersion.isBlank())
   throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Año, período o versión de reglas inválidos");
  QualCycle cycle=new QualCycle();cycle.year=input.year;cycle.startAt=input.startAt;cycle.endAt=input.endAt;
  cycle.rulesVersion=input.rulesVersion;cycle.legalBasis=input.legalBasis;cycle.status="BORRADOR";cycle.createdAt=LocalDateTime.now();
  return cycles.save(cycle);
 }
}
