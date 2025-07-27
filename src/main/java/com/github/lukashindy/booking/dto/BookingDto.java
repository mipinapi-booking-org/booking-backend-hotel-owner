package com.github.lukashindy.booking.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BookingDto {
    private Long id;
    private Long roomId;
    private UUID clientId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String guestFullNames;
    private String specialRequests;
    private String status;
}
