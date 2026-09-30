package com.techvengershurricane.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* MODEL: add account, safety, and resident fields/methods here. */
public class User {
    private int userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;
    private String username;
    private String passwordHash;
    private List<String> roles = new ArrayList<>();
    private List<Map<String, Object>> associatedPeople = new ArrayList<>();
    private String verificationStatus;
    private Map<String, Double> lastKnownLocation;
    private String safetyStatus;
    private String safetyStatusUpdatedAt;

    public User() {
        /* JSON: required by Gson. */
    }

    public User(int userId, String firstName, String lastName, String username, String email) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) { this.roles = roles; }
    public List<Map<String, Object>> getAssociatedPeople() { return associatedPeople; }
    public void setAssociatedPeople(List<Map<String, Object>> associatedPeople) { this.associatedPeople = associatedPeople; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public Map<String, Double> getLastKnownLocation() { return lastKnownLocation; }
    public void setLastKnownLocation(Map<String, Double> lastKnownLocation) { this.lastKnownLocation = lastKnownLocation; }
    public String getSafetyStatus() { return safetyStatus; }
    public void setSafetyStatus(String safetyStatus) { this.safetyStatus = safetyStatus; }
    public String getSafetyStatusUpdatedAt() { return safetyStatusUpdatedAt; }
    public void setSafetyStatusUpdatedAt(String safetyStatusUpdatedAt) { this.safetyStatusUpdatedAt = safetyStatusUpdatedAt; }
}
