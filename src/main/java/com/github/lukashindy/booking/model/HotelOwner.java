package com.github.lukashindy.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Entity
public class HotelOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String email;
    private String passwordHash;
    private String role;

    @OneToMany(mappedBy = "hotelOwner", fetch = FetchType.LAZY)
    private List<HotelOwnerAccess> hotelAccesses;
}
