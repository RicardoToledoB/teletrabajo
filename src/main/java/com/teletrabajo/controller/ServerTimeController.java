package com.teletrabajo.controller;


import com.teletrabajo.dto.ServerTimeResponseDTO;
import com.teletrabajo.service.ServerTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/time")
@RequiredArgsConstructor
public class ServerTimeController {

    private final ServerTimeService serverTimeService;

    @GetMapping("/server")
    public ServerTimeResponseDTO getServerTime(){
        return serverTimeService.getServerTime();
    }
}
