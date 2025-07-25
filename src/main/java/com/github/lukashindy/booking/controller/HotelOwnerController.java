package com.github.lukashindy.booking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hotel-owner")
public class HotelOwnerController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Hotel Owner Booking Backend!";
    }

    @GetMapping("/status")
    public String status() {
        return "Service is running successfully!";
    }
}
