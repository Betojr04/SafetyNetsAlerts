package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.DataLoaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@WebMvcTest(AlertController.class)
public class AlertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataLoaderService dataLoaderService;

    private List<Person> mockPersons;
    private List<Firestation> mockFirestations;
    private List<MedicalRecord> mockMedicalRecords;

    @BeforeEach
    void setUp() {
        // Setup mock persons
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

        Person person2 = new Person();
        person2.setFirstName("Jane");
        person2.setLastName("Doe");
        person2.setAddress("123 Main St");
        person2.setCity("Springfield");
        person2.setZip("12345");
        person2.setPhone("555-5678");
        person2.setEmail("jane@example.com");
        mockPersons.add(person2);

        // Setup mock firestations
        mockFirestations = new ArrayList<>();
        Firestation firestation1 = new Firestation();
        firestation1.setAddress("123 Main St");
        firestation1.setStation("1");
        mockFirestations.add(firestation1);

        // Setup mock medical records
        mockMedicalRecords = new ArrayList<>();
        MedicalRecord record1 = new MedicalRecord();
        record1.setFirstName("John");
        record1.setLastName("Doe");
        record1.setBirthdate("01/01/1980");
        record1.setMedications(Arrays.asList("med1:100mg"));
        record1.setAllergies(Arrays.asList("peanuts"));
        mockMedicalRecords.add(record1);

        MedicalRecord record2 = new MedicalRecord();
        record2.setFirstName("Jane");
        record2.setLastName("Doe");
        record2.setBirthdate("01/01/2010");
        record2.setMedications(Arrays.asList());
        record2.setAllergies(Arrays.asList());
        mockMedicalRecords.add(record2);
    }

    @Test
    void testGetCommunityEmail() throws Exception {
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);

        mockMvc.perform(get("/communityEmail")
                        .param("city", "Springfield"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("john@example.com"));

        verify(dataLoaderService, times(1)).getPersons();
    }

    @Test
    void testGetPhoneAlert() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);

        mockMvc.perform(get("/phoneAlert")
                        .param("firestation", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value("555-1234"));

        verify(dataLoaderService, times(1)).getFirestations();
        verify(dataLoaderService, times(1)).getPersons();
    }

    @Test
    void testGetChildAlert() throws Exception {
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        mockMvc.perform(get("/childAlert")
                        .param("address", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.children", hasSize(1)))
                .andExpect(jsonPath("$.children[0].firstName").value("Jane"))
                .andExpect(jsonPath("$.householdMembers", hasSize(1)))
                .andExpect(jsonPath("$.householdMembers[0].firstName").value("John"));

        verify(dataLoaderService, times(1)).getPersons();
        verify(dataLoaderService, atLeastOnce()).getMedicalRecords();
    }

    @Test
    void testGetPeopleByStation() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons", hasSize(2)))
                .andExpect(jsonPath("$.adultCount").value(1))
                .andExpect(jsonPath("$.childCount").value(1));

        verify(dataLoaderService, times(1)).getFirestations();
        verify(dataLoaderService, times(1)).getPersons();
    }

    @Test
    void testGetFireInfo() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        mockMvc.perform(get("/fire")
                        .param("address", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.station").value("1"))
                .andExpect(jsonPath("$.residents", hasSize(2)))
                .andExpect(jsonPath("$.residents[0].name").value("John Doe"));

        verify(dataLoaderService, times(1)).getFirestations();
        verify(dataLoaderService, times(1)).getPersons();
    }

    @Test
    void testGetFloodInfo() throws Exception {
        when(dataLoaderService.getFirestations()).thenReturn(mockFirestations);
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['123 Main St']", hasSize(2)));

        verify(dataLoaderService, times(1)).getFirestations();
        verify(dataLoaderService, atLeastOnce()).getPersons();
    }

    @Test
    void testGetPersonInfo() throws Exception {
        when(dataLoaderService.getPersons()).thenReturn(mockPersons);
        when(dataLoaderService.getMedicalRecords()).thenReturn(mockMedicalRecords);

        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));

        verify(dataLoaderService, times(1)).getPersons();
    }
}
