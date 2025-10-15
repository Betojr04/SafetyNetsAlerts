package com.safetynet.alerts.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Service responsible for loading and managing SafetyNet data from JSON file.
 * This service loads persons, firestations, and medical records data on application startup
 * and provides access to these data collections through getter methods.
 */
@Service
public class DataLoaderService {
    private List<Person> persons;
    private List<Firestation> firestations;
    private List<MedicalRecord> medicalRecords;

    /**
     * Loads data from the safetynet.json file when the application is ready.
     * Parses JSON and initializes mutable ArrayLists for persons, firestations, and medical records.
     * This method is automatically invoked when ApplicationReadyEvent is fired.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("data/safetynet.json");


            JsonNode root = mapper.readTree(is);

            persons = new ArrayList<>(Arrays.asList(mapper.treeToValue(root.get("persons"), Person[].class)));
            firestations = new ArrayList<>(Arrays.asList(mapper.treeToValue(root.get("firestations"), Firestation[].class)));
            medicalRecords = new ArrayList<>(Arrays.asList(mapper.treeToValue(root.get("medicalrecords"), MedicalRecord[].class)));
            System.out.println("✅ Data loaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the list of persons loaded from JSON.
     * @return mutable list of Person objects
     */
    public List<Person> getPersons() { return persons; }

    /**
     * Returns the list of firestations loaded from JSON.
     * @return mutable list of Firestation objects
     */
    public List<Firestation> getFirestations() { return firestations; }

    /**
     * Returns the list of medical records loaded from JSON.
     * @return mutable list of MedicalRecord objects
     */
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
}
