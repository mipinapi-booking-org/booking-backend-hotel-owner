package com.github.lukashindy.booking.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BookingDto {
    private Long id;
    private Long roomId;
    private UUID clientId;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private String guestFullNames;
    private String specialRequests;
    private String status;
    private String refuseReason;
    private UUID updatedBy;
    private LocalDateTime lastUpdatedDate;
}
