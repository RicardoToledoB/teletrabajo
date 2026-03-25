package com.teletrabajo.controller;

import com.teletrabajo.dto.UserGroupDTO;
import com.teletrabajo.service.impl.UserGroupServiceImpl;
import com.teletrabajo.service.impl.UserRoleServiceImpl;
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
@RequestMapping("/api/v1/users_groups")
@PreAuthorize("hasAnyRole('ADMIN','ADMINISTRATIVO','SUPERVISOR','JEFATURA')")
public class UserGroupController {

    @Autowired
    private UserGroupServiceImpl service;

    @PostMapping
    public ResponseEntity<UserGroupDTO> create(@RequestBody UserGroupDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }



    @GetMapping("/{id}")
    public ResponseEntity<UserGroupDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserGroupDTO> update(@PathVariable Integer id, @RequestBody UserGroupDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/all")
    public ResponseEntity<List<UserGroupDTO>> getAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/getAllPaginated")
    public ResponseEntity<Page<UserGroupDTO>> getAllPaginated(
            @RequestParam(required = false) Integer id,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.getAllPaginated(id, pageable));
    }


    /* SOFT DELETE */
    @GetMapping
    public ResponseEntity<List<UserGroupDTO>> listActive() {
        return ResponseEntity.ok(service.listActive());
    }

    @GetMapping("/deleted")
    public ResponseEntity<List<UserGroupDTO>> listDeleted() {
        return ResponseEntity.ok(service.listDeleted());
    }




    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restore(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/user/{id}")
    public ResponseEntity<List<UserGroupDTO>> findByUserId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getRolesByUser(id));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteByUserId(@PathVariable Integer userId) {
        service.deleteByUserId(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}/role/{roleId}")
    public ResponseEntity<Void> deleteByUserAndRole(@PathVariable Integer userId, @PathVariable Integer roleId) {
        service.deleteByUserAndRole(userId, roleId);
        return ResponseEntity.noContent().build();
    }
}
