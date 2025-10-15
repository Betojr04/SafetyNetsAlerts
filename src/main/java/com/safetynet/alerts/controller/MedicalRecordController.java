package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Medical Records.
 * Provides endpoints for CRUD operations on medical record data.
 */
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
        logger.info("Request received to add medical record for {} {}", record.getFirstName(), record.getLastName());
        try {
            dataLoaderService.getMedicalRecords().add(record);
            logger.info("Successfully added medical record for {} {}", record.getFirstName(), record.getLastName());
            return "Medical record added.";
        } catch (Exception e) {
            logger.error("Error adding medical record for {} {}", record.getFirstName(), record.getLastName(), e);
            throw e;
        }
    }

    @PutMapping
    public String updateMedicalRecord(@RequestBody MedicalRecord updatedRecord) {
        logger.info("Request received to update medical record for {} {}", updatedRecord.getFirstName(), updatedRecord.getLastName());
        try {
            List<MedicalRecord> records = dataLoaderService.getMedicalRecords();
            for (MedicalRecord m : records) {
                if (m.getFirstName().equalsIgnoreCase(updatedRecord.getFirstName())
                        && m.getLastName().equalsIgnoreCase(updatedRecord.getLastName())) {
                    logger.debug("Medical record found, updating fields");
                    m.setBirthdate(updatedRecord.getBirthdate());
                    m.setMedications(updatedRecord.getMedications());
                    m.setAllergies(updatedRecord.getAllergies());
                    logger.info("Successfully updated medical record for {} {}", m.getFirstName(), m.getLastName());
                    return "Medical record updated.";
                }
            }
            logger.info("Medical record not found for {} {}", updatedRecord.getFirstName(), updatedRecord.getLastName());
            return "Medical record not found.";
        } catch (Exception e) {
            logger.error("Error updating medical record for {} {}", updatedRecord.getFirstName(), updatedRecord.getLastName(), e);
            throw e;
        }
    }

    @DeleteMapping
    public String deleteMedicalRecord(@RequestParam String firstName, @RequestParam String lastName) {
        logger.info("Request received to delete medical record for {} {}", firstName, lastName);
        try {
            boolean removed = dataLoaderService.getMedicalRecords().removeIf(
                    m -> m.getFirstName().equalsIgnoreCase(firstName) && m.getLastName().equalsIgnoreCase(lastName)
            );
            if (removed) {
                logger.info("Successfully deleted medical record for {} {}", firstName, lastName);
                return "Medical record deleted.";
            } else {
                logger.info("Medical record not found for {} {}", firstName, lastName);
                return "Medical record not found.";
            }
        } catch (Exception e) {
            logger.error("Error deleting medical record for {} {}", firstName, lastName, e);
            throw e;
        }
    }
}
