package model;
import java.util.UUID;
import java.util.Date;

/**
 * A household relief request, its assignment, destination, and workflow state.
 * @author Team Tech-vengers
 */
public class ReliefRequest {
    /**
     * A request for at least this many people moves up one priority level.
     */
    public static final int LARGE_GROUP_SIZE = 10;

    private UUID requestId;
    private RequestType type;
    private String description;
    private RequestStatus status;
    private PriorityLevel priority;
    private Date dateSubmitted;
    private String location;
    private int numberOfPeopleNeeded;
    private int numberOfAnimals;
    private String animalNotes;
    private String photoUrl;
    private boolean isSuspicious;
    private ReliefRequest isDuplicateOf;
    private HurricaneEvent forHurricane;
    private VolunteerProfile assignedVolunteer;
    private User submittedBy;
    private java.util.List<Person> familyMembers = new java.util.ArrayList<>();
    private java.util.List<Pet> pets = new java.util.ArrayList<>();
    private String medicalNeeds = "";
    private UUID destinationShelterId;

    /**
     * Creates relief request.
     * @param type type for this operation
     * @param description description for this operation
     * @param location location for this operation
     * @param numberOfPeopleNeeded number of people needed for this operation
     */
    public ReliefRequest(RequestType type, String description, String location, int numberOfPeopleNeeded) {
        this(UUID.randomUUID(), type, description, location, numberOfPeopleNeeded,
             0, "", "", false, null, null);
    }

    /**
     * Copies another request, including its status, priority and date.
     * @param request request for this operation
     */
    public ReliefRequest(ReliefRequest request) {
        this(request.getRequestId(), request.getType(), request.getDescription(), request.getLocation(),
             request.getNumberOfPeopleNeeded(), request.getNumberOfAnimals(), request.getAnimalNotes(),
             request.getPhotoUrl(), request.isSuspicious(), request.getIsDuplicateOf(), request.getForHurricane());
        restoreStatus(request.getStatus());
        setPriority(request.getPriority());
        setDateSubmitted(request.getDateSubmitted());
        setAssignedVolunteer(request.getAssignedVolunteer());
        setSubmittedBy(request.getSubmittedBy());
        setFamilyMembers(request.getFamilyMembers());
        setPets(request.getPets());
        setMedicalNeeds(request.getMedicalNeeds());
        setDestinationShelterId(request.getDestinationShelterId());
    }


    /**
     * Creates relief request.
     * @param requestId request id for this operation
     * @param type type for this operation
     * @param description description for this operation
     * @param location location for this operation
     * @param numberOfPeopleNeeded number of people needed for this operation
     * @param numberOfAnimals number of animals for this operation
     * @param animalNotes animal notes for this operation
     * @param photoUrl photo url for this operation
     * @param isSuspicious is suspicious for this operation
     * @param isDuplicateOf is duplicate of for this operation
     * @param forHurricane for hurricane for this operation
     */
    public ReliefRequest(UUID requestId, RequestType type, String description, String location, int numberOfPeopleNeeded,
        int numberOfAnimals, String animalNotes, String photoUrl, boolean isSuspicious, ReliefRequest isDuplicateOf, HurricaneEvent forHurricane) {
        setRequestId(requestId);
        setType(type);
        setDescription(description);
        setStatus(RequestStatus.SUBMITTED);
        setDateSubmitted(new Date());
        setLocation(location);
        setNumberOfPeopleNeeded(numberOfPeopleNeeded);
        setNumberOfAnimals(numberOfAnimals);
        setAnimalNotes(animalNotes);
        setPhotoUrl(photoUrl);
        setSuspicious(isSuspicious);
        setIsDuplicateOf(isDuplicateOf);
        setForHurricane(forHurricane);
        // Priority depends on the fields above, so it is set last.
        setPriority(calculatePriority());
    }

    /**
     * Cancels the request. A request that is already fulfilled stays fulfilled.
     */
    public void cancel() {
        if (!isOpen()) {
            return;
        }
        setStatus(RequestStatus.CANCELLED);
    }

