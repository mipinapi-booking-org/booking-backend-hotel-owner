package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.HotelDto;
import com.github.lukashindy.booking.model.Hotel;
import com.github.lukashindy.booking.model.HotelOwner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    HotelMapper INSTANCE = Mappers.getMapper(HotelMapper.class);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "owner.id", target = "ownerId")
    HotelDto toDto(Hotel hotel);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "ownerId", target = "owner")
    Hotel toEntity(HotelDto dto);

    default HotelOwner map(Long ownerId) {
        if (ownerId == null) return null;
        HotelOwner owner = new HotelOwner();
        owner.setId(ownerId);
        return owner;
    }
}
