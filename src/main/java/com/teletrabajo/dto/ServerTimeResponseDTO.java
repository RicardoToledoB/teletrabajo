package com.teletrabajo.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerTimeResponseDTO {

    private String date;
    private String time;
    private String dateTime;
    private String timezone;
}
