package model;

import java.util.ArrayList;

/** Stores and searches shelters. */
public class ShelterList {
    public static final double DEFAULT_RADIUS_MILES = 25;
    private static ShelterList instance;
    private ArrayList<Shelter> shelters;

    private ShelterList() {
        shelters = new ArrayList<>();
    }
    
    public static ShelterList getInstance() {
        if (instance == null) instance = new ShelterList();
        return instance;
    }
    public ArrayList<Shelter> getShelters() { return shelters; }
    public void addShelter(Shelter shelter) {
        if (shelter != null && !shelters.contains(shelter)) shelters.add(shelter);
    }

    /**
     * Open shelters with space in the same area as the location. A location can
     * be a town ("Anytown") or a full address ("123 Main St, Anytown, USA"), in
     * which case shelters in the same town match. Shelters have no coordinates
     * yet, so radius is not used.
     */
    public ArrayList<Shelter> searchByProximity(String location, double radius) {
        ArrayList<Shelter> matches = new ArrayList<>();
        String wanted = location == null ? "" : location.trim().toLowerCase();
        String[] parts = wanted.split(",");
        String town = parts.length > 1 ? parts[1].trim() : wanted;
        for (Shelter shelter : shelters) {
            String address = shelter.getAddress() == null ? "" : shelter.getAddress().toLowerCase();
            boolean sameArea = wanted.isEmpty() || address.contains(wanted) || address.contains(town);
            if (sameArea && shelter.hasSpace()) matches.add(shelter);
        }
        return matches;
    }

    public ArrayList<Shelter> filterByAccommodation(Accommodation accommodation) {
        ArrayList<Shelter> matches = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getAccommodations() != null && shelter.getAccommodations().contains(accommodation)) {
                matches.add(shelter);
            }
        }
        return matches;
    }

    public boolean verifyStatusReport(Shelter shelter, ShelterStatus status) {
        return shelter != null && shelter.getStatus() == status;
    }

    public boolean save() { return DataWriter.saveShelters(shelters); }
}
