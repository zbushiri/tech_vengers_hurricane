package model;

<<<<<<< HEAD
import java.time.LocalDateTime;
=======
import java.math.BigDecimal;
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
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
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
    }

    /**
     * Adds a shelter unless one with the same id is already in the list.
     *
     * @return true if the shelter was added
     */
    public boolean addShelter(Shelter shelter) {
        if (shelter == null) {
            return false;
        }
        for (Shelter existing : shelters) {
            if (existing.getShelterId() != null && existing.getShelterId().equals(shelter.getShelterId())) {
                return false;
            }
        }
        return shelters.add(shelter);
    }

    /**
     * Finds shelters that are not closed and are in the same town as the
     * given location. Shelters with open space are listed first.
     *
     * Addresses are stored as plain text without coordinates, so the radius
     * cannot be measured yet; the town in the address is used instead.
     * TODO: Use radiusMiles once shelters and users store coordinates.
     */
    public ArrayList<Shelter> searchByProximity(String location, double radiusMiles) {
        String town = townOf(location);
        ArrayList<Shelter> nearbyShelters = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getStatus() == ShelterStatus.CLOSED) {
                continue;
            }
            String address = shelter.getAddress() == null ? "" : shelter.getAddress().toLowerCase();
            if (town.isEmpty() || address.contains(town)) {
                nearbyShelters.add(shelter);
            }
        }
        nearbyShelters.sort((a, b) -> Boolean.compare(b.hasSpace(), a.hasSpace()));
        return nearbyShelters;
    }

    /**
     * Gets the town from a location like "123 Main St, Anytown, USA" or
     * "Columbia, SC". Street parts and coordinates start with a number or a
     * minus sign and are skipped.
     *
     * @return the town in lower case, or "" if none was found
     */
    private static String townOf(String location) {
        if (location == null) {
            return "";
        }
        for (String part : location.split(",")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty() && !Character.isDigit(trimmed.charAt(0)) && trimmed.charAt(0) != '-') {
                return trimmed.toLowerCase();
            }
        }
        return "";
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

    /**
     * Finds shelters whose information has not been updated within
     * REFRESH_HOURS, or has never been updated.
     */
    public ArrayList<Shelter> getSheltersNeedingRefresh() {
<<<<<<< HEAD
        LocalDateTime cutoff = LocalDateTime.now().minusHours(REFRESH_HOURS);
        ArrayList<Shelter> sheltersNeedingRefresh = new ArrayList<>();
        for (Shelter shelter : shelters) {
            if (shelter.getLastUpdated() == null || shelter.getLastUpdated().isBefore(cutoff)) {
                sheltersNeedingRefresh.add(shelter);
            }
=======
        ArrayList<Shelter> sheltersNeedingRefresh = new ArrayList<>();
        for (Shelter shelter : shelters) {
            //if (shelter.needsRefresh()) {
                //sheltersNeedingRefresh.add(shelter);
            //}
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
        }
        return sheltersNeedingRefresh;
    }
    public boolean save() {
<<<<<<< HEAD
        return DataWriter.saveShelters();
=======
        return true;
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
    }
}
