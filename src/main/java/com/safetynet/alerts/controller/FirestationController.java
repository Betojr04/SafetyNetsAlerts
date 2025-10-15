package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Firestation mappings.
 * Provides endpoints for CRUD operations on firestation-address mappings.
 */
@RestController
@RequestMapping("/firestation")
public class FirestationController {

    private final DataLoaderService dataLoaderService;
    private static final Logger logger = LoggerFactory.getLogger(FirestationController.class);

    public FirestationController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @PostMapping
    public String addFirestation(@RequestBody Firestation firestation) {
        logger.info("Request received to add firestation mapping: {} -> {}", firestation.getAddress(), firestation.getStation());
        try {
            dataLoaderService.getFirestations().add(firestation);
            logger.info("Successfully added firestation mapping: {} -> {}", firestation.getAddress(), firestation.getStation());
            return "Firestation mapping added.";
        } catch (Exception e) {
            logger.error("Error adding firestation mapping: {} -> {}", firestation.getAddress(), firestation.getStation(), e);
            throw e;
        }
    }

    @PutMapping
    public String updateFirestation(@RequestBody Firestation updatedFirestation) {
        logger.info("Request received to update firestation mapping for address: {}", updatedFirestation.getAddress());
        try {
            List<Firestation> firestations = dataLoaderService.getFirestations();
            for (Firestation f : firestations) {
                if (f.getAddress().equalsIgnoreCase(updatedFirestation.getAddress())) {
                    logger.debug("Firestation mapping found, updating to station: {}", updatedFirestation.getStation());
                    f.setStation(updatedFirestation.getStation());
                    logger.info("Successfully updated firestation mapping for address {} to station {}",
                            f.getAddress(), f.getStation());
                    return "Firestation mapping updated.";
                }
            }
            logger.info("Firestation mapping not found for address: {}", updatedFirestation.getAddress());
            return "Firestation mapping not found.";
        } catch (Exception e) {
            logger.error("Error updating firestation mapping for address: {}", updatedFirestation.getAddress(), e);
            throw e;
        }
    }

    @DeleteMapping
    public String deleteFirestation(@RequestParam String address) {
        logger.info("Request received to delete firestation mapping for address: {}", address);
        try {
            boolean removed = dataLoaderService.getFirestations().removeIf(
                    f -> f.getAddress().equalsIgnoreCase(address)
            );
            if (removed) {
                logger.info("Successfully deleted firestation mapping for address: {}", address);
                return "Firestation mapping deleted.";
            } else {
                logger.info("Firestation mapping not found for address: {}", address);
                return "Firestation mapping not found.";
            }
        } catch (Exception e) {
            logger.error("Error deleting firestation mapping for address: {}", address, e);
            throw e;
        }
    }
}
