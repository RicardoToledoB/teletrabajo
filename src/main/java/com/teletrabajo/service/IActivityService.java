package com.teletrabajo.service;

import com.teletrabajo.dto.ActivityDTO;

import java.util.List;

public interface IActivityService {
    ActivityDTO create(ActivityDTO dto);
    ActivityDTO update(Integer id, ActivityDTO dto);
    ActivityDTO getById(Integer id);
    List<ActivityDTO> getAll();
    void delete(Integer id);
}
