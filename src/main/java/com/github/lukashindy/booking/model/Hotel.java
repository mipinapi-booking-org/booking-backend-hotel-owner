package com.github.lukashindy.booking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_hotel_owner"))
    private HotelOwner owner;

    private String name;
    private String country;
    private String city;
    private String street;
}
