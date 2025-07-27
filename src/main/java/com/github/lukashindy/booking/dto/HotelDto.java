package com.github.lukashindy.booking.dto;

import lombok.Data;

@Data
public class HotelDto {
    private Long id;
    private String name;
    private String country;
    private String city;
    private String street;
    private Long ownerId;
}
