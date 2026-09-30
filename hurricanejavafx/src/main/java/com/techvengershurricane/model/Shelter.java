package com.techvengershurricane.model;

import java.util.ArrayList;
import java.util.List;

/* MODEL: add shelter capacity, status, and supply behavior here. */
public class Shelter {
    private int shelterId;
    private String name;
    private String address;
    private int capacity;
    private int occupancy;
    private String status;
    private List<String> accommodations = new ArrayList<>();
    private String lastUpdated;
    private List<String> shelterOperators = new ArrayList<>();

    public Shelter() {
        /* JSON: required by Gson. */
    }

    public Shelter(int shelterId, String name, String address, int capacity) {
        this.shelterId = shelterId;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
    }

    public int getShelterId() { return shelterId; }
    public void setShelterId(int shelterId) { this.shelterId = shelterId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getOccupancy() { return occupancy; }
    public void setOccupancy(int occupancy) { this.occupancy = occupancy; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<String> getAccommodations() { return accommodations; }
    public void setAccommodations(List<String> accommodations) { this.accommodations = accommodations; }
    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
    public List<String> getShelterOperators() { return shelterOperators; }
    public void setShelterOperators(List<String> shelterOperators) { this.shelterOperators = shelterOperators; }
    public int getAvailableBeds() { return Math.max(0, capacity - occupancy); }
}
