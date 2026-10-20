package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
public class ShelterList {
    public static final double DEFAULT_RADIUS_MILES = 25;
    /** A shelter whose status is older than this many hours needs a refresh. */
    public static final int REFRESH_HOURS = 24;

    private static ShelterList instance;
    private ArrayList<Shelter> shelters;
    private class Location {
        double latitude;
        double longitude;
        Random rand = new Random();
        public Location() {
            latitude = rand.nextDouble() * 30;
            longitude = rand.nextDouble() * 60;
        }
    }

    private HashMap<String, Location> locations;
    private ShelterList() {
        shelters = new ArrayList<>();
        locations = new HashMap<>();
    }

    public static ShelterList getInstance() {
        if (instance == null) {
            instance = new ShelterList();
            instance.shelters = new ArrayList<>();
            instance.locations = new HashMap<>();
            for (Shelter shelter : instance.shelters) {
                for (String location : instance.locations.keySet()) {
                    if (shelter.getAddress().equals(location)) {
                        break;
                    }
                }
                instance.locations.put(shelter.getAddress(), instance.new Location());
            }
        }
        return instance;
    }

<<<<<<< HEAD
    public ArrayList<Shelter> getShelters() {
        return shelters;
=======
    public void addShelter(Shelter shelter) {
        shelters.add(shelter);
    }

    public ArrayList<Shelter> getShelters() {
        return shelters;
    }
    
    public ArrayList<Shelter> searchByProximity(Location from, double radius) {
        ArrayList<Shelter> nearbyShelters = new ArrayList<>();
         for (Shelter shelter : shelters) {
            Location location = instance.locations.get(shelter.getAddress());
            if (location == null) {
                continue;
            }
            double distance = calculateDistance(from, location);
             if (distance <= radius) {
                 nearbyShelters.add(shelter);
             }
        }
        return nearbyShelters;
    }

    private double calculateDistance(Location start, Location end) {
        return Math.sqrt(Math.pow(start.latitude - end.latitude, 2) + Math.pow(start.longitude - end.longitude, 2));
    }
    public ArrayList<Shelter> filterByAccommodation(Accommodation criteria) {
        ArrayList<Shelter> filteredShelters = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getAccommodations() != null && shelter.getAccommodations().contains(criteria)) {
                filteredShelters.add(shelter);
            }
        }
        return filteredShelters;
    }
    public boolean verifyStatusReport(Shelter shelter, ShelterStatus status) {
        return shelter != null && shelter.getStatus() == status;
    }
    public ArrayList<Shelter> getSheltersNeedingRefresh() {
        ArrayList<Shelter> sheltersNeedingRefresh = new ArrayList<>();
        for (Shelter shelter : shelters) {
            //if (shelter.needsRefresh()) {
                //sheltersNeedingRefresh.add(shelter);
            //}
        }
        return sheltersNeedingRefresh;
    }
    public boolean save() {
        return true;
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
    }
}
