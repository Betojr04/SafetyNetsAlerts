package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.service.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        dataLoaderService.getPersons().add(newPerson);
        logger.info("Added new person: {} {}", newPerson.getFirstName(), newPerson.getLastName());
        return "Person added successfully.";
    }

    @PutMapping
    public String updatePerson(@RequestBody Person updatedPerson) {
        List<Person> persons = dataLoaderService.getPersons();
        for (Person p : persons) {
            if (p.getFirstName().equalsIgnoreCase(updatedPerson.getFirstName())
                    && p.getLastName().equalsIgnoreCase(updatedPerson.getLastName())) {
                p.setAddress(updatedPerson.getAddress());
                p.setCity(updatedPerson.getCity());
                p.setZip(updatedPerson.getZip());
                p.setPhone(updatedPerson.getPhone());
                p.setEmail(updatedPerson.getEmail());
                logger.info("Updated person: {} {}", p.getFirstName(), p.getLastName());
                return "Person updated successfully.";
            }
        }
        return "Person not found.";
    }

    @DeleteMapping
    public String deletePerson(@RequestParam String firstName, @RequestParam String lastName) {
        boolean removed = dataLoaderService.getPersons().removeIf(
                p -> p.getFirstName().equalsIgnoreCase(firstName) && p.getLastName().equalsIgnoreCase(lastName)
        );
        if (removed) {
            logger.info("Deleted person: {} {}", firstName, lastName);
            return "Person deleted successfully.";
        } else {
            return "Person not found.";
        }
    }
}
