package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.RoomTypeDto;
import com.github.lukashindy.booking.model.RoomType;
import com.github.lukashindy.booking.model.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RoomTypeMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "capacity", target = "capacity")
    @Mapping(source = "hotel.id", target = "hotelId")
    RoomTypeDto toDto(RoomType entity);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "capacity", target = "capacity")
    @Mapping(source = "hotelId", target = "hotel", qualifiedByName = "mapHotel")
    RoomType toEntity(RoomTypeDto dto);

    @Named("mapHotel")
    default Hotel mapHotel(Long hotelId) {
        if (hotelId == null) return null;
        Hotel hotel = new Hotel();
        hotel.setId(hotelId);
        return hotel;
    }
}
