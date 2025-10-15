package com.safetynet.alerts.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DataLoaderService dataLoaderService;

    private List<Person> mockPersons;

    @BeforeEach
    void setUp() {
        mockPersons = new ArrayList<>();
        Person person1 = new Person();
        person1.setFirstName("John");
        person1.setLastName("Doe");
        person1.setAddress("123 Main St");
        person1.setCity("Springfield");
        person1.setZip("12345");
        person1.setPhone("555-1234");
        person1.setEmail("john@example.com");
        mockPersons.add(person1);
    }

    @Test
    void testAddPerson() throws Exception {
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);

        Person newPerson = new Person();
        newPerson.setFirstName("Jane");
        newPerson.setLastName("Smith");
        newPerson.setAddress("456 Oak St");
        newPerson.setCity("Springfield");
        newPerson.setZip("12345");
        newPerson.setPhone("555-5678");
        newPerson.setEmail("jane@example.com");

        mockMvc.perform(post("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPerson)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person added successfully."));

        verify(dataLoaderService, times(1)).getPersons();
    }

    @Test
    void testUpdatePerson_Success() throws Exception {
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);

        Person updatedPerson = new Person();
        updatedPerson.setFirstName("John");
        updatedPerson.setLastName("Doe");
        updatedPerson.setAddress("789 New St");
        updatedPerson.setCity("NewCity");
        updatedPerson.setZip("54321");
        updatedPerson.setPhone("555-9999");
        updatedPerson.setEmail("john.new@example.com");

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerson)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person updated successfully."));

        verify(dataLoaderService, times(1)).getPersons();
    }

    @Test
    void testUpdatePerson_NotFound() throws Exception {
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);

        Person updatedPerson = new Person();
        updatedPerson.setFirstName("NonExistent");
        updatedPerson.setLastName("Person");
        updatedPerson.setAddress("789 New St");
        updatedPerson.setCity("NewCity");
        updatedPerson.setZip("54321");
        updatedPerson.setPhone("555-9999");
        updatedPerson.setEmail("nonexistent@example.com");

        mockMvc.perform(put("/person")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPerson)))
                .andExpect(status().isOk())
                .andExpect(content().string("Person not found."));
    }

    @Test
    void testDeletePerson_Success() throws Exception {
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);

        mockMvc.perform(delete("/person")
                        .param("firstName", "John")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person deleted successfully."));

        verify(dataLoaderService, times(1)).getPersons();
    }

    @Test
    void testDeletePerson_NotFound() throws Exception {
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);

        mockMvc.perform(delete("/person")
                        .param("firstName", "NonExistent")
                        .param("lastName", "Person"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person not found."));
    }
}
