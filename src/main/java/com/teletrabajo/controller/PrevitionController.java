package com.teletrabajo.controller;

import com.teletrabajo.dto.PrevitionDTO;
import com.teletrabajo.service.impl.ActivityServiceImpl;
import com.teletrabajo.service.impl.PrevitionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/previtions")
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR','JEFATURA')")
public class PrevitionController {

    @Autowired
    private PrevitionServiceImpl service;

    @PostMapping
    public ResponseEntity<PrevitionDTO> create(@RequestBody PrevitionDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }



    @GetMapping("/{id}")
    public ResponseEntity<PrevitionDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrevitionDTO> update(@PathVariable Integer id, @RequestBody PrevitionDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/all")
    public ResponseEntity<List<PrevitionDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<PrevitionDTO>> getAllPaginated(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(name, pageable));
    }


    /* SOFT DELETE */
    @GetMapping
    public ResponseEntity<List<PrevitionDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<PrevitionDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }



    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}
