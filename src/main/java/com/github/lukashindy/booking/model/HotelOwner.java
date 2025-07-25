package com.github.lukashindy.booking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class HotelOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String passwordHash;
    private String role;
}
