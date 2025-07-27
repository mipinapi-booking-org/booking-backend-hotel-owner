package com.github.lukashindy.booking.service;

import com.github.lukashindy.booking.dto.BookingDto;
import com.github.lukashindy.booking.mapper.BookingMapper;
import com.github.lukashindy.booking.model.Booking;
import com.github.lukashindy.booking.model.HotelOwner;
import com.github.lukashindy.booking.repository.BookingRepository;
import com.github.lukashindy.booking.repository.HotelOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private HotelOwnerRepository hotelOwnerRepository;
    
    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private AccessControlService accessControlService;

    /**
     * Получить все бронирования для отелей, к которым имеет доступ пользователь
     */
    public List<BookingDto> getAllBookingsForUser(UUID hotelOwnerId) {
        List<Booking> bookings = bookingRepository.findBookingsForHotelOwner(hotelOwnerId);
        return bookingMapper.toDto(bookings);
    }

    /**
     * Получить все бронирования
     */
    public List<BookingDto> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookingMapper.toDto(bookings);
    }

    /**
     * Получить бронирование по ID
     */
    public Optional<BookingDto> getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(bookingMapper::toDto);
    }

    /**
     * Подтвердить бронирование (CONFIRMED) с проверкой прав доступа
     */
    public BookingDto confirmBookingWithAccessControl(Long bookingId, UUID hotelOwnerId) {
        Booking booking = getBookingEntity(bookingId);
        Long hotelId = booking.getRoom().getHotel().getId();
        
        if (!accessControlService.canManageBookings(hotelOwnerId, hotelId)) {
            throw new SecurityException("У вас нет прав для управления бронированиями в этом отеле");
        }
        
        return confirmBooking(bookingId, hotelOwnerId);
    }

    /**
     * Подтвердить бронирование (CONFIRMED)
     */
    public BookingDto confirmBooking(Long bookingId, UUID hotelOwnerId) {
        Booking booking = getBookingEntity(bookingId);
        HotelOwner hotelOwner = getHotelOwnerEntity(hotelOwnerId);
        
        validateStatusTransition(booking.getStatus(), Booking.Status.CONFIRMED);
        
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.setUpdatedBy(hotelOwner);
        booking.setLastUpdatedDate(LocalDateTime.now());
        booking.setRefuseReason(null); // Очищаем причину отказа при подтверждении
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    /**
     * Отклонить бронирование (REFUSED) с указанием причины и проверкой прав доступа
     */
    public BookingDto refuseBookingWithAccessControl(Long bookingId, UUID hotelOwnerId, String refuseReason) {
        Booking booking = getBookingEntity(bookingId);
        Long hotelId = booking.getRoom().getHotel().getId();
        
        if (!accessControlService.canManageBookings(hotelOwnerId, hotelId)) {
            throw new SecurityException("У вас нет прав для управления бронированиями в этом отеле");
        }
        
        return refuseBooking(bookingId, hotelOwnerId, refuseReason);
    }

    /**
     * Отклонить бронирование (REFUSED) с указанием причины
     */
    public BookingDto refuseBooking(Long bookingId, UUID hotelOwnerId, String refuseReason) {
        if (refuseReason == null || refuseReason.trim().isEmpty()) {
            throw new IllegalArgumentException("Причина отказа обязательна для указания");
        }
        
        Booking booking = getBookingEntity(bookingId);
        HotelOwner hotelOwner = getHotelOwnerEntity(hotelOwnerId);
        
        validateStatusTransition(booking.getStatus(), Booking.Status.REFUSED);
        
        booking.setStatus(Booking.Status.REFUSED);
        booking.setRefuseReason(refuseReason.trim());
        booking.setUpdatedBy(hotelOwner);
        booking.setLastUpdatedDate(LocalDateTime.now());
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    /**
     * Завершить бронирование (COMPLETED) с проверкой прав доступа - только для подтвержденных заказов
     */
    public BookingDto completeBookingWithAccessControl(Long bookingId, UUID hotelOwnerId) {
        Booking booking = getBookingEntity(bookingId);
        Long hotelId = booking.getRoom().getHotel().getId();
        
        if (!accessControlService.canManageBookings(hotelOwnerId, hotelId)) {
            throw new SecurityException("У вас нет прав для управления бронированиями в этом отеле");
        }
        
        return completeBooking(bookingId, hotelOwnerId);
    }

    /**
     * Завершить бронирование (COMPLETED) - только для подтвержденных заказов
     */
    public BookingDto completeBooking(Long bookingId, UUID hotelOwnerId) {
        Booking booking = getBookingEntity(bookingId);
        HotelOwner hotelOwner = getHotelOwnerEntity(hotelOwnerId);
        
        if (booking.getStatus() != Booking.Status.CONFIRMED) {
            throw new IllegalStateException("Можно завершить только подтвержденное бронирование");
        }
        
        booking.setStatus(Booking.Status.COMPLETED);
        booking.setUpdatedBy(hotelOwner);
        booking.setLastUpdatedDate(LocalDateTime.now());
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    /**
     * Отменить бронирование (CANCELLED) с проверкой прав доступа
     */
    public BookingDto cancelBookingWithAccessControl(Long bookingId, UUID hotelOwnerId) {
        Booking booking = getBookingEntity(bookingId);
        Long hotelId = booking.getRoom().getHotel().getId();
        
        if (!accessControlService.canManageBookings(hotelOwnerId, hotelId)) {
            throw new SecurityException("У вас нет прав для управления бронированиями в этом отеле");
        }
        
        return cancelBooking(bookingId, hotelOwnerId);
    }

    /**
     * Отменить бронирование (CANCELLED)
     */
    public BookingDto cancelBooking(Long bookingId, UUID hotelOwnerId) {
        Booking booking = getBookingEntity(bookingId);
        HotelOwner hotelOwner = getHotelOwnerEntity(hotelOwnerId);
        
        validateStatusTransition(booking.getStatus(), Booking.Status.CANCELLED);
        
        booking.setStatus(Booking.Status.CANCELLED);
        booking.setUpdatedBy(hotelOwner);
        booking.setLastUpdatedDate(LocalDateTime.now());
        
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    /**
     * Получить бронирования по статусу для пользователя с доступом
     */
    public List<BookingDto> getBookingsByStatusForUser(Booking.Status status, UUID hotelOwnerId) {
        List<Booking> bookings = bookingRepository.findBookingsForHotelOwner(hotelOwnerId);
        List<Booking> filteredBookings = bookings.stream()
            .filter(booking -> booking.getStatus() == status)
            .toList();
        return bookingMapper.toDto(filteredBookings);
    }

    /**
     * Получить бронирования по статусу
     */
    public List<BookingDto> getBookingsByStatus(Booking.Status status) {
        List<Booking> bookings = bookingRepository.findByStatus(status);
        return bookingMapper.toDto(bookings);
    }

    // Вспомогательные методы

    private Booking getBookingEntity(Long bookingId) {
        return bookingRepository.findById(bookingId)
            .orElseThrow(() -> new IllegalArgumentException("Бронирование с ID " + bookingId + " не найдено"));
    }

    private HotelOwner getHotelOwnerEntity(UUID hotelOwnerId) {
        return hotelOwnerRepository.findById(hotelOwnerId)
            .orElseThrow(() -> new IllegalArgumentException("Сотрудник отеля с ID " + hotelOwnerId + " не найден"));
    }

    private void validateStatusTransition(Booking.Status currentStatus, Booking.Status newStatus) {
        // Проверяем допустимые переходы статусов
        switch (currentStatus) {
            case CREATED:
                if (newStatus != Booking.Status.CONFIRMED && 
                    newStatus != Booking.Status.REFUSED && 
                    newStatus != Booking.Status.CANCELLED) {
                    throw new IllegalStateException("Из статуса CREATED можно перейти только в CONFIRMED, REFUSED или CANCELLED");
                }
                break;
            case CONFIRMED:
                if (newStatus != Booking.Status.COMPLETED && 
                    newStatus != Booking.Status.CANCELLED) {
                    throw new IllegalStateException("Из статуса CONFIRMED можно перейти только в COMPLETED или CANCELLED");
                }
                break;
            case REFUSED:
            case CANCELLED:
            case COMPLETED:
                throw new IllegalStateException("Нельзя изменить статус завершенного, отклоненного или отмененного бронирования");
            default:
                throw new IllegalStateException("Неизвестный статус: " + currentStatus);
        }
    }
}
