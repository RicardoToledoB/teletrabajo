package com.teletrabajo.service;

import com.teletrabajo.dto.WorkDTO;

import java.util.List;

public interface IWorkService {
    WorkDTO create(WorkDTO dto);
    WorkDTO update(Integer id, WorkDTO dto);
    WorkDTO getById(Integer id);
    List<WorkDTO> getAll();
    void delete(Integer id);
}
