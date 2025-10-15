package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.DataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedicalRecordController.class)
public class MedicalRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DataLoaderService dataLoaderService;

    private List<MedicalRecord> mockMedicalRecords;

    @BeforeEach
    void setUp() {
        mockMedicalRecords = new ArrayList<>();
        MedicalRecord record = new MedicalRecord();
        record.setFirstName("John");
        record.setLastName("Doe");
        record.setBirthdate("01/01/1980");
        record.setMedications(Arrays.asList("med1:100mg"));
        record.setAllergies(Arrays.asList("peanuts"));
        mockMedicalRecords.add(record);
    }

    @Test
    void testAddMedicalRecord() throws Exception {
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        MedicalRecord newRecord = new MedicalRecord();
        newRecord.setFirstName("Jane");
        newRecord.setLastName("Smith");
        newRecord.setBirthdate("02/02/1990");
        newRecord.setMedications(Arrays.asList("med2:200mg"));
        newRecord.setAllergies(Arrays.asList("shellfish"));

        mockMvc.perform(post("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newRecord)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record added."));

        verify(dataLoaderService, times(1)).getMedicalRecords();
    }

    @Test
    void testUpdateMedicalRecord_Success() throws Exception {
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName("John");
        updatedRecord.setLastName("Doe");
        updatedRecord.setBirthdate("01/01/1980");
        updatedRecord.setMedications(Arrays.asList("newMed:300mg"));
        updatedRecord.setAllergies(Arrays.asList("dairy"));

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRecord)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record updated."));

        verify(dataLoaderService, times(1)).getMedicalRecords();
    }

    @Test
    void testUpdateMedicalRecord_NotFound() throws Exception {
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        MedicalRecord updatedRecord = new MedicalRecord();
        updatedRecord.setFirstName("NonExistent");
        updatedRecord.setLastName("Person");
        updatedRecord.setBirthdate("01/01/1990");
        updatedRecord.setMedications(Arrays.asList("med:100mg"));
        updatedRecord.setAllergies(Arrays.asList("none"));

        mockMvc.perform(put("/medicalRecord")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRecord)))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record not found."));
    }

    @Test
    void testDeleteMedicalRecord_Success() throws Exception {
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record deleted."));

        verify(dataLoaderService, times(1)).getMedicalRecords();
    }

    @Test
    void testDeleteMedicalRecord_NotFound() throws Exception {
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        mockMvc.perform(delete("/medicalRecord")
                        .param("firstName", "NonExistent")
                        .param("lastName", "Person"))
                .andExpect(status().isOk())
                .andExpect(content().string("Medical record not found."));
    }
}
