package com.teletrabajo.service;

import com.teletrabajo.dto.UserRoleDTO;

import java.util.List;

public interface IUserRoleService {

    UserRoleDTO create(UserRoleDTO dto);

    UserRoleDTO update(Integer id, UserRoleDTO dto);

    UserRoleDTO getById(Integer id);

    List<UserRoleDTO> getAll();

    void delete(Integer id);
}
