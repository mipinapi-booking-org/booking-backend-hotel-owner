package com.github.lukashindy.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String country;
    private String city;
    private String street;

    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    private List<HotelOwnerAccess> ownerAccesses;
}
