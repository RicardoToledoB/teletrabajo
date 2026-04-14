package com.teletrabajo.service.impl;

import com.teletrabajo.dto.StudyDTO;
import com.teletrabajo.entity.StudyEntity;
import com.teletrabajo.repository.ActivityRepository;
import com.teletrabajo.repository.StudyRepository;
import com.teletrabajo.service.IActivityService;
import com.teletrabajo.service.IStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyServiceImpl implements IStudyService {

    @Autowired
    private StudyRepository repository;


    private StudyDTO mapToDTO(StudyEntity entity) {
        return StudyDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private StudyEntity mapToEntity(StudyDTO dto) {
        return StudyEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    

    public StudyDTO create(StudyDTO dto) {
        StudyEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public StudyDTO update(Integer id, StudyDTO dto) {
        StudyEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        entity.setName(dto.getName());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public StudyDTO getById(Integer id) {
        StudyEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<StudyDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<StudyDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<StudyDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<StudyDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<StudyDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<StudyDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        StudyEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

   
}
