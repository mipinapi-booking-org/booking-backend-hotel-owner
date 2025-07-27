package com.github.lukashindy.booking.dto;

import lombok.Data;

@Data
public class RoomTypeDto {
    private Long id;
    private Long hotelId;
    private String name;
    private Integer capacity;
}
