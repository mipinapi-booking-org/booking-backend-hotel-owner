package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.HotelDto;
import com.github.lukashindy.booking.model.Hotel;
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
    @Mapping(target = "ownerId", ignore = true) // Управляется через HotelOwnerAccess
    HotelDto toDto(Hotel hotel);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "city", target = "city")
    @Mapping(source = "street", target = "street")
    @Mapping(target = "ownerAccesses", ignore = true) // Управляется отдельно
    Hotel toEntity(HotelDto dto);
}
