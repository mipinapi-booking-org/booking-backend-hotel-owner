package com.github.lukashindy.booking.dto;

import lombok.Data;

@Data
public class RoomWithDetailsDto {
    private Long id;
    private String roomNumber;
    private Long roomTypeId;
    private String roomTypeName;
    private Integer roomTypeCapacity;
    private Long hotelId;
    private String hotelName;
    private String hotelCity;
    private String hotelCountry;
}
