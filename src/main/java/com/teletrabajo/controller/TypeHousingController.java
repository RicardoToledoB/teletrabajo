package com.teletrabajo.controller;

import com.teletrabajo.dto.TypeHousingDTO;
import com.teletrabajo.service.impl.TypeHousingServiceImpl;
import com.teletrabajo.service.impl.TypePropertyServiceImpl;
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
@RequestMapping("/api/v1/types_housings")
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR','JEFATURA')")
public class TypeHousingController {

    @Autowired
    private TypeHousingServiceImpl service;

    @PostMapping
    public ResponseEntity<TypeHousingDTO> create(@RequestBody TypeHousingDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }



    @GetMapping("/{id}")
    public ResponseEntity<TypeHousingDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TypeHousingDTO> update(@PathVariable Integer id, @RequestBody TypeHousingDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/all")
    public ResponseEntity<List<TypeHousingDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<TypeHousingDTO>> getAllPaginated(
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(name, pageable));
    }


    /* SOFT DELETE */
    @GetMapping
    public ResponseEntity<List<TypeHousingDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<TypeHousingDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }



    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }
}
