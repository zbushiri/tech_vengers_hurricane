package model;

public class ShelterList {
    private static ShelterList instance;
    private ArrayList<Shelter> shelters;
    private ShelterList() {
        shelters = new ArrayList<>();
    }
    private static ShelterList getInstance() {
        if (instance == null) {
            instance = new ShelterList();
        }
        return instance;
    }
    public ArrayList<Shelter> searchByProximity(String location, double radius) {
        ArrayList<Shelter> nearbyShelters = new ArrayList<>();
        for (Shelter shelter : shelters) {
            double distance = calculateDistance(location, shelter.getLocation());
            if (distance <= radius) {
                nearbyShelters.add(shelter);
            }
        }
        return nearbyShelters;
    }
    public ArrayList<Shelter> filterByAccomodation(Accomodation criteria) {
        ArrayList<Shelter> filteredShelters = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getAccomodation().equals(criteria)) {
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
            if (shelter.needsRefresh()) {
                sheltersNeedingRefresh.add(shelter);
            }
        }
        return sheltersNeedingRefresh;
    
    }
    public boolean save() {
        // Implementation for saving the shelter list
        return true;
    }
}
