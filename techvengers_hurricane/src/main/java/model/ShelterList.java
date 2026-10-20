package model;

import java.util.ArrayList;

/** Stores and searches shelters. */
public class ShelterList {
    public static final double DEFAULT_RADIUS_MILES = 25;
    private static ShelterList instance;
    private final ArrayList<Shelter> shelters = new ArrayList<>();

    private ShelterList() { }
    public static ShelterList getInstance() {
        if (instance == null) instance = new ShelterList();
        return instance;
    }
    public ArrayList<Shelter> getShelters() { return shelters; }
    public void addShelter(Shelter shelter) {
        if (shelter != null && !shelters.contains(shelter)) shelters.add(shelter);
    }

    public ArrayList<Shelter> searchByProximity(String location, double radius) {
        ArrayList<Shelter> matches = new ArrayList<>();
        String wanted = location == null ? "" : location.trim().toLowerCase();
        for (Shelter shelter : shelters) {
            boolean sameArea = wanted.isEmpty() || shelter.getAddress().toLowerCase().contains(wanted);
            if (sameArea && shelter.hasSpace()) matches.add(shelter);
        }
        return matches;
    }

    public ArrayList<Shelter> filterByAccommodation(Accommodation accommodation) {
        ArrayList<Shelter> matches = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getAccommodations().contains(accommodation)) matches.add(shelter);
        }
        return matches;
    }

    public boolean verifyStatusReport(Shelter shelter, ShelterStatus status) {
        return shelter != null && shelter.getStatus() == status;
    }

    public boolean save() { return DataWriter.saveShelters(shelters); }
}
