package com.teletrabajo.service;

import com.teletrabajo.dto.ParentTypeDTO;

import java.util.List;

public interface IParentTypeService {
    ParentTypeDTO create(ParentTypeDTO dto);
    ParentTypeDTO update(Integer id, ParentTypeDTO dto);
    ParentTypeDTO getById(Integer id);
    List<ParentTypeDTO> getAll();
    void delete(Integer id);
}
