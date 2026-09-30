package com.techvengershurricane.model;

import java.util.ArrayList;
import java.util.List;

/* MODEL: add volunteer skills and availability behavior here. */
public class Volunteer extends User {
    private List<String> skills = new ArrayList<>();
    private List<String> equipment = new ArrayList<>();
    private String backgroundCheckStatus;
    private boolean transportationAccess;
    private String availabilityStatus;

    public Volunteer() {
        /* JSON: required by Gson. */
    }

    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public List<String> getEquipment() { return equipment; }
    public void setEquipment(List<String> equipment) { this.equipment = equipment; }
    public String getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public void setBackgroundCheckStatus(String value) { this.backgroundCheckStatus = value; }
    public boolean hasTransportationAccess() { return transportationAccess; }
    public void setTransportationAccess(boolean value) { this.transportationAccess = value; }
    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String value) { this.availabilityStatus = value; }
}
