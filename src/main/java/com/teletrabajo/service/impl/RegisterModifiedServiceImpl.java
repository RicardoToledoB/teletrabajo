package com.teletrabajo.service.impl;

import com.teletrabajo.dto.RegisterDTO;
import com.teletrabajo.dto.RegisterModifiedDTO;
import com.teletrabajo.dto.UserDTO;
import com.teletrabajo.entity.RegisterEntity;
import com.teletrabajo.entity.RegisterModifiedEntity;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.repository.RegisterModifiedRepository;
import com.teletrabajo.repository.RegisterRepository;
import com.teletrabajo.service.IRegisterModifiedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RegisterModifiedServiceImpl implements IRegisterModifiedService {

    @Autowired
    private RegisterModifiedRepository repository;


    private RegisterModifiedDTO mapToDTO(RegisterModifiedEntity entity) {
        return RegisterModifiedDTO.builder()
                .id(entity.getId())
                .user(entity.getUser() != null ? mapToUserDTO(entity.getUser()) : null)
                .register(entity.getRegister() != null ? mapToRegisterDTO(entity.getRegister()) : null)
                .administrator(entity.getAdministrator() != null ? mapToUserDTO(entity.getUser()) : null)
                .state(entity.getState())
                .observations(entity.getObservations())
                .register_datetime(entity.getRegister_datetime())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private RegisterModifiedEntity mapToEntity(RegisterModifiedDTO dto) {
        return RegisterModifiedEntity.builder()
                .id(dto.getId())
                .register(dto.getRegister() != null ? mapToRegisterEntity(dto.getRegister()) : null)
                .user(dto.getUser() != null ? mapToUserEntity(dto.getUser()) : null)
                .administrator(dto.getAdministrator() != null ? mapToUserEntity(dto.getUser()) : null)
                .state(dto.getState())
                .observations(dto.getObservations())
                .register_datetime(dto.getRegister_datetime())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }

    private RegisterDTO mapToRegisterDTO(RegisterEntity entity) {
        return RegisterDTO.builder()
                .id(entity.getId())
                .user(entity.getUser() != null ? mapToUserDTO(entity.getUser()) : null)
                .state(entity.getState())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private RegisterEntity mapToRegisterEntity(RegisterDTO dto) {
        return RegisterEntity.builder()
                .id(dto.getId())
                .user(dto.getUser() != null ? mapToUserEntity(dto.getUser()) : null)
                .state(dto.getState())
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




    public RegisterModifiedDTO create(RegisterModifiedDTO dto) {
        RegisterModifiedEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    public RegisterModifiedDTO update(Integer id, RegisterModifiedDTO dto) {
        RegisterModifiedEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setRegister(mapToRegisterEntity(dto.getRegister()));
        entity.setAdministrator(mapToUserEntity(dto.getAdministrator()));
        entity.setUser(mapToUserEntity(dto.getUser()));
        entity.setObservations(dto.getObservations());
        entity.setState(dto.getState());
        entity.setRegister_datetime(dto.getRegister_datetime());
        return mapToDTO(repository.save(entity));
    }

    public RegisterModifiedDTO getById(Integer id) {
        RegisterModifiedEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    public List<RegisterModifiedDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<RegisterModifiedDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<RegisterModifiedDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<RegisterModifiedDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<RegisterModifiedDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<RegisterModifiedDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        RegisterModifiedEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }
}
