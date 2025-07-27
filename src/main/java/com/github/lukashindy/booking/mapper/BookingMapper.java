package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.BookingDto;
import com.github.lukashindy.booking.model.Booking;
import com.github.lukashindy.booking.model.Room;
import com.github.lukashindy.booking.model.HotelOwner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "checkInDate", target = "checkInDate")
    @Mapping(source = "checkOutDate", target = "checkOutDate")
    @Mapping(source = "guestFullNames", target = "guestFullNames")
    @Mapping(source = "specialRequests", target = "specialRequests")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "refuseReason", target = "refuseReason")
    @Mapping(source = "room.id", target = "roomId")
    @Mapping(source = "updatedBy.id", target = "updatedBy")
    @Mapping(source = "lastUpdatedDate", target = "lastUpdatedDate")
    BookingDto toDto(Booking entity);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "checkInDate", target = "checkInDate")
    @Mapping(source = "checkOutDate", target = "checkOutDate")
    @Mapping(source = "guestFullNames", target = "guestFullNames")
    @Mapping(source = "specialRequests", target = "specialRequests")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "refuseReason", target = "refuseReason")
    @Mapping(target = "room", source = "roomId", qualifiedByName = "mapRoom")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "mapHotelOwner")
    @Mapping(source = "lastUpdatedDate", target = "lastUpdatedDate")
    Booking toEntity(BookingDto dto);

    @Named("mapRoom")
    default Room mapRoom(Long roomId) {
        if (roomId == null) return null;
        Room room = new Room();
        room.setId(roomId);
        return room;
    }

    @Named("mapHotelOwner")
    default HotelOwner mapHotelOwner(UUID hotelOwnerId) {
        if (hotelOwnerId == null) return null;
        HotelOwner hotelOwner = new HotelOwner();
        hotelOwner.setId(hotelOwnerId);
        return hotelOwner;
    }
}
