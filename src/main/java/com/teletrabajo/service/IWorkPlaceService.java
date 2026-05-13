package com.teletrabajo.service;

import com.teletrabajo.dto.WorkPlaceDTO;

import java.util.List;

public interface IWorkPlaceService {
    WorkPlaceDTO create(WorkPlaceDTO dto);
    WorkPlaceDTO update(Integer id, WorkPlaceDTO dto);
    WorkPlaceDTO getById(Integer id);
    List<WorkPlaceDTO> getAll();
    void delete(Integer id);
}
