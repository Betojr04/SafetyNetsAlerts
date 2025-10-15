package com.safetynet.alerts.model;

/**
 * Represents a firestation mapping in the SafetyNet system.
 * Maps a specific address to a fire station number for emergency response.
 */
public class Firestation {
    private String address;
    private String station;

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStation() { return station; }
    public void setStation(String station) { this.station = station; }
}
