package com.teletrabajo.service.impl;

import com.teletrabajo.dto.CivilStateDTO;
import com.teletrabajo.entity.CivilStateEntity;
import com.teletrabajo.repository.CivilStateRepository;
import com.teletrabajo.repository.ParentTypeRepository;
import com.teletrabajo.service.ICivilStateService;
import com.teletrabajo.service.IParentTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CivilStateServiceImpl implements ICivilStateService {

    @Autowired
    private CivilStateRepository repository;


    private CivilStateDTO mapToDTO(CivilStateEntity entity) {
        return CivilStateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private CivilStateEntity mapToEntity(CivilStateDTO dto) {
        return CivilStateEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    

    public CivilStateDTO create(CivilStateDTO dto) {
        CivilStateEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public CivilStateDTO update(Integer id, CivilStateDTO dto) {
        CivilStateEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        entity.setName(dto.getName());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public CivilStateDTO getById(Integer id) {
        CivilStateEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<CivilStateDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<CivilStateDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<CivilStateDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<CivilStateDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<CivilStateDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<CivilStateDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        CivilStateEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

   
}
