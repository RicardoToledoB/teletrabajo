package com.teletrabajo.service;

import com.teletrabajo.dto.SubscribeDTO;

import java.util.List;

public interface ISubscribeService {

    SubscribeDTO create(SubscribeDTO dto);
    SubscribeDTO update(Integer id, SubscribeDTO dto);
    SubscribeDTO getById(Integer id);
    List<SubscribeDTO> getAll();
    void delete(Integer id);
}
