package com.teletrabajo.controller;

import com.teletrabajo.dto.WorkDTO;
import com.teletrabajo.service.impl.RegisterServiceImpl;
import com.teletrabajo.service.impl.WorkServiceImpl;
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
@RequestMapping("/api/v1/works")
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR','JEFATURA')")

public class WorkController {

    @Autowired
    private WorkServiceImpl service;

    @PostMapping
    public ResponseEntity<WorkDTO> create(@RequestBody WorkDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<WorkDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }




    @GetMapping("/{id}")
    public ResponseEntity<WorkDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkDTO> update(@PathVariable Integer id, @RequestBody WorkDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    /* SOFT DELETE */
    @GetMapping
    public ResponseEntity<List<WorkDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<WorkDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }



    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    public ResponseEntity<Page<WorkDTO>> getByUserId(
            @RequestParam Integer userId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(service.findByUserId(userId, pageable));
    }
}
