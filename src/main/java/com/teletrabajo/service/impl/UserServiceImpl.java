package com.teletrabajo.service.impl;

import com.teletrabajo.dto.ChangePasswordDTO;
import com.teletrabajo.dto.UserDTO;
import com.teletrabajo.entity.UserEntity;
import com.teletrabajo.repository.UserRepository;
import com.teletrabajo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UserDTO mapToDTO(UserEntity entity) {
        return UserDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .secondName(entity.getSecondName())
                .firstLastName(entity.getFirstLastName())
                .secondLastName(entity.getSecondLastName())
                .email(entity.getEmail())
                .username(entity.getUsername())
                .password(null)
                .rut(entity.getRut())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private UserEntity mapToEntity(UserDTO dto) {
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

    public UserDTO create(UserDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("La contraseña no puede estar vacía");
        }

        UserEntity entity = mapToEntity(dto);
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity = repository.save(entity);
        return mapToDTO(entity);
    }

    @Override
    public UserDTO update(Integer id, UserDTO dto) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        entity.setFirstName(dto.getFirstName());
        entity.setSecondName(dto.getSecondName());
        entity.setFirstLastName(dto.getFirstLastName());
        entity.setSecondLastName(dto.getSecondLastName());
        entity.setEmail(dto.getEmail());
        entity.setUsername(dto.getUsername());
        entity.setRut(dto.getRut());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return mapToDTO(repository.save(entity));
    }

    @Override
    public UserDTO getById(Integer id) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return mapToDTO(entity);
    }

    @Override
    public List<UserDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> findByEmail(String email) {
        return repository.findByEmail(email).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public Page<UserDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public List<UserDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        UserEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    @Override
    public void changePassword(Integer id, ChangePasswordDTO dto) {
        UserEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getCurrentPassword() == null || dto.getCurrentPassword().isBlank()) {
            throw new RuntimeException("La contraseña actual es obligatoria");
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().isBlank()) {
            throw new RuntimeException("La nueva contraseña es obligatoria");
        }

        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().isBlank()) {
            throw new RuntimeException("La confirmación de contraseña es obligatoria");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), entity.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña actual es incorrecta");
        }

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("La nueva contraseña y su confirmación no coinciden");
        }

        if (passwordEncoder.matches(dto.getNewPassword(), entity.getPassword())) {
            throw new RuntimeException("La nueva contraseña no puede ser igual a la actual");
        }

        entity.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(entity);
    }
}