package com.teletrabajo.service.impl;

import com.teletrabajo.dto.WorkPlaceDTO;
import com.teletrabajo.entity.WorkPlaceEntity;
import com.teletrabajo.repository.ActivityRepository;
import com.teletrabajo.repository.WorkPlaceRepository;
import com.teletrabajo.service.IActivityService;
import com.teletrabajo.service.IWorkPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkPlaceServiceImpl implements IWorkPlaceService {

    @Autowired
    private WorkPlaceRepository repository;


    private WorkPlaceDTO mapToDTO(WorkPlaceEntity entity) {
        return WorkPlaceDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private WorkPlaceEntity mapToEntity(WorkPlaceDTO dto) {
        return WorkPlaceEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    

    public WorkPlaceDTO create(WorkPlaceDTO dto) {
        WorkPlaceEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public WorkPlaceDTO update(Integer id, WorkPlaceDTO dto) {
        WorkPlaceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        entity.setName(dto.getName());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public WorkPlaceDTO getById(Integer id) {
        WorkPlaceEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<WorkPlaceDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<WorkPlaceDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<WorkPlaceDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<WorkPlaceDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<WorkPlaceDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<WorkPlaceDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        WorkPlaceEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

   
}
