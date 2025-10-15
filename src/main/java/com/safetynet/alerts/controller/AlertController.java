package com.safetynet.alerts.controller;

import com.safetynet.alerts.model.Person;
import com.safetynet.alerts.model.Firestation;
import com.safetynet.alerts.model.MedicalRecord;
import com.safetynet.alerts.service.DataLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST controller for SafetyNet alert endpoints.
 * Provides various emergency information endpoints for fire stations, flood alerts,
 * community emails, phone alerts, and person information queries.
 */
@RestController
public class AlertController {

    private final DataLoaderService dataLoaderService;
    private static final Logger logger = LoggerFactory.getLogger(AlertController.class);

    public AlertController(DataLoaderService dataLoaderService) {
        this.dataLoaderService = dataLoaderService;
    }

    // ----------------- /communityEmail -----------------
    @GetMapping("/communityEmail")
    public List<String> getEmails(@RequestParam String city) {
        logger.info("Request received for community emails in city: {}", city);
        try {
            logger.debug("Filtering persons by city: {}", city);
            List<String> emails = dataLoaderService.getPersons().stream()
                    .filter(p -> p.getCity().equalsIgnoreCase(city))
                    .map(Person::getEmail)
                    .distinct()
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved {} emails for city: {}", emails.size(), city);
            logger.debug("Emails found: {}", emails);
            return emails;
        } catch (Exception e) {
            logger.error("Error retrieving community emails for city: {}", city, e);
            throw e;
        }
    }

    // ----------------- /phoneAlert -----------------
    @GetMapping("/phoneAlert")
    public List<String> getPhones(@RequestParam String firestation) {
        logger.info("Request received for phoneAlert for firestation: {}", firestation);
        try {
            logger.debug("Finding addresses covered by firestation: {}", firestation);
            List<String> addresses = dataLoaderService.getFirestations().stream()
                    .filter(f -> f.getStation().equals(firestation))
                    .map(Firestation::getAddress)
                    .collect(Collectors.toList());
            logger.debug("Found {} addresses for firestation {}", addresses.size(), firestation);

            List<String> phones = dataLoaderService.getPersons().stream()
                    .filter(p -> addresses.contains(p.getAddress()))
                    .map(Person::getPhone)
                    .distinct()
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved {} phone numbers for firestation: {}", phones.size(), firestation);
            return phones;
        } catch (Exception e) {
            logger.error("Error retrieving phone numbers for firestation: {}", firestation, e);
            throw e;
        }
    }

    // ----------------- /childAlert -----------------
    @GetMapping("/childAlert")
    public Map<String, Object> getChildren(@RequestParam String address) {
        logger.info("Request received for childAlert at address: {}", address);
        try {
            logger.debug("Finding residents at address: {}", address);
            List<Person> residents = dataLoaderService.getPersons().stream()
                    .filter(p -> p.getAddress().equalsIgnoreCase(address))
                    .collect(Collectors.toList());
            logger.debug("Found {} residents at address: {}", residents.size(), address);

            List<Map<String, Object>> children = new ArrayList<>();
            List<Map<String, Object>> adults = new ArrayList<>();

            for (Person person : residents) {
                dataLoaderService.getMedicalRecords().stream()
                        .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName())
                                && m.getLastName().equalsIgnoreCase(person.getLastName()))
                        .findFirst()
                        .ifPresent(med -> {
                            int age = calculateAge(med.getBirthdate());
                            logger.debug("Calculated age {} for {} {}", age, person.getFirstName(), person.getLastName());
                            Map<String, Object> info = new HashMap<>();
                            info.put("firstName", person.getFirstName());
                            info.put("lastName", person.getLastName());
                            info.put("age", age);

                            if (age <= 18) {
                                children.add(info);
                            } else {
                                adults.add(info);
                            }
                        });
            }

