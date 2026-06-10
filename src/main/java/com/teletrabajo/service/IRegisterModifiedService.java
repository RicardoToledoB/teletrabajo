package com.teletrabajo.service;

import com.teletrabajo.dto.RegisterModifiedDTO;

import java.util.List;

public interface IRegisterModifiedService {
    RegisterModifiedDTO create(RegisterModifiedDTO dto);
    RegisterModifiedDTO update(Integer id, RegisterModifiedDTO dto);
    RegisterModifiedDTO getById(Integer id);
    List<RegisterModifiedDTO> getAll();
    void delete(Integer id);
}
