package com.teletrabajo.service.impl;

import com.teletrabajo.dto.GroupDTO;
import com.teletrabajo.dto.RoleDTO;
import com.teletrabajo.dto.UserDTO;
import com.teletrabajo.dto.UserGroupDTO;
import com.teletrabajo.entity.GroupEntity;
import com.teletrabajo.entity.RoleEntity;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.entity.UserGroupEntity;
import com.teletrabajo.repository.UserGroupRepository;
import com.teletrabajo.service.IUserGroupService;
import com.teletrabajo.service.IUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGroupServiceImpl implements IUserGroupService {

    @Autowired
    private UserGroupRepository repository;


    private UserGroupDTO mapToDTO(UserGroupEntity entity) {
        return UserGroupDTO.builder()
                .id(entity.getId())
                .user(mapToUserDTO(entity.getUser()))
                .group(mapToGroupDTO(entity.getGroup()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserGroupEntity mapToEntity(UserGroupDTO dto) {
        return UserGroupEntity.builder()
                .id(dto.getId())
                .user(mapToUserEntity(dto.getUser()))
                .group(mapToGroupEntity(dto.getGroup()))
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

    private GroupDTO mapToGroupDTO(GroupEntity entity) {
        return GroupDTO.builder()
                .id(entity.getId())
                .user(entity.getUser() != null ? mapToUserDTO(entity.getUser()) : null)
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private GroupEntity mapToGroupEntity(GroupDTO dto) {
        return GroupEntity.builder()
                .id(dto.getId())
                .user(dto.getUser() != null ? mapToUserEntity(dto.getUser()) : null)
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    public UserGroupDTO create(UserGroupDTO dto) {
        UserGroupEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public UserGroupDTO update(Integer id, UserGroupDTO dto) {
        UserGroupEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setUser(mapToUserEntity(dto.getUser()));
        entity.setGroup(mapToGroupEntity(dto.getGroup()));
        return mapToDTO(repository.save(entity));
    }

    @Override
    public UserGroupDTO getById(Integer id) {
        UserGroupEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<UserGroupDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<UserGroupDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<UserGroupDTO> getAllPaginated(Integer id, Pageable pageable) {
        return repository.search(id, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<UserGroupDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<UserGroupDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<UserGroupDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        UserGroupEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    public List<UserGroupDTO> getRolesByUser(Integer userId) {
        return repository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteByUserId(Integer userId) {
        repository.softDeleteByUserId(userId);
    }

    @Transactional
    public void deleteByUserAndRole(Integer userId, Integer groupId) {
        UserGroupEntity entity = repository.findByUserIdAndgroupId(userId, groupId)
                .orElseThrow(() -> new RuntimeException("La relación usuario-rol no existe o ya fue eliminada."));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }
}
