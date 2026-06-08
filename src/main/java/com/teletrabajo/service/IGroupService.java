package com.teletrabajo.service;

import com.teletrabajo.dto.GroupDTO;

import java.util.List;

public interface IGroupService {
    GroupDTO create(GroupDTO dto);
    GroupDTO update(Integer id, GroupDTO dto);
    GroupDTO getById(Integer id);
    List<GroupDTO> getAll();
    void delete(Integer id);
}
