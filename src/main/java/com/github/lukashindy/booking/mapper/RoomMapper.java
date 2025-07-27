package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.RoomDto;
import com.github.lukashindy.booking.model.Room;
import com.github.lukashindy.booking.model.RoomType;
import com.github.lukashindy.booking.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "roomNumber", target = "roomNumber")
    @Mapping(source = "roomType.id", target = "roomTypeId")
    @Mapping(source = "hotel.id", target = "hotelId")
    RoomDto toDto(Room entity);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "roomNumber", target = "roomNumber")
    @Mapping(source = "roomTypeId", target = "roomType", qualifiedByName = "mapRoomType")
    @Mapping(source = "hotelId", target = "hotel", qualifiedByName = "mapHotel")
    Room toEntity(RoomDto dto);

    @org.mapstruct.Named("mapRoomType")
    default RoomType mapRoomType(Long roomTypeId) {
        if (roomTypeId == null) return null;
        RoomType roomType = new RoomType();
        roomType.setId(roomTypeId);
        return roomType;
    }

    @org.mapstruct.Named("mapHotel")
    default Hotel mapHotel(Long hotelId) {
        if (hotelId == null) return null;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        return hotel;
    }
}
