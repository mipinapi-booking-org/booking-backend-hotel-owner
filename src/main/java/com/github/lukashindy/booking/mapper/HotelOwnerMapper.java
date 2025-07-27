package com.github.lukashindy.booking.mapper;

import com.github.lukashindy.booking.dto.HotelOwnerDto;
import com.github.lukashindy.booking.model.HotelOwner;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HotelOwnerMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "passwordHash", target = "passwordHash")
    @Mapping(source = "role", target = "role")
    HotelOwnerDto toDto(HotelOwner entity);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "email", target = "email")
    @Mapping(source = "passwordHash", target = "passwordHash")
    @Mapping(source = "role", target = "role")
    HotelOwner toEntity(HotelOwnerDto dto);
}
