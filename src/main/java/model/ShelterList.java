package model;

import java.util.ArrayList;

import location.Address;
import location.MapTools;

/**
 * Stores and searches shelters.
 * @author Team Tech-vengers
 */
public class ShelterList {
    /**
     * default radius miles.
     */
    public static final double DEFAULT_RADIUS_MILES = 25;
    private static ShelterList instance;
    private ArrayList<Shelter> shelters;

    private ShelterList() {
        shelters = new ArrayList<>();
    }
    
    /**
     * Returns the shared shelter list.
     * @return the shared instance, created on first access
     */
    public static ShelterList getInstance() {
        if (instance == null) instance = new ShelterList();
        return instance;
    }
    /**
     * Returns the shelters.
     * @return the shelters
     */
    public ArrayList<Shelter> getShelters() { return shelters; }
    /**
     * Adds a nonnull shelter unless the same object is already in the list.
     * @param shelter shelter to add or inspect
     */
    public void addShelter(Shelter shelter) {
        if (shelter != null && !shelters.contains(shelter)) shelters.add(shelter);
    }

    /**
     * Finds the closest open shelter with room for the group and every accommodation
     * they need. Uses driving distance; reports a service failure rather than inventing a nearest result.
     * 
     * @param from where the group is starting
     * @param needs accommodations the shelter must have 
     * @param people how many people need beds
     * @return the closest matching shelter, or null if none match
     */
    public Shelter findClosestShelter(Address from, ArrayList<Accommodation> needs, int people) {
        try {
            return findClosestShelter(from, needs, people, 0, MapTools.getInstance());
        } catch (java.io.IOException e) {
            throw new IllegalStateException("Cannot determine the nearest shelter: " + e.getMessage(), e);
        }
    }

    /**
     * Selects the eligible shelter with the shortest actual driving route.
     * @param from household pickup address
     * @param needs every required accommodation
     * @param people human beds needed
     * @param pets animal spaces needed
     * @param routes driving-route provider
     * @return nearest eligible shelter, or null when no shelter has room
     * @throws java.io.IOException if any eligible route cannot be compared reliably
     */
    public Shelter findClosestShelter(Address from, java.util.List<Accommodation> needs,
            int people, int pets, location.RoutingService routes) throws java.io.IOException {
        if (from == null || people < 1 || pets < 0) throw new IllegalArgumentException("Invalid household or address.");
        Shelter closest = null;
        double shortest = Double.POSITIVE_INFINITY;
        for (Shelter shelter : shelters) {
            if (!shelter.hasSpace() || shelter.getAvailableSpaces() < people
                    || !shelter.getAccommodations().containsAll(needs)
                    || (pets > 0 && !shelter.getAccommodations().contains(Accommodation.PET_FRIENDLY))
                    || shelter.getPetCapacity() - shelter.getPetOccupancy() < pets) continue;
            double distance = routes.route(from, Address.fromString(shelter.getAddress())).distanceMeters();
            if (distance < shortest) { shortest = distance; closest = shelter; }
        }
        return closest;
    }

    /**
     * Finds open shelters within the supplied driving-distance radius.
     * @param location full departure address
     * @param radius maximum driving miles
     * @return shelters ordered by driving distance
     * @throws IllegalStateException when driving distances are unavailable
     */
    public ArrayList<Shelter> searchByProximity(String location, double radius) {
        Address from = Address.fromString(location);
        if (from == null || radius < 0) throw new IllegalArgumentException("Enter a full street, city, state and ZIP, and a nonnegative radius.");
        java.util.Map<Shelter, Double> distances = new java.util.HashMap<>();
        try {
            for (Shelter shelter : shelters) {
                if (shelter.hasSpace()) {
                    double miles = MapTools.getInstance().route(from, Address.fromString(shelter.getAddress())).distanceMeters() / 1609.344;
                    if (miles <= radius) distances.put(shelter, miles);
                }
            }
        } catch (java.io.IOException e) { throw new IllegalStateException("Shelter distances unavailable: " + e.getMessage(), e); }
        return new ArrayList<>(distances.keySet().stream().sorted(java.util.Comparator.comparingDouble(distances::get)).toList());
    }

    /**
     * Finds shelters offering the requested service, regardless of status or remaining space.
     * @param accommodation shelter service to require or add
     * @return shelters that list the requested accommodation
     */
    public ArrayList<Shelter> filterByAccommodation(Accommodation accommodation) {
        ArrayList<Shelter> matches = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getAccommodations() != null && shelter.getAccommodations().contains(accommodation)) {
                matches.add(shelter);
            }
        }
        return matches;
    }

    /**
     * Compares a reported operating status with the shelter's current stored status.
     * @param shelter shelter to add or inspect
     * @param status shelter operating status to store or compare
     * @return true if the shelter exists and its stored status equals the reported status
     */
    public boolean verifyStatusReport(Shelter shelter, ShelterStatus status) {
        return shelter != null && shelter.getStatus() == status;
    }

    /**
     * Saves every shelter in the shared list to JSON.
     * @return true if the shelters JSON file was written successfully
     */
    public boolean save() { return DataWriter.saveShelters(shelters); }
}
