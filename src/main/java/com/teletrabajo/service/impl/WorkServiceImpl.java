package com.teletrabajo.service.impl;

import com.teletrabajo.dto.SubscribeDTO;
import com.teletrabajo.dto.WorkDTO;
import com.teletrabajo.dto.UserDTO;
import com.teletrabajo.entity.SubscribeEntity;
import com.teletrabajo.entity.WorkEntity;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.repository.RegisterRepository;
import com.teletrabajo.repository.WorkRepository;
import com.teletrabajo.service.IRegisterService;
import com.teletrabajo.service.IWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkServiceImpl implements IWorkService {

    @Autowired
    private WorkRepository repository;


    private WorkDTO mapToDTO(WorkEntity entity) {
        return WorkDTO.builder()
                .id(entity.getId())
                .user(entity.getUser() != null ? mapToUserDTO(entity.getUser()) : null)
                .subscribe(entity.getSubscribe() != null ? mapToSubscribeDTO(entity.getSubscribe()) : null)
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private WorkEntity mapToEntity(WorkDTO dto) {
        return WorkEntity.builder()
                .id(dto.getId())
                .user(dto.getUser() != null ? mapToUserEntity(dto.getUser()) : null)
                .subscribe(dto.getSubscribe() != null ? mapToSubscribeEntity(dto.getSubscribe()) : null)
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    private SubscribeDTO mapToSubscribeDTO(SubscribeEntity entity) {
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

    private SubscribeEntity mapToSubscribeEntity(SubscribeDTO dto) {
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



    public WorkDTO create(WorkDTO dto) {
        WorkEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public WorkDTO update(Integer id, WorkDTO dto) {
        WorkEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        entity.setUser(mapToUserEntity(dto.getUser()));
        entity.setSubscribe(mapToSubscribeEntity(dto.getSubscribe()));
        entity.setDescription(dto.getDescription());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public WorkDTO getById(Integer id) {
        WorkEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<WorkDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<WorkDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }







    /*Listar communas activas*/
    public List<WorkDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<WorkDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<WorkDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        WorkEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public Page<WorkDTO> findByUserId(Integer userId, Pageable pageable) {
        return repository.findByUserId(userId, pageable)
                .map(this::mapToDTO);
    }
}
