package com.github.lukashindy.booking.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HotelOwnerController.class)
class HotelOwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void hello_ShouldReturnGreeting() throws Exception {
        mockMvc.perform(get("/api/v1/hotel-owner/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello from Hotel Owner Booking Backend!"));
    }

    @Test
    void status_ShouldReturnStatus() throws Exception {
        mockMvc.perform(get("/api/v1/hotel-owner/status"))
                .andExpect(status().isOk())
                .andExpect(content().string("Service is running successfully!"));
    }
}
