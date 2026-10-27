package model;

import java.util.ArrayList;

import location.Address;
import location.MapTools;

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
     * Finds the closest open shelter with room for the group and every accommodation
     * they need. Falls back to the first match if the map API can't be reached.
     * 
     * @param from where the group is starting
     * @param needs accommodations the shelter must have 
     * @param people how many people need beds
     * @return the closest matching shelter, or null if none match
     */
    public Shelter findClosestShelter(Address from, ArrayList<Accommodation> needs, int people) {
        ArrayList<Shelter> candidates = new ArrayList<>();
        ArrayList<Address> addresses = new ArrayList<>();
        
        for (Shelter shelter : shelters) {
            Address address = Address.fromString(shelter.getAddress());
            if (shelter.hasSpace() && shelter.getCapacity() - shelter.getOccupancy() >= people
            && shelter.getAccommodations().containsAll(needs)
            && address != null) {
                candidates.add(shelter);
                addresses.add(address);
            }
        }

        if (candidates.isEmpty()) {
            return null;
        }

        try {
            Address closest = MapTools.getInstance().findClosestAddress(from, addresses.toArray(new Address[0]));
            for (int i = 0; i < addresses.size(); i++) {
                if (addresses.get(i).toString().equals(closest.toString())) {
                    return candidates.get(i);
                }
            }
        } catch (Exception e) {
            System.out.println("Map service unavailable, showing the first matching shelter.");
        }
        return candidates.get(0);
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
