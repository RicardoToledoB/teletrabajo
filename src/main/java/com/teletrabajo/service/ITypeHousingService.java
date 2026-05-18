package com.teletrabajo.service;

import com.teletrabajo.dto.TypeHousingDTO;

import java.util.List;

public interface ITypeHousingService {
    TypeHousingDTO create(TypeHousingDTO dto);
    TypeHousingDTO update(Integer id, TypeHousingDTO dto);
    TypeHousingDTO getById(Integer id);
    List<TypeHousingDTO> getAll();
    void delete(Integer id);
}
