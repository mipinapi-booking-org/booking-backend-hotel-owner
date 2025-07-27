package com.github.lukashindy.booking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hotel_owner_access", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_owner_id", "hotel_id"}))
@Data
public class HotelOwnerAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_owner_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_hotel_owner_access_owner"))
    private HotelOwner hotelOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_hotel_owner_access_hotel"))
    private Hotel hotel;

    @Column(name = "granted_at", nullable = false)
    private LocalDateTime grantedAt;

    @Column(name = "granted_by")
    private UUID grantedBy; // ID администратора, который предоставил доступ

    @Enumerated(EnumType.STRING)
    @Column(name = "access_level", nullable = false)
    private AccessLevel accessLevel = AccessLevel.MANAGER;

    public enum AccessLevel {
        OWNER,      // Полный доступ (владелец отеля)
        MANAGER,    // Управление бронированиями
        VIEWER      // Только просмотр
    }
}
