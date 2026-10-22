package model;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;

public class Shelter {
    private UUID shelterId;
    private String name;
    private location.MapLocation location;
    private int capacity;
    private int occupancy;
    private ShelterStatus status;
    private List<Accommodation> accommodations;
    private LocalDateTime lastUpdated;
    private ArrayList<User> shelterOperators;

    public Shelter(UUID shelterID, String name, location.MapLocation location, int capacity,
        int occupancy, ShelterStatus status, ArrayList<Accommodation> accomodations) {
            setShelterId(shelterID);
            setName(name);
            setLocation(location);
            setCapacity(capacity);
            setStatus(status);
            setOccupancy(occupancy);
            setAccommodations(accomodations);
            setLastUpdated(LocalDateTime.now());
            setShelterOperators(new ArrayList<User>());
    }

    /**
     * Changes the shelter's status. Only this shelter's operators or a
     * coordinator may do this.
     *
     * @return true if the status was changed
     */
    public boolean updateStatus(ShelterStatus status, User requestedBy) {
        if (status == null || !canManage(requestedBy)) {
            return false;
        }
        setStatus(status);
        setLastUpdated(LocalDateTime.now());
        System.out.println("Status updated by user: " + requestedBy.getUsername());
        return true;
    }

    /**
     * Adds an accommodation. Only this shelter's operators or a coordinator
     * may do this.
     *
     * @return true if the accommodation was added
     */
    public boolean addAccommodation(Accommodation accommodation, User requestedBy) {
        if (accommodation == null || !canManage(requestedBy)) {
            return false;
        }
        if (accommodations == null) {
            accommodations = new ArrayList<>();
        }
        if (accommodations.contains(accommodation)) {
            return false;
        }
        accommodations.add(accommodation);
        setLastUpdated(LocalDateTime.now());
        System.out.println("Accommodation added by user: " + requestedBy.getUsername());
        return true;
    }

    public void reportStatusFlag(ShelterStatus status, User reportedBy) {
        // TODO:Log the status flag report
        // This should be expanded to actually record the user in a log 
        String reporter = reportedBy == null ? "unknown" : reportedBy.getUsername();
        System.out.println("Status flag reported by user: " + reporter + " for status: " + status);
    }

    /**
     * Operators are matched by username, since the operator list may hold
     * a different User object than the one that is logged in.
     */
    public boolean isShelterOperator(User user) {
        if (shelterOperators == null || user == null || user.getUsername() == null) {
            return false;
        }
        for (User operator : shelterOperators) {
            if (user.getUsername().equalsIgnoreCase(operator.getUsername())) {
                return true;
            }
        }
        return false;
    }

    private boolean canManage(User user) {
        return user != null && (isShelterOperator(user) || user.hasRole(AccountRole.COORDINATOR));
    }

    public boolean hasSpace() {
        return status == ShelterStatus.OPEN && occupancy < capacity;
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

    public location.MapLocation getLocation() {
        return location;
    }

    public void setLocation(location.MapLocation location) {
        this.location = location;
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

    /**
     * Sets how many people are in the shelter. An open shelter that reaches
     * capacity becomes FULL, and a full shelter with space again reopens.
     */
    public void setOccupancy(int occupancy) {
        this.occupancy = Math.max(0, occupancy);
        if (status == ShelterStatus.OPEN && this.occupancy >= capacity) {
            status = ShelterStatus.FULL;
        } else if (status == ShelterStatus.FULL && this.occupancy < capacity) {
            status = ShelterStatus.OPEN;
        }
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
                "location='" + location + "\n" +
                "capacity=" + capacity + "\n" +
                "occupancy=" + occupancy + "\n" +
                "status=" + status + "\n" +
                "accommodations=" + accommodations + "\n" +
                "lastUpdated=" + lastUpdated + "\n" +
                "shelterOperators=" + shelterOperators + "\n" +
                '}';
    }
}