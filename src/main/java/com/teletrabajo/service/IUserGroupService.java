package com.teletrabajo.service;

import com.teletrabajo.dto.UserGroupDTO;

import java.util.List;

public interface IUserGroupService {

    UserGroupDTO create(UserGroupDTO dto);

    UserGroupDTO update(Integer id, UserGroupDTO dto);

    UserGroupDTO getById(Integer id);

    List<UserGroupDTO> getAll();

    void delete(Integer id);
}
