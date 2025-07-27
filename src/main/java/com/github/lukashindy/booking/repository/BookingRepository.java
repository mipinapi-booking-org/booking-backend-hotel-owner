package com.github.lukashindy.booking.repository;

import com.github.lukashindy.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByStatus(Booking.Status status);
    
    /**
     * Найти все бронирования для отелей, к которым имеет доступ пользователь
     */
    @Query("SELECT b FROM Booking b " +
           "JOIN b.room r " +
           "JOIN r.hotel h " +
           "JOIN h.ownerAccesses ha " +
           "WHERE ha.hotelOwner.id = :hotelOwnerId")
    List<Booking> findBookingsForHotelOwner(@Param("hotelOwnerId") UUID hotelOwnerId);
    
    /**
     * Найти все бронирования для конкретного отеля
     */
    @Query("SELECT b FROM Booking b " +
           "JOIN b.room r " +
           "WHERE r.hotel.id = :hotelId")
    List<Booking> findBookingsByHotelId(@Param("hotelId") Long hotelId);
}
