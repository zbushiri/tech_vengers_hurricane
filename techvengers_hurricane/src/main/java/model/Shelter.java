package model;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;

public class Shelter {
    private UUID shelterId;
    private String name;
    private String address;
    private int capacity;
    private int occupancy;
    private ShelterStatus status;
    private List<Accommodation> accommodations;
    private LocalDateTime lastUpdated;
    private ArrayList<User> shelterOperators;

    public Shelter(UUID shelterID, String name, String address, int capacity,
        int occupancy, ShelterStatus status, ArrayList<Accommodation> accomodations) {
            setShelterId(shelterID);
            setName(name);
            setAddress(address);
            setCapacity(capacity);
            setOccupancy(occupancy);
            setStatus(status);
            setAccommodations(accomodations);
            setLastUpdated(LocalDateTime.now());
            setShelterOperators(new ArrayList<User>());
    }

    public void updateStatus(ShelterStatus status, User requestedBy) {
        setStatus(status);
        setLastUpdated(LocalDateTime.now());
        // TODO: Log the user who requested the status update
        // This should be expanded to actually record the user in a log 
        System.out.println("Status updated by user: " + requestedBy.getUsername());
    }

    public void addAccommodations(Accommodation accommodation, User requestedBy) {
        if (accommodations == null) {
            accommodations = new ArrayList<>();
        }
        accommodations.add(accommodation);
        setLastUpdated(LocalDateTime.now());
        // TODO: Log the user who requested the addition
        // This should be expanded to actually record the user in a log 
        System.out.println("Accommodation added by user: " + requestedBy.getUsername());
    }

    public void reportStatusFlag(ShelterStatus status, User reportedBy) {
        // TODO:Log the status flag report
        // This should be expanded to actually record the user in a log 
        System.out.println("Status flag reported by user: " + reportedBy.getUsername() + " for status: " + status);
    }

    public boolean isShelterOperator(User user) {
        if (shelterOperators == null) {
            return false;
        }
        return shelterOperators.contains(user);
    }
    
    public UUID getShelterId() {
        return shelterId;
    }

    public void setShelterId(UUID shelterId) {
        this.shelterId = shelterId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(int occupancy) {
        this.occupancy = occupancy;
    }

    public ShelterStatus getStatus() {
        return status;
    }

    public void setStatus(ShelterStatus status) {
        this.status = status;
    }

    public List<Accommodation> getAccommodations() {
        return accommodations;
    }

    public void setAccommodations(List<Accommodation> accommodations) {
        this.accommodations = accommodations;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public ArrayList<User> getShelterOperators() {
        return shelterOperators;
    }

    public void setShelterOperators(ArrayList<User> shelterOperators) {
        this.shelterOperators = shelterOperators;
    }

    @Override 
    public String toString() {
        return "Shelter{" + "\n" +
                "shelterId=" + shelterId + "\n" +
                "name='" + name + "\n" +
                "address='" + address + "\n" +
                "capacity=" + capacity + "\n" +
                "occupancy=" + occupancy + "\n" +
                "status=" + status + "\n" +
                "accommodations=" + accommodations + "\n" +
                "lastUpdated=" + lastUpdated + "\n" +
                "shelterOperators=" + shelterOperators + "\n" +
                '}';
    }
}