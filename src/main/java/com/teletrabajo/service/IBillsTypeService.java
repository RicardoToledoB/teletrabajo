package com.teletrabajo.service;

import com.teletrabajo.dto.BillsTypeDTO;

import java.util.List;

public interface IBillsTypeService {
    BillsTypeDTO create(BillsTypeDTO dto);
    BillsTypeDTO update(Integer id, BillsTypeDTO dto);
    BillsTypeDTO getById(Integer id);
    List<BillsTypeDTO> getAll();
    void delete(Integer id);
}
