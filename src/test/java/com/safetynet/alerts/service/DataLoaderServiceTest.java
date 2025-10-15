package com.safetynet.alerts.service;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.model.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DataLoaderServiceTest {

    @Autowired
    private DataLoaderService dataLoaderService;

    @Test
    void testDataLoadedSuccessfully() {
        assertNotNull(dataLoaderService.getPersons(), "Persons list should not be null");
        assertNotNull(dataLoaderService.getFirestations(), "Firestations list should not be null");
        assertNotNull(dataLoaderService.getMedicalRecords(), "Medical records list should not be null");
    }

    @Test
    void testPersonsListNotEmpty() {
        List<Person> persons = dataLoaderService.getPersons();
        assertFalse(persons.isEmpty(), "Persons list should not be empty");
        assertTrue(persons.size() > 0, "Persons list should contain data");
    }

    @Test
    void testFirestationsListNotEmpty() {
        List<Firestation> firestations = dataLoaderService.getFirestations();
        assertFalse(firestations.isEmpty(), "Firestations list should not be empty");
        assertTrue(firestations.size() > 0, "Firestations list should contain data");
    }

    @Test
    void testMedicalRecordsListNotEmpty() {
        List<MedicalRecord> medicalRecords = dataLoaderService.getMedicalRecords();
        assertFalse(medicalRecords.isEmpty(), "Medical records list should not be empty");
        assertTrue(medicalRecords.size() > 0, "Medical records list should contain data");
    }

    @Test
    void testPersonDataIntegrity() {
        List<Person> persons = dataLoaderService.getPersons();
        Person firstPerson = persons.get(0);

        assertNotNull(firstPerson.getFirstName(), "Person first name should not be null");
        assertNotNull(firstPerson.getLastName(), "Person last name should not be null");
        assertNotNull(firstPerson.getAddress(), "Person address should not be null");
        assertNotNull(firstPerson.getCity(), "Person city should not be null");
        assertNotNull(firstPerson.getZip(), "Person zip should not be null");
        assertNotNull(firstPerson.getPhone(), "Person phone should not be null");
        assertNotNull(firstPerson.getEmail(), "Person email should not be null");
    }

    @Test
    void testFirestationDataIntegrity() {
        List<Firestation> firestations = dataLoaderService.getFirestations();
        Firestation firstFirestation = firestations.get(0);

        assertNotNull(firstFirestation.getAddress(), "Firestation address should not be null");
        assertNotNull(firstFirestation.getStation(), "Firestation station should not be null");
    }

    @Test
    void testMedicalRecordDataIntegrity() {
        List<MedicalRecord> medicalRecords = dataLoaderService.getMedicalRecords();
        MedicalRecord firstRecord = medicalRecords.get(0);

        assertNotNull(firstRecord.getFirstName(), "Medical record first name should not be null");
        assertNotNull(firstRecord.getLastName(), "Medical record last name should not be null");
        assertNotNull(firstRecord.getBirthdate(), "Medical record birthdate should not be null");
        assertNotNull(firstRecord.getMedications(), "Medical record medications should not be null");
        assertNotNull(firstRecord.getAllergies(), "Medical record allergies should not be null");
    }

    @Test
    void testListsAreMutable() {
        List<Person> persons = dataLoaderService.getPersons();
        int originalSize = persons.size();

        Person testPerson = new Person();
        testPerson.setFirstName("Test");
        testPerson.setLastName("User");

        persons.add(testPerson);
        assertEquals(originalSize + 1, persons.size(), "Persons list should be mutable");

        persons.remove(testPerson);
        assertEquals(originalSize, persons.size(), "Should be able to remove from persons list");
    }
}