    /**
     * Add detail.
     * @param detail detail for this operation
     */
    public void addDetail(String detail) {
        if (detail == null || detail.trim().isEmpty()) {
            return;
        }
        if (description == null || description.isEmpty()) {
            setDescription(detail);
        } else {
            setDescription(description + "\n" + detail);
        }
    }

    /**
     * Flag as suspicious.
     */
    public void flagAsSuspicious() {
        setSuspicious(true);
    }

    /**
     * Lets a volunteer take this request. It only works when the request has
     * not been claimed yet and the volunteer is available and verified.
     * The volunteer is then marked as on assignment.
     * @param volunteer volunteer for this operation
     */
    public void claimRequest(VolunteerProfile volunteer) {
        if (status != RequestStatus.SUBMITTED || volunteer == null || !volunteer.canClaimRequests() || !isForVolunteers() || hasOpenOriginal()) {
            return;
        }
        setAssignedVolunteer(volunteer);
        setStatus(RequestStatus.ASSIGNED);
        volunteer.setAvailability(AvailabilityStatus.ON_ASSIGNMENT);
    }

    /**
     * Medical and hazard requests start at HIGH, shelter and food at MEDIUM,
     * and everything else at LOW. Large groups move up one level.
     * @return the result of calculate priority
     */
    public PriorityLevel calculatePriority() {
        PriorityLevel level;
        if ((medicalNeeds != null && !medicalNeeds.isBlank()) || type == RequestType.MEDICAL || type == RequestType.HAZARD) {
            level = PriorityLevel.HIGH;
        } else if (type == RequestType.SHELTER || type == RequestType.FOOD) {
            level = PriorityLevel.MEDIUM;
        } else {
            level = PriorityLevel.LOW;
        }
        if (numberOfPeopleNeeded >= LARGE_GROUP_SIZE && level != PriorityLevel.CRITICAL) {
            level = PriorityLevel.values()[level.ordinal() + 1];
        }
        return level;
    }
    /**
     * Is open.
     * @return whether is open
     */
    public boolean isOpen() {
        return status == RequestStatus.SUBMITTED || status == RequestStatus.ASSIGNED || status == RequestStatus.IN_PROGRESS;
    }
    
    /**
     * Is for volunteers.
     * @return whether is for volunteers
     */
    public boolean isForVolunteers() {
        return type == RequestType.FOOD || type == RequestType.TRANSPORTATION ||
        type == RequestType.SUPPLIES || type == RequestType.OTHER;
    }

    /**
     * Completes an assignment only for a coordinator or the destination's manager.
     * A shelter-bound household must be checked in before completion.
     * @param manager user requesting completion; volunteers alone are denied
     * @return true if an authorized manager completed the active request
     */
    public boolean completeRequest(User manager) {
        if (manager == null || (status != RequestStatus.ASSIGNED && status != RequestStatus.IN_PROGRESS)) return false;
        Shelter destination = ShelterList.getInstance().getShelters().stream()
                .filter(shelter -> shelter.getShelterId().equals(destinationShelterId)).findFirst().orElse(null);
        if (destinationShelterId != null && (destination == null || !destination.hasAdmission(requestId))) return false;
        boolean coordinator = manager.hasRole(AccountRole.COORDINATOR);
        boolean operator = destination != null && manager.hasRole(AccountRole.SHELTER_OPERATOR)
                && destination.isShelterOperator(manager);
        if (!coordinator && !operator) return false;
        setStatus(RequestStatus.FULFILLED);
        return true;
    }

