package com.teletrabajo.service;

import com.teletrabajo.dto.CivilStateDTO;

import java.util.List;

public interface ICivilStateService {
    CivilStateDTO create(CivilStateDTO dto);
    CivilStateDTO update(Integer id, CivilStateDTO dto);
    CivilStateDTO getById(Integer id);
    List<CivilStateDTO> getAll();
    void delete(Integer id);
}
