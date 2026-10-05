package model;

import java.util.ArrayList;

public class ShelterList {
    private static ShelterList instance;
    private ArrayList<Shelter> shelters;

    private ShelterList() {}


    public static ShelterList getInstance() {
        if (instance == null) {
            instance = new ShelterList();
            instance.shelters = new ArrayList<>();
        }
        return instance;
    }
    
    public ArrayList<Shelter> searchByProximity(String location, double radius) {
                      
        // ArrayList<Shelter> nearbyShelters = new ArrayList<>();
        // for (Shelter shelter : shelters) {
            // double distance = calculateDistance(location, shelter.getAddress());
            // if (distance <= radius) {
                // nearbyShelters.add(shelter);
            //}
       // }
        // return nearbyShelters;
        return new ArrayList<>();
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
        //ArrayList<Shelter> sheltersNeedingRefresh = new ArrayList<>();
        //for (Shelter shelter : shelters) {
            //if (shelter.needsRefresh()) {
                //sheltersNeedingRefresh.add(shelter);
            //}
        //}
        //return sheltersNeedingRefresh;
        return new ArrayList<>();
    }
    public boolean save() {
        // Implementation for saving the shelter list
        return true;
    }
}
