package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
public class ShelterList {
    private static ShelterList instance;
    private ArrayList<Shelter> shelters;
    public class Location {
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
            if (shelter.getAccommodations().contains(criteria)) {
                filteredShelters.add(shelter);
            }
        }
        return filteredShelters;
    }
    public boolean verifyStatusReport(Shelter shelter, ShelterStatus status) {
        return shelter.getStatus().equals(status);
    }
    public boolean save() {
        DataWriter.saveShelters(shelters);
        return true;
    }
}
