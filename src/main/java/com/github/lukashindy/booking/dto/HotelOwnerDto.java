package com.github.lukashindy.booking.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class HotelOwnerDto {
    private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private String role;
}
