package com.teletrabajo.service.impl;

import com.teletrabajo.dto.PrevitionDTO;
import com.teletrabajo.entity.PrevitionEntity;
import com.teletrabajo.repository.ActivityRepository;
import com.teletrabajo.repository.PrevitionRepository;
import com.teletrabajo.service.IActivityService;
import com.teletrabajo.service.IPrevitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrevitionServiceImpl implements IPrevitionService {

    @Autowired
    private PrevitionRepository repository;


    private PrevitionDTO mapToDTO(PrevitionEntity entity) {
        return PrevitionDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }

    private PrevitionEntity mapToEntity(PrevitionDTO dto) {
        return PrevitionEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .deletedAt(dto.getDeletedAt())
                .build();
    }


    

    public PrevitionDTO create(PrevitionDTO dto) {
        PrevitionEntity entity = repository.save(mapToEntity(dto));
        return mapToDTO(entity);
    }

    @Override
    public PrevitionDTO update(Integer id, PrevitionDTO dto) {
        PrevitionEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        entity.setName(dto.getName());
        return mapToDTO(repository.save(entity));
    }

    @Override
    public PrevitionDTO getById(Integer id) {
        PrevitionEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return mapToDTO(entity);
    }

    @Override
    public List<PrevitionDTO> getAll() {
        return repository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }
    public Page<PrevitionDTO> getAllPaginated(Pageable pageable) {
        return repository.findAllPaginated(pageable)
                .map(this::mapToDTO);
    }

    public Page<PrevitionDTO> getAllPaginated(String name, Pageable pageable) {
        return repository.search(name, pageable).map(this::mapToDTO);
    }





    /*Listar communas activas*/
    public List<PrevitionDTO> listAll() {
        return repository.findAllIncludingDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<PrevitionDTO> listActive() {
        return repository.findAllActive().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }



    public List<PrevitionDTO> listDeleted() {
        return repository.findAllDeleted().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public void restore(Integer id) {
        PrevitionEntity entity = repository.findAnyById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

   
}
