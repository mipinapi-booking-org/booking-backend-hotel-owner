package com.github.lukashindy.booking.repository;

import com.github.lukashindy.booking.model.HotelOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelOwnerRepository extends JpaRepository<HotelOwner, Long> {
}
