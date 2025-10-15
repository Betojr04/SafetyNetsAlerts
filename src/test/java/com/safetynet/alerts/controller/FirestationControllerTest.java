package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.DataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FirestationController.class)
public class FirestationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DataLoaderService dataLoaderService;

    private List<Firestation> mockFirestations;

    @BeforeEach
    void setUp() {
        mockFirestations = new ArrayList<>();
        Firestation firestation = new Firestation();
        firestation.setAddress("123 Main St");
        firestation.setStation("1");
        mockFirestations.add(firestation);
    }

    @Test
    void testAddFirestation() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);

        Firestation newFirestation = new Firestation();
        newFirestation.setAddress("456 Oak St");
        newFirestation.setStation("2");

        mockMvc.perform(post("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newFirestation)))
                .andExpect(status().isOk())
                .andExpect(content().string("Firestation mapping added."));

        verify(dataLoaderService, times(1)).getFirestations();
    }

    @Test
    void testUpdateFirestation_Success() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);

        Firestation updatedFirestation = new Firestation();
        updatedFirestation.setAddress("123 Main St");
        updatedFirestation.setStation("5");

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirestation)))
                .andExpect(status().isOk())
                .andExpect(content().string("Firestation mapping updated."));

        verify(dataLoaderService, times(1)).getFirestations();
    }

    @Test
    void testUpdateFirestation_NotFound() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);

        Firestation updatedFirestation = new Firestation();
        updatedFirestation.setAddress("NonExistent St");
        updatedFirestation.setStation("5");

        mockMvc.perform(put("/firestation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFirestation)))
                .andExpect(status().isOk())
                .andExpect(content().string("Firestation mapping not found."));
    }

    @Test
    void testDeleteFirestation_Success() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);

        mockMvc.perform(delete("/firestation")
                        .param("address", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(content().string("Firestation mapping deleted."));

        verify(dataLoaderService, times(1)).getFirestations();
    }

    @Test
    void testDeleteFirestation_NotFound() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);

        mockMvc.perform(delete("/firestation")
                        .param("address", "NonExistent St"))
                .andExpect(status().isOk())
                .andExpect(content().string("Firestation mapping not found."));
    }
}
