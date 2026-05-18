package com.teletrabajo.service;

import com.teletrabajo.dto.StudyDTO;

import java.util.List;

public interface IStudyService {
    StudyDTO create(StudyDTO dto);
    StudyDTO update(Integer id, StudyDTO dto);
    StudyDTO getById(Integer id);
    List<StudyDTO> getAll();
    void delete(Integer id);
}
