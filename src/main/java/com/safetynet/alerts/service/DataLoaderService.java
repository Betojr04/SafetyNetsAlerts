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
import java.util.List;

@Service
public class DataLoaderService {
    private List<Person> persons;
    private List<Firestation> firestations;
    private List<MedicalRecord> medicalRecords;

    @EventListener(ApplicationReadyEvent.class)
    public void loadData() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getClassLoader().getResourceAsStream("data/safetynet.json");


            JsonNode root = mapper.readTree(is);

            persons = List.of(mapper.treeToValue(root.get("persons"), Person[].class));
            firestations = List.of(mapper.treeToValue(root.get("firestations"), Firestation[].class));
            medicalRecords = List.of(mapper.treeToValue(root.get("medicalrecords"), MedicalRecord[].class));
            System.out.println("✅ Data loaded successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Person> getPersons() { return persons; }
    public List<Firestation> getFirestations() { return firestations; }
    public List<MedicalRecord> getMedicalRecords() { return medicalRecords; }
}
