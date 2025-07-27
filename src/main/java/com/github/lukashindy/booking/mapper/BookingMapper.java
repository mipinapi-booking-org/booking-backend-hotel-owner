package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.BookingDto;
import com.github.lukashindy.booking.model.Booking;
import com.github.lukashindy.booking.model.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "checkInDate", target = "checkInDate")
    @Mapping(source = "checkOutDate", target = "checkOutDate")
    @Mapping(source = "guestFullNames", target = "guestFullNames")
    @Mapping(source = "specialRequests", target = "specialRequests")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "room.id", target = "roomId")
    BookingDto toDto(Booking entity);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "clientId", target = "clientId")
    @Mapping(source = "checkInDate", target = "checkInDate")
    @Mapping(source = "checkOutDate", target = "checkOutDate")
    @Mapping(source = "guestFullNames", target = "guestFullNames")
    @Mapping(source = "specialRequests", target = "specialRequests")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "room", source = "roomId", qualifiedByName = "mapRoom")
    Booking toEntity(BookingDto dto);

    @Named("mapRoom")
    default Room mapRoom(Long roomId) {
        if (roomId == null) return null;
        Room room = new Room();
        room.setId(roomId);
        return room;
    }
}
