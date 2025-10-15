package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Person entities.
 * Provides endpoints for CRUD operations on person data.
 */
@RestController
@RequestMapping("/person")
public class PersonController {

    private final DataLoaderService dataLoaderService;
    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    public PersonController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    @PostMapping
    public String addPerson(@RequestBody Person newPerson) {
        logger.info("Request received to add person: {} {}", newPerson.getFirstName(), newPerson.getLastName());
        try {
            dataLoaderService.getPersons().add(newPerson);
            logger.info("Successfully added person: {} {}", newPerson.getFirstName(), newPerson.getLastName());
            return "Person added successfully.";
        } catch (Exception e) {
            logger.error("Error adding person: {} {}", newPerson.getFirstName(), newPerson.getLastName(), e);
            throw e;
        }
    }

    @PutMapping
    public String updatePerson(@RequestBody Person updatedPerson) {
        logger.info("Request received to update person: {} {}", updatedPerson.getFirstName(), updatedPerson.getLastName());
        try {
            List<Person> persons = dataLoaderService.getPersons();
            for (Person p : persons) {
                if (p.getFirstName().equalsIgnoreCase(updatedPerson.getFirstName())
                        && p.getLastName().equalsIgnoreCase(updatedPerson.getLastName())) {
                    logger.debug("Person found, updating fields");
                    p.setAddress(updatedPerson.getAddress());
                    p.setCity(updatedPerson.getCity());
                    p.setZip(updatedPerson.getZip());
                    p.setPhone(updatedPerson.getPhone());
                    p.setEmail(updatedPerson.getEmail());
                    logger.info("Successfully updated person: {} {}", p.getFirstName(), p.getLastName());
                    return "Person updated successfully.";
                }
            }
            logger.info("Person not found: {} {}", updatedPerson.getFirstName(), updatedPerson.getLastName());
            return "Person not found.";
        } catch (Exception e) {
            logger.error("Error updating person: {} {}", updatedPerson.getFirstName(), updatedPerson.getLastName(), e);
            throw e;
        }
    }

    @DeleteMapping
    public String deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        logger.info("Request received to delete person: {} {}", firstName, lastName);
        try {
            boolean removed = dataLoaderService.getPersons().removeIf(
                    p -> p.getFirstName().equalsIgnoreCase(firstName) && p.getLastName().equalsIgnoreCase(lastName)
            );
            if (removed) {
                logger.info("Successfully deleted person: {} {}", firstName, lastName);
                return "Person deleted successfully.";
            } else {
                logger.info("Person not found: {} {}", firstName, lastName);
                return "Person not found.";
            }
        } catch (Exception e) {
            logger.error("Error deleting person: {} {}", firstName, lastName, e);
            throw e;
        }
    }
}
