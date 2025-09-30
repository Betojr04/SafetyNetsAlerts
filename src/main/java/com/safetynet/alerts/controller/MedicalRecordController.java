package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    private final DataLoaderService dataLoaderService;
    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);

    public MedicalRecordController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @PostMapping
    public String addMedicalRecord(@RequestBody MedicalRecord record) {
        dataLoaderService.getMedicalRecords().add(record);
        logger.info("Added medical record for {} {}", record.getFirstName(), record.getLastName());
        return "Medical record added.";
    }

    @PutMapping
    public String updateMedicalRecord(@RequestBody MedicalRecord updatedRecord) {
        List<MedicalRecord> records = dataLoaderService.getMedicalRecords();
        for (MedicalRecord m : records) {
            if (m.getFirstName().equalsIgnoreCase(updatedRecord.getFirstName())
                    && m.getLastName().equalsIgnoreCase(updatedRecord.getLastName())) {
                m.setBirthdate(updatedRecord.getBirthdate());
                m.setMedications(updatedRecord.getMedications());
                m.setAllergies(updatedRecord.getAllergies());
                logger.info("Updated medical record for {} {}", m.getFirstName(), m.getLastName());
                return "Medical record updated.";
            }
        }
        return "Medical record not found.";
    }

    @DeleteMapping
    public String deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        boolean removed = dataLoaderService.getMedicalRecords().removeIf(
                m -> m.getFirstName().equalsIgnoreCase(firstName) && m.getLastName().equalsIgnoreCase(lastName)
        );
        if (removed) {
            logger.info("Deleted medical record for {} {}", firstName, lastName);
            return "Medical record deleted.";
        } else {
            return "Medical record not found.";
        }
    }
}
