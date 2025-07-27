package com.github.lukashindy.booking.dto;

import lombok.Data;

@Data
public class RoomDto {
    private Long id;
    private Long roomTypeId;
    private Long hotelId;
    private String roomNumber;
}
