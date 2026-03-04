package com.teletrabajo.service;

import com.teletrabajo.dto.RoleDTO;

import java.util.List;

public interface IRoleService {
    RoleDTO create(RoleDTO dto);

    RoleDTO update(Integer id, RoleDTO dto);

    RoleDTO getById(Integer id);

    List<RoleDTO> getAll();

    void delete(Integer id);
}
