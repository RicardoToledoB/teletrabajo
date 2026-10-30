package com.teletrabajo.controller.capacitacion;
import com.teletrabajo.service.capacitacion.CapacitacionService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/v1/public/capacitacion")
public class CapacitacionVerificationController {
 private final CapacitacionService service;
 public CapacitacionVerificationController(CapacitacionService service){this.service=service;}
 @GetMapping("/verificar/{code}") public Map<String,Object> verify(@PathVariable String code){return service.verify(code);}
}
