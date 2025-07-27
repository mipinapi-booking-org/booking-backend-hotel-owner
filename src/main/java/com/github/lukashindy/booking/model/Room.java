package com.github.lukashindy.booking.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", foreignKey = @ForeignKey(name = "fk_room_roomtype"))
    private RoomType roomType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", foreignKey = @ForeignKey(name = "fk_room_hotel"))
    private Hotel hotel;

    private String roomNumber;
}
