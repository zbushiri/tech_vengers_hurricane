package model;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * A shelter with separate human capacity, pet capacity, operators and saved admissions.
 * @author Team Tech-vengers
 */
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
    private final ArrayList<ShelterAdmission> admissions = new ArrayList<>();
    private int petCapacity = 0;

    /**
     * Creates a shelter with its identity, human capacity, operating status and services.
     * @param shelterID shelter's unique identifier
     * @param name shelter's display name
     * @param address shelter's street address
     * @param capacity maximum number of human occupants
     * @param occupancy current number of human occupants; pets are counted separately
     * @param status shelter operating status to store or compare
     * @param accomodations services available at the shelter
     */
    public Shelter(UUID shelterID, String name, String address, int capacity,
        int occupancy, ShelterStatus status, ArrayList<Accommodation> accomodations) {
            setShelterId(shelterID);
            setName(name);
            setAddress(address);
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
     * @param status shelter operating status to store or compare
     * @param requestedBy operator or coordinator requesting the change
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
     * @param accommodation shelter service to require or add
     * @param requestedBy operator or coordinator requesting the change
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

    /**
     * Prints a status report and its reporter; reports are not yet persisted.
     * @param status shelter operating status to store or compare
     * @param reportedBy user reporting the status, or null for an unknown reporter
     */
    public void reportStatusFlag(ShelterStatus status, User reportedBy) {
        // TODO:Log the status flag report
        // This should be expanded to actually record the user in a log 
        String reporter = reportedBy == null ? "unknown" : reportedBy.getUsername();
        System.out.println("Status flag reported by user: " + reporter + " for status: " + status);
    }

    /**
     * Operators are matched by username, since the operator list may hold
     * a different User object than the one that is logged in.
     * @param user account to check against the shelter operator list
     * @return true if the user's username matches a listed operator, ignoring case
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
        return user != null && ((user.hasRole(AccountRole.SHELTER_OPERATOR) && isShelterOperator(user))
                || user.hasRole(AccountRole.COORDINATOR));
    }

    /**
     * Checks in a household once, enforcing destination, permissions and capacity.
     * @param request assigned household request
     * @param manager this shelter's operator or a coordinator
     * @return true if a new admission was recorded
     */
    public synchronized boolean checkIn(ReliefRequest request, User manager) {
        if (!canManage(manager) || request == null || hasAdmission(request.getRequestId())
                || !shelterId.equals(request.getDestinationShelterId())
                || (request.getStatus() != RequestStatus.ASSIGNED && request.getStatus() != RequestStatus.IN_PROGRESS)
                || request.getFamilyMembers().size() != request.getNumberOfPeopleNeeded()
                || request.getPets().size() != request.getNumberOfAnimals()
                || !canAccommodate(request)) return false;
        admissions.add(new ShelterAdmission(request.getRequestId(), request.getFamilyMembers(), request.getPets()));
        setOccupancy(occupancy + request.getNumberOfPeopleNeeded());
        setLastUpdated(LocalDateTime.now());
        return true;
    }

    /**
     * Tests all required services and both human and animal space.
     * @param request household to accommodate
     * @return whether the open shelter can take the whole household
     */
    public boolean canAccommodate(ReliefRequest request) {
        return hasSpace() && capacity - occupancy >= request.getNumberOfPeopleNeeded()
                && (request.getMedicalNeeds().isBlank() || accommodations.contains(Accommodation.MEDICAL_SUPPORT))
                && (request.getNumberOfAnimals() == 0 || accommodations.contains(Accommodation.PET_FRIENDLY))
                && petCapacity - getPetOccupancy() >= request.getNumberOfAnimals();
    }

    /**
     * Has admission.
     * @param requestId request to look up
     * @return whether the household has already checked in
     */
    public boolean hasAdmission(UUID requestId) {
        return admissions.stream().anyMatch(admission -> admission.requestId().equals(requestId));
    }
    /**
     * Returns the admissions.
     * @return saved household admissions; legacy aggregate occupancy may have no names
     */
    public List<ShelterAdmission> getAdmissions() { return List.copyOf(admissions); }
    /**
     * Restores admissions without increasing the separately persisted occupancy again.
     * @param saved household admissions read from the shelter JSON file
     */
    void restoreAdmissions(List<ShelterAdmission> saved) { admissions.clear(); admissions.addAll(saved); }
    /**
     * Returns the occupants.
     * @return named occupants recorded by household check-in
     */
    public List<Person> getOccupants() { return admissions.stream().flatMap(a -> a.people().stream()).toList(); }
    /**
     * Returns the pets.
     * @return accompanying pets currently recorded at this shelter
     */
    public List<Pet> getPets() { return admissions.stream().flatMap(a -> a.pets().stream()).toList(); }
    /**
     * Returns the pet occupancy.
     * @return number of accompanying pets
     */
    public int getPetOccupancy() { return getPets().size(); }
    /**
     * Returns the available spaces.
     * @return available human beds
     */
    public int getAvailableSpaces() { return Math.max(0, capacity - occupancy); }
    /**
     * Returns the pet capacity.
     * @return maximum accompanying pets
     */
    public int getPetCapacity() { return petCapacity; }
    /**
     * Sets the pet capacity.
     * @param value maximum accompanying pets, never negative
     */
    public void setPetCapacity(int value) {
        if (value < 0) throw new IllegalArgumentException("Pet capacity cannot be negative.");
        petCapacity = value;
    }

    /**
     * Checks whether the shelter is open and has at least one unused human bed.
     * @return true if the status is OPEN and human occupancy is below capacity
     */
    public boolean hasSpace() {
        return status == ShelterStatus.OPEN && occupancy < capacity;
    }

    /**
     * Returns the shelter id.
     * @return the shelter id
     */
    public UUID getShelterId() {
        return shelterId;
    }

    /**
     * Sets the shelter id.
     * @param shelterId shelter's unique identifier
     */
    public void setShelterId(UUID shelterId) {
        this.shelterId = shelterId;
    }

    /**
     * Returns the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name shelter's display name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the address.
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address.
     * @param address shelter's street address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the capacity.
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the capacity.
     * @param capacity maximum number of human occupants
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Returns the occupancy.
     * @return the occupancy
     */
    public int getOccupancy() {
        return occupancy;
    }

    /**
     * Sets how many people are in the shelter. An open shelter that reaches
     * capacity becomes FULL, and a full shelter with space again reopens.
     * @param occupancy current number of human occupants; pets are counted separately
     */
    public void setOccupancy(int occupancy) {
        this.occupancy = Math.max(0, occupancy);
        if (status == ShelterStatus.OPEN && this.occupancy >= capacity) {
            status = ShelterStatus.FULL;
        } else if (status == ShelterStatus.FULL && this.occupancy < capacity) {
            status = ShelterStatus.OPEN;
        }
    }

    /**
     * Returns the status.
     * @return the status
     */
    public ShelterStatus getStatus() {
        return status;
    }

    /**
     * Sets the status.
     * @param status shelter operating status to store or compare
     */
    public void setStatus(ShelterStatus status) {
        this.status = status;
    }

    /**
     * Returns the accommodations.
     * @return the accommodations
     */
    public List<Accommodation> getAccommodations() {
        return accommodations;
    }

    /**
     * Sets the accommodations.
     * @param accommodations services available at the shelter
     */
    public void setAccommodations(List<Accommodation> accommodations) {
        this.accommodations = accommodations;
    }

    /**
     * Returns the last updated.
     * @return the last updated
     */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Sets the last updated.
     * @param lastUpdated time when the shelter's information was last updated
     */
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Returns the shelter operators.
     * @return the shelter operators
     */
    public ArrayList<User> getShelterOperators() {
        return shelterOperators;
    }

    /**
     * Sets the shelter operators.
     * @param shelterOperators accounts assigned to manage this shelter
     */
    public void setShelterOperators(ArrayList<User> shelterOperators) {
        this.shelterOperators = shelterOperators;
    }

    /**
     * Formats this object for display.
     * @return shelter identity, address, capacity, occupancy, services and operators
     */
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