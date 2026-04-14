package com.teletrabajo.service;

import com.teletrabajo.dto.PrevitionDTO;

import java.util.List;

public interface IPrevitionService {
    PrevitionDTO create(PrevitionDTO dto);
    PrevitionDTO update(Integer id, PrevitionDTO dto);
    PrevitionDTO getById(Integer id);
    List<PrevitionDTO> getAll();
    void delete(Integer id);
}
