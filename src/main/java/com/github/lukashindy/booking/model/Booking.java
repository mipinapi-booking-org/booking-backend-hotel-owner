package com.github.lukashindy.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_booking_room"))
    private Room room;

    private UUID clientId;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    @Lob
    private String guestFullNames;
    @Lob
    private String specialRequests;
    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", foreignKey = @ForeignKey(name = "fk_booking_updated_by"))
    private HotelOwner updatedBy;

    private LocalDateTime lastUpdatedDate;

    public enum Status {
        CREATED, CONFIRMED, CANCELLED, COMPLETED
    }
}
