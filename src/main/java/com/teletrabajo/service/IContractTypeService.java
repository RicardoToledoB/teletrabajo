package com.teletrabajo.service;

import com.teletrabajo.dto.ContractTypeDTO;

import java.util.List;

public interface IContractTypeService {
    ContractTypeDTO create(ContractTypeDTO dto);
    ContractTypeDTO update(Integer id, ContractTypeDTO dto);
    ContractTypeDTO getById(Integer id);
    List<ContractTypeDTO> getAll();
    void delete(Integer id);
}
