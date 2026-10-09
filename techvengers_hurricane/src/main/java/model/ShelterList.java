package model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
public class ShelterList {
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
    
    public ArrayList<Shelter> searchByProximity(Location from, double radius) {
        ArrayList<Shelter> nearbyShelters = new ArrayList<>();
         for (Shelter shelter : shelters) {
            double distance = calculateDistance(from, instance.locations.get(shelter.getAddress()));
             if (distance <= radius) {
                 nearbyShelters.add(shelter);
             }
        }
        return nearbyShelters;
    }
    private double calculateDistance(Location start, Location end) {
        BigDecimal startLat = new BigDecimal(start.latitude);
        BigDecimal startLon = new BigDecimal(start.longitude);
        BigDecimal endLat = new BigDecimal(end.latitude);
        BigDecimal endLon = new BigDecimal(end.longitude);
        return Math.sqrt(Math.pow(start.latitude - end.latitude, 2) + Math.pow(start.longitude - end.longitude, 2));
    }
    public ArrayList<Shelter> filterByAccommodation(Accommodation criteria) {
        ArrayList<Shelter> filteredShelters = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getAccommodations().contains(criteria)) {
                filteredShelters.add(shelter);
            }
        }
        return filteredShelters;
    }
    public boolean verifyStatusReport(Shelter shelter, ShelterStatus status) {
        return shelter.getStatus().equals(status);
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
    }
}
