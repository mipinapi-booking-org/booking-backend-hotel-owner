package com.github.lukashindy.booking.service;

import com.github.lukashindy.booking.model.HotelOwnerAccess;
import com.github.lukashindy.booking.repository.HotelOwnerAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccessControlService {

    @Autowired
    private HotelOwnerAccessRepository hotelOwnerAccessRepository;

    /**
     * Проверить, имеет ли пользователь доступ к отелю
     */
    public boolean hasAccessToHotel(UUID hotelOwnerId, Long hotelId) {
        return hotelOwnerAccessRepository
            .findByHotelOwnerIdAndHotelId(hotelOwnerId, hotelId)
            .isPresent();
    }

    /**
     * Проверить, имеет ли пользователь право на управление бронированиями в отеле
     */
    public boolean canManageBookings(UUID hotelOwnerId, Long hotelId) {
        List<HotelOwnerAccess.AccessLevel> managerLevels = Arrays.asList(
            HotelOwnerAccess.AccessLevel.OWNER,
            HotelOwnerAccess.AccessLevel.MANAGER
        );
        
        return hotelOwnerAccessRepository
            .findByHotelOwnerIdAndHotelIdAndAccessLevelIn(hotelOwnerId, hotelId, managerLevels)
            .isPresent();
    }

    /**
     * Проверить, является ли пользователь владельцем отеля
     */
    public boolean isOwner(UUID hotelOwnerId, Long hotelId) {
        List<HotelOwnerAccess.AccessLevel> ownerLevels = Arrays.asList(
            HotelOwnerAccess.AccessLevel.OWNER
        );
        
        return hotelOwnerAccessRepository
            .findByHotelOwnerIdAndHotelIdAndAccessLevelIn(hotelOwnerId, hotelId, ownerLevels)
            .isPresent();
    }

    /**
     * Получить уровень доступа пользователя к отелю
     */
    public Optional<HotelOwnerAccess.AccessLevel> getAccessLevel(UUID hotelOwnerId, Long hotelId) {
        return hotelOwnerAccessRepository
            .findByHotelOwnerIdAndHotelId(hotelOwnerId, hotelId)
            .map(HotelOwnerAccess::getAccessLevel);
    }

    /**
     * Получить список всех отелей, к которым имеет доступ пользователь
     */
    public List<HotelOwnerAccess> getUserHotelAccesses(UUID hotelOwnerId) {
        return hotelOwnerAccessRepository.findByHotelOwnerId(hotelOwnerId);
    }

    /**
     * Получить список пользователей с доступом к отелю
     */
    public List<HotelOwnerAccess> getHotelUserAccesses(Long hotelId) {
        return hotelOwnerAccessRepository.findByHotelId(hotelId);
    }
}
