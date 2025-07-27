package com.github.lukashindy.booking.repository;

import com.github.lukashindy.booking.model.HotelOwnerAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HotelOwnerAccessRepository extends JpaRepository<HotelOwnerAccess, Long> {
    
    /**
     * Найти все отели, к которым имеет доступ пользователь
     */
    @Query("SELECT h FROM HotelOwnerAccess h WHERE h.hotelOwner.id = :hotelOwnerId")
    List<HotelOwnerAccess> findByHotelOwnerId(@Param("hotelOwnerId") UUID hotelOwnerId);
    
    /**
     * Проверить, имеет ли пользователь доступ к конкретному отелю
     */
    @Query("SELECT h FROM HotelOwnerAccess h WHERE h.hotelOwner.id = :hotelOwnerId AND h.hotel.id = :hotelId")
    Optional<HotelOwnerAccess> findByHotelOwnerIdAndHotelId(
        @Param("hotelOwnerId") UUID hotelOwnerId, 
        @Param("hotelId") Long hotelId);
    
    /**
     * Найти все доступы для конкретного отеля
     */
    @Query("SELECT h FROM HotelOwnerAccess h WHERE h.hotel.id = :hotelId")
    List<HotelOwnerAccess> findByHotelId(@Param("hotelId") Long hotelId);
    
    /**
     * Проверить, имеет ли пользователь доступ к отелю с минимальным уровнем доступа
     */
    @Query("SELECT h FROM HotelOwnerAccess h WHERE h.hotelOwner.id = :hotelOwnerId AND h.hotel.id = :hotelId AND h.accessLevel IN :minAccessLevels")
    Optional<HotelOwnerAccess> findByHotelOwnerIdAndHotelIdAndAccessLevelIn(
        @Param("hotelOwnerId") UUID hotelOwnerId, 
        @Param("hotelId") Long hotelId, 
        @Param("minAccessLevels") List<HotelOwnerAccess.AccessLevel> minAccessLevels);
}
