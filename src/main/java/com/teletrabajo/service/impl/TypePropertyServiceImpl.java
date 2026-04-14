package com.teletrabajo.service.impl;

import com.teletrabajo.dto.TypePropertyDTO;
import com.teletrabajo.entity.TypePropertyEntity;
import com.teletrabajo.repository.ActivityRepository;
import com.teletrabajo.repository.TypePropertyRepository;
import com.teletrabajo.service.IActivityService;
import com.teletrabajo.service.ITypePropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TypePropertyServiceImpl implements ITypePropertyService {

    @Autowired
    private TypePropertyRepository repository;


    private TypePropertyDTO mapToDTO(TypePropertyEntity entity) {
        return TypePropertyDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private TypePropertyEntity mapToEntity(TypePropertyDTO dto) {
        return TypePropertyEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    

    public TypePropertyDTO create(TypePropertyDTO dto) {
        TypePropertyEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public TypePropertyDTO update(Integer id, TypePropertyDTO dto) {
        TypePropertyEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        entity.setName(dto.getName());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public TypePropertyDTO getById(Integer id) {
        TypePropertyEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<TypePropertyDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<TypePropertyDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<TypePropertyDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<TypePropertyDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<TypePropertyDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<TypePropertyDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        TypePropertyEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

   
}
