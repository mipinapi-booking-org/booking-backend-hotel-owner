package com.github.lukashindy.booking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", foreignKey = @ForeignKey(name = "fk_roomtype_hotel"))
    private Hotel hotel;

    private String name;
    private Integer capacity;
}
