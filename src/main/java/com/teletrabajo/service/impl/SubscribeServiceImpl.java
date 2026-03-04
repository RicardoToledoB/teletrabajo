package com.teletrabajo.service.impl;

import com.teletrabajo.dto.SubscribeDTO;
import com.teletrabajo.dto.UserDTO;
import com.teletrabajo.entity.SubscribeEntity;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.repository.SubscribeRepository;
import com.teletrabajo.service.ISubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscribeServiceImpl implements ISubscribeService {

    @Autowired
    private SubscribeRepository repository;


    private SubscribeDTO mapToDTO(SubscribeEntity entity) {
        return SubscribeDTO.builder()
                .id(entity.getId())
                .begin(entity.getBegin())
                .end(entity.getEnd())
                .user(entity.getUser() != null ? mapToUserDTO(entity.getUser()) : null)
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private SubscribeEntity mapToEntity(SubscribeDTO dto) {
        return SubscribeEntity.builder()
                .id(dto.getId())
                .begin(dto.getBegin())
                .end(dto.getEnd())
                .user(dto.getUser() != null ? mapToUserEntity(dto.getUser()) : null)
                .active(dto.isActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private UserDTO mapToUserDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .secondName(entity.getSecondName())
                .firstLastName(entity.getFirstLastName())
                .secondLastName(entity.getSecondLastName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(entity.getPassword())
                .rut(entity.getRut())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserEntity mapToUserEntity(UserDTO dto) {
        return UserEntity.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .secondName(dto.getSecondName())
                .firstLastName(dto.getFirstLastName())
                .secondLastName(dto.getSecondLastName())
                .email(dto.getEmail())
                .username(dto.getUsername())
                .password(dto.getPassword())
                .rut(dto.getRut())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }




    public SubscribeDTO create(SubscribeDTO dto) {
        SubscribeEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public SubscribeDTO update(Integer id, SubscribeDTO dto) {
        SubscribeEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setBegin(dto.getBegin());
        entity.setEnd(dto.getEnd());
        entity.setUser(mapToUserEntity(dto.getUser()));
        entity.setActive(dto.isActive());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public SubscribeDTO getById(Integer id) {
        SubscribeEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<SubscribeDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<SubscribeDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<SubscribeDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<SubscribeDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<SubscribeDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<SubscribeDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        SubscribeEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