            Map<String, Object> response = new HashMap<>();
            response.put("children", children);
            response.put("householdMembers", adults);
            logger.info("Successfully retrieved childAlert for address: {} - {} children, {} adults",
                    address, children.size(), adults.size());
            return response;
        } catch (Exception e) {
            logger.error("Error retrieving childAlert for address: {}", address, e);
            throw e;
        }
    }

    // ----------------- /firestation -----------------
    @GetMapping("/firestation")
    public Map<String, Object> getPeopleByStation(@RequestParam String stationNumber) {
        logger.info("Request received for firestation coverage: {}", stationNumber);
        try {
            logger.debug("Finding addresses covered by station: {}", stationNumber);
            List<String> addresses = dataLoaderService.getFirestations().stream()
                    .filter(f -> f.getStation().equals(stationNumber))
                    .map(Firestation::getAddress)
                    .collect(Collectors.toList());
            logger.debug("Found {} addresses for station {}", addresses.size(), stationNumber);

            List<Map<String, Object>> persons = new ArrayList<>();
            int adults = 0;
            int children = 0;

            for (Person person : dataLoaderService.getPersons()) {
                if (addresses.contains(person.getAddress())) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("firstName", person.getFirstName());
                    info.put("lastName", person.getLastName());
                    info.put("address", person.getAddress());
                    info.put("phone", person.getPhone());

                    int age = getAgeForPerson(person);
                    logger.debug("Person: {} {} - Age: {}", person.getFirstName(), person.getLastName(), age);
                    if (age <= 18) children++;
                    else adults++;

                    persons.add(info);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("persons", persons);
            response.put("adultCount", adults);
            response.put("childCount", children);
            logger.info("Successfully retrieved firestation coverage for station {}: {} persons ({} adults, {} children)",
                    stationNumber, persons.size(), adults, children);
            return response;
        } catch (Exception e) {
            logger.error("Error retrieving firestation coverage for station: {}", stationNumber, e);
            throw e;
        }
    }

    // ----------------- /fire -----------------
    @GetMapping("/fire")
    public Map<String, Object> getFireInfo(@RequestParam String address) {
        logger.info("Request received for fire info at address: {}", address);
        try {
            logger.debug("Finding fire station for address: {}", address);
            Optional<String> station = dataLoaderService.getFirestations().stream()
                    .filter(f -> f.getAddress().equalsIgnoreCase(address))
                    .map(Firestation::getStation)
                    .findFirst();
            logger.debug("Fire station for address {}: {}", address, station.orElse("Unknown"));

            List<Map<String, Object>> residents = new ArrayList<>();

            for (Person person : dataLoaderService.getPersons()) {
                if (person.getAddress().equalsIgnoreCase(address)) {
                    Map<String, Object> info = new HashMap<>();
                    info.put("name", person.getFirstName() + " " + person.getLastName());
                    info.put("phone", person.getPhone());
                    int age = getAgeForPerson(person);
                    info.put("age", age);
                    logger.debug("Processing resident: {} - Age: {}", person.getFirstName() + " " + person.getLastName(), age);

                    dataLoaderService.getMedicalRecords().stream()
                            .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName())
                                    && m.getLastName().equalsIgnoreCase(person.getLastName()))
                            .findFirst()
                            .ifPresent(med -> {
                                info.put("medications", med.getMedications());
                                info.put("allergies", med.getAllergies());
                            });

                    residents.add(info);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("station", station.orElse("Unknown"));
            response.put("residents", residents);
            logger.info("Successfully retrieved fire info for address {}: station {}, {} residents",
                    address, station.orElse("Unknown"), residents.size());
            return response;
        } catch (Exception e) {
            logger.error("Error retrieving fire info for address: {}", address, e);
            throw e;
        }
    }

    // ----------------- /flood/stations -----------------
    @GetMapping("/flood/stations")
    public Map<String, List<Map<String, Object>>> getFloodInfo(@RequestParam List<String> stations) {
        logger.info("Request received for flood info for stations: {}", stations);
        try {
            logger.debug("Finding addresses covered by stations: {}", stations);
            Map<String, List<Map<String, Object>>> households = new HashMap<>();

            List<String> addresses = dataLoaderService.getFirestations().stream()
                    .filter(f -> stations.contains(f.getStation()))
                    .map(Firestation::getAddress)
                    .collect(Collectors.toList());
            logger.debug("Found {} addresses for stations {}", addresses.size(), stations);

            for (String address : addresses) {
                List<Map<String, Object>> residents = new ArrayList<>();
                for (Person person : dataLoaderService.getPersons()) {
                    if (person.getAddress().equalsIgnoreCase(address)) {
                        Map<String, Object> info = new HashMap<>();
                        info.put("name", person.getFirstName() + " " + person.getLastName());
                        info.put("phone", person.getPhone());
                        int age = getAgeForPerson(person);
                        info.put("age", age);
                        logger.debug("Processing resident at {}: {} - Age: {}", address,
                                person.getFirstName() + " " + person.getLastName(), age);

                        dataLoaderService.getMedicalRecords().stream()
                                .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName())
                                        && m.getLastName().equalsIgnoreCase(person.getLastName()))
                                .findFirst()
                                .ifPresent(med -> {
                                    info.put("medications", med.getMedications());
                                    info.put("allergies", med.getAllergies());
                                });

                        residents.add(info);
                    }
                }
                households.put(address, residents);
                logger.debug("Address {}: {} residents", address, residents.size());
            }

            logger.info("Successfully retrieved flood info for stations {}: {} households",
                    stations, households.size());
            return households;
        } catch (Exception e) {
            logger.error("Error retrieving flood info for stations: {}", stations, e);
            throw e;
        }
    }

    // ----------------- /personInfo -----------------
    @GetMapping("/personInfo")
    public List<Map<String, Object>> getPersonInfo(@RequestParam String lastName) {
        logger.info("Request received for personInfo with lastName: {}", lastName);
        try {
            logger.debug("Searching for persons with lastName: {}", lastName);
            List<Map<String, Object>> results = dataLoaderService.getPersons().stream()
                    .filter(p -> p.getLastName().equalsIgnoreCase(lastName))
                    .map(person -> {
                        Map<String, Object> info = new HashMap<>();
                        info.put("firstName", person.getFirstName());
                        info.put("lastName", person.getLastName());
                        info.put("address", person.getAddress());
                        info.put("email", person.getEmail());
                        int age = getAgeForPerson(person);
                        info.put("age", age);
                        logger.debug("Found person: {} {} - Age: {}", person.getFirstName(), person.getLastName(), age);

                        dataLoaderService.getMedicalRecords().stream()
                                .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName())
                                        && m.getLastName().equalsIgnoreCase(person.getLastName()))
                                .findFirst()
                                .ifPresent(med -> {
                                    info.put("medications", med.getMedications());
                                    info.put("allergies", med.getAllergies());
                                });

                        return info;
                    })
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved personInfo for lastName {}: {} persons found", lastName, results.size());
            return results;
        } catch (Exception e) {
            logger.error("Error retrieving personInfo for lastName: {}", lastName, e);
            throw e;
        }
    }

    // ----------------- Helpers -----------------
    private int calculateAge(String birthdate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate dob = LocalDate.parse(birthdate, formatter);
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (Exception e) {
            logger.error("Error parsing birthdate: {}", birthdate, e);
            return -1;
        }
    }

    private int getAgeForPerson(Person person) {
        return dataLoaderService.getMedicalRecords().stream()
                .filter(m -> m.getFirstName().equalsIgnoreCase(person.getFirstName())
                        && m.getLastName().equalsIgnoreCase(person.getLastName()))
                .findFirst()
                .map(m -> calculateAge(m.getBirthdate()))
                .orElse(-1);
    }
}
