package com.teletrabajo.service;

import com.teletrabajo.dto.TypePropertyDTO;

import java.util.List;

public interface ITypePropertyService {
    TypePropertyDTO create(TypePropertyDTO dto);
    TypePropertyDTO update(Integer id, TypePropertyDTO dto);
    TypePropertyDTO getById(Integer id);
    List<TypePropertyDTO> getAll();
    void delete(Integer id);
}