    /**
     * Returns the family members.
     * @return named people traveling on this request, including the account holder
     */
    public java.util.List<Person> getFamilyMembers() { return java.util.List.copyOf(familyMembers); }
    /**
     * Sets the family members.
     * @param people named household members, including the account holder
     */
    public void setFamilyMembers(java.util.List<Person> people) { familyMembers = new java.util.ArrayList<>(people); }
    /**
     * Returns the pets.
     * @return accompanying pets
     */
    public java.util.List<Pet> getPets() { return java.util.List.copyOf(pets); }
    /**
     * Sets the pets.
     * @param animals accompanying pets; updates the legacy animal count and notes
     */
    public void setPets(java.util.List<Pet> animals) {
        pets = new java.util.ArrayList<>(animals);
        numberOfAnimals = pets.size();
        animalNotes = pets.stream().map(pet -> pet.name() + " (" + pet.species() + ")")
                .collect(java.util.stream.Collectors.joining(", "));
    }
    /**
     * Returns the medical needs.
     * @return medical assistance required by this household
     */
    public String getMedicalNeeds() { return medicalNeeds; }
    /**
     * Sets the medical needs.
     * @param needs medical assistance required, or empty text for none
     */
    public void setMedicalNeeds(String needs) { medicalNeeds = needs == null ? "" : needs; priority = calculatePriority(); }
    /**
     * Returns the destination shelter id.
     * @return selected shelter identifier, or null before selection
     */
    public UUID getDestinationShelterId() { return destinationShelterId; }
    /**
     * Sets the destination shelter id.
     * @param shelterId selected shelter identifier
     */
    public void setDestinationShelterId(UUID shelterId) { destinationShelterId = shelterId; }

    /**
     * A duplicate may enter the queue once its original is no longer open.
     * @return whether has open original
     */
    public boolean hasOpenOriginal() {
        return isDuplicateOf != null && isDuplicateOf.isOpen();
    }

    /**
     * Start request.
     * @param volunteer volunteer for this operation
     * @return true when the operation succeeds
     */
    public boolean startRequest(VolunteerProfile volunteer) {
        if (!isAssignedTo(volunteer) || status != RequestStatus.ASSIGNED) return false;
        setStatus(RequestStatus.IN_PROGRESS);
        return true;
    }

    private boolean isAssignedTo(VolunteerProfile volunteer) {
        return volunteer != null && assignedVolunteer != null
                && assignedVolunteer.getUserId().equals(volunteer.getUserId());
    }

    void updateStatus(RequestStatus status) {
        setStatus(status);
    }

    /**
     * Returns the for hurricane.
     * @return the for hurricane
     */
    public HurricaneEvent getForHurricane() {
        return forHurricane;
    }

    /**
     * Returns the request id.
     * @return the request id
     */
    public UUID getRequestId() {
        return requestId;
    }
    
    /**
     * Returns the type.
     * @return the type
     */
    public RequestType getType() {
        return type;
    }

    /**
     * Returns the description.
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the status.
     * @return the status
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Returns the priority.
     * @return the priority
     */
    public PriorityLevel getPriority() {
        return priority;
    }

    /**
     * Returns the date submitted.
     * @return the date submitted
     */
    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    /**
     * Returns the location.
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Returns the number of people needed.
     * @return the number of people needed
     */
    public int getNumberOfPeopleNeeded() {
        return numberOfPeopleNeeded;
    }

    /**
     * Returns the number of animals.
     * @return the number of animals
     */
    public int getNumberOfAnimals() {
        return numberOfAnimals;
    }

    /**
     * Returns the animal notes.
     * @return the animal notes
     */
    public String getAnimalNotes() {
        return animalNotes;
    }

    /**
     * Returns the photo url.
     * @return the photo url
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * Is suspicious.
     * @return whether is suspicious
     */
    public boolean isSuspicious() {
        return isSuspicious;
    }

    /**
     * Returns the is duplicate of.
     * @return the is duplicate of
     */
    public ReliefRequest getIsDuplicateOf() {
        return isDuplicateOf;
    }

