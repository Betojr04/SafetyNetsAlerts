package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.service.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        dataLoaderService.getFirestations().add(firestation);
        logger.info("Added firestation mapping: {} -> {}", firestation.getAddress(), firestation.getStation());
        return "Firestation mapping added.";
    }

    @PutMapping
    public String updateFirestation(@RequestBody Firestation updatedFirestation) {
        List<Firestation> firestations = dataLoaderService.getFirestations();
        for (Firestation f : firestations) {
            if (f.getAddress().equalsIgnoreCase(updatedFirestation.getAddress())) {
                f.setStation(updatedFirestation.getStation());
                logger.info("Updated firestation mapping for address {} to station {}",
                        f.getAddress(), f.getStation());
                return "Firestation mapping updated.";
            }
        }
        return "Firestation mapping not found.";
    }

    @DeleteMapping
    public String deleteFirestation(@RequestParam String address) {
        boolean removed = dataLoaderService.getFirestations().removeIf(
                f -> f.getAddress().equalsIgnoreCase(address)
        );
        if (removed) {
            logger.info("Deleted firestation mapping for address {}", address);
            return "Firestation mapping deleted.";
        } else {
            return "Firestation mapping not found.";
        }
    }
}
