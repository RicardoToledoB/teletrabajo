package com.teletrabajo.service;

import com.teletrabajo.dto.ChangePasswordDTO;
import com.teletrabajo.dto.UserDTO;

import java.util.List;

public interface IUserService {
    UserDTO create(UserDTO dto);

    UserDTO update(Integer id, UserDTO dto);

    UserDTO getById(Integer id);

    List<UserDTO> getAll();

    void delete(Integer id);
    void changePassword(Integer id, ChangePasswordDTO dto);
}
