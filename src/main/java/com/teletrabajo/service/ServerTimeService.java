package com.teletrabajo.service;

import com.teletrabajo.dto.ServerTimeResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ServerTimeService {

    public ServerTimeResponseDTO getServerTime(){

        LocalDateTime now = LocalDateTime.now();

        return ServerTimeResponseDTO.builder()
                .date(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .time(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                .dateTime(now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .timezone(ZoneId.systemDefault().toString())
                .build();
    }
}