    /**
     * Sets the request id.
     * @param requestId request id for this operation
     */
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }
    /**
     * Sets the type.
     * @param type type for this operation
     */
    public void setType(RequestType type) {
        this.type = type;
        priority = calculatePriority();
    }
    /**
     * Sets the description.
     * @param description description for this operation
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** Enforces transitions during normal use. Loading uses restoreStatus. */
    @SuppressWarnings("deprecation")
    void setStatus(RequestStatus next) {
        if (next == null) return;
        if (next == RequestStatus.COMPLETE) next = RequestStatus.FULFILLED;
        if (status == next) return;
        boolean allowed = status == null && next == RequestStatus.SUBMITTED;
        if (status == RequestStatus.SUBMITTED) {
            allowed = next == RequestStatus.CANCELLED
                    || (next == RequestStatus.ASSIGNED && assignedVolunteer != null);
        } else if (status == RequestStatus.ASSIGNED) {
            allowed = next == RequestStatus.IN_PROGRESS || next == RequestStatus.FULFILLED
                    || next == RequestStatus.CANCELLED;
        } else if (status == RequestStatus.IN_PROGRESS) {
            allowed = next == RequestStatus.FULFILLED || next == RequestStatus.CANCELLED;
        }
        if (!allowed) return;
        status = next;
        if ((next == RequestStatus.FULFILLED || next == RequestStatus.CANCELLED)
                && assignedVolunteer != null) {
            assignedVolunteer.setAvailability(AvailabilityStatus.AVAILABLE);
        }
    }

    /** Restores persisted state without replaying workflow side effects. */
    @SuppressWarnings("deprecation")
    void restoreStatus(RequestStatus saved) {
        status = saved == RequestStatus.COMPLETE ? RequestStatus.FULFILLED : saved;
    }

    /**
     * Sets the priority.
     * @param priority priority for this operation
     */
    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    /**
     * Sets the date submitted.
     * @param dateSubmitted date submitted for this operation
     */
    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    /**
     * Sets the location.
     * @param location location for this operation
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the number of people needed.
     * @param numberOfPeopleNeeded number of people needed for this operation
     */
    public void setNumberOfPeopleNeeded(int numberOfPeopleNeeded) {
        this.numberOfPeopleNeeded = numberOfPeopleNeeded;
        priority = calculatePriority();
    }

    /**
     * Sets the number of animals.
     * @param numberOfAnimals number of animals for this operation
     */
    public void setNumberOfAnimals(int numberOfAnimals) {
        this.numberOfAnimals = numberOfAnimals;
    }

    /**
     * Sets the animal notes.
     * @param animalNotes animal notes for this operation
     */
    public void setAnimalNotes(String animalNotes) {
        this.animalNotes = animalNotes;
    }

    /**
     * Sets the photo url.
     * @param photoUrl photo url for this operation
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Sets the suspicious.
     * @param isSuspicious is suspicious for this operation
     */
    public void setSuspicious(boolean isSuspicious) {
        this.isSuspicious = isSuspicious;
    }

    /**
     * Sets the is duplicate of.
     * @param isDuplicateOf is duplicate of for this operation
     */
    public void setIsDuplicateOf(ReliefRequest isDuplicateOf) {
        this.isDuplicateOf = isDuplicateOf;
    }

    /**
     * Sets the for hurricane.
     * @param forHurricane for hurricane for this operation
     */
    public void setForHurricane(HurricaneEvent forHurricane) {
        this.forHurricane = forHurricane;
    }

    /**
     * Returns the submitted by.
     * @return the submitted by
     */
    public User getSubmittedBy() { return submittedBy; }

    /**
     * Sets the submitted by.
     * @param user user for this operation
     */
    public void setSubmittedBy(User user) { submittedBy = user; }

    /**
     * Returns the assigned volunteer.
     * @return the assigned volunteer
     */
    public VolunteerProfile getAssignedVolunteer() {
        return assignedVolunteer;
    }

    /**
     * Sets the assigned volunteer.
     * @param assignedVolunteer assigned volunteer for this operation
     */
    public void setAssignedVolunteer(VolunteerProfile assignedVolunteer) {
        this.assignedVolunteer = assignedVolunteer;
    }


    /**
     * Formats this object for display.
     * @return the result of to string
     */
    @Override 
    public String toString() {
        return "ReliefRequest{" + "\n" +
                "requestId=" + requestId + "\n" +
                "type=" + type + "\n" +
                "description='" + description + "\n" +
                "status=" + status + "\n" +
                "priority=" + priority + "\n" +
                "dateSubmitted=" + dateSubmitted + "\n" +
                "location='" + location + "\n" +
                "numberOfPeopleNeeded=" + numberOfPeopleNeeded + "\n" +
                "numberOfAnimals=" + numberOfAnimals + "\n" +
                "animalNotes='" + animalNotes + "\n" +
                "photoUrl='" + photoUrl + "\n" +
                "isSuspicious=" + isSuspicious + "\n" +
                "isDuplicateOf=" + isDuplicateOf + "\n" +
                "forHurricane=" + forHurricane + "\n" +
                '}';
    }
}


