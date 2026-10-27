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
     * Creates a submitted relief request and calculates its initial priority.
     * @param type kind of relief assistance requested
     * @param description description of the household situation and requested help
     * @param location full street address where help is needed
     * @param numberOfPeopleNeeded number of people needing assistance
     */
    public ReliefRequest(RequestType type, String description, String location, int numberOfPeopleNeeded) {
        this(UUID.randomUUID(), type, description, location, numberOfPeopleNeeded,
             0, "", "", false, null, null);
    }

    /**
     * Copies another request, including its status, priority and date.
     * @param request existing request whose fields and workflow state are copied
     */
    public ReliefRequest(ReliefRequest request) {
        this(request.getRequestId(), request.getType(), request.getDescription(), request.getLocation(),
             request.getNumberOfPeopleNeeded(), request.getNumberOfAnimals(), request.getAnimalNotes(),
             request.getPhotoUrl(), request.isSuspicious(), request.getIsDuplicateOf(), request.getForHurricane());
        restoreStatus(request.getStatus());
        setDateSubmitted(request.getDateSubmitted());
        setAssignedVolunteer(request.getAssignedVolunteer());
        setSubmittedBy(request.getSubmittedBy());
        setFamilyMembers(request.getFamilyMembers());
        setPets(request.getPets());
        setMedicalNeeds(request.getMedicalNeeds());
        setDestinationShelterId(request.getDestinationShelterId());
        // setMedicalNeeds recalculates priority, so the copied priority is restored last.
        setPriority(request.getPriority());
    }


    /**
     * Creates a submitted relief request and calculates its initial priority.
     * @param requestId unique identifier of the relief request
     * @param type kind of relief assistance requested
     * @param description description of the household situation and requested help
     * @param location full street address where help is needed
     * @param numberOfPeopleNeeded number of people needing assistance
     * @param numberOfAnimals number of accompanying animals
     * @param animalNotes description of accompanying animals
     * @param photoUrl address of an optional supporting photo
     * @param isSuspicious whether the request has been flagged for review
     * @param isDuplicateOf original request, or null when no duplicate is known
     * @param forHurricane associated hurricane event, or null when none is selected
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
     * Appends a nonblank detail to the description on a new line.
     * @param detail nonblank text to append to the request description
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
     * Flags the request for review without changing its workflow status.
     */
    public void flagAsSuspicious() {
        setSuspicious(true);
    }

    /**
     * Lets a volunteer take this request. It only works when the request has
     * not been claimed yet and the volunteer is available and verified.
     * The volunteer is then marked as on assignment.
     * @param volunteer volunteer attempting to claim or start the assignment
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
     * Medical needs, medical requests and hazards start at HIGH; shelter and food start at MEDIUM.
     * Other requests start at LOW. Large groups move up one level.
     * @return calculated urgency, raised for medical needs and groups of at least LARGE_GROUP_SIZE
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
     * Checks whether the request is submitted, assigned or in progress.
     * @return true if the request has not been fulfilled or cancelled
     */
    public boolean isOpen() {
        return status == RequestStatus.SUBMITTED || status == RequestStatus.ASSIGNED || status == RequestStatus.IN_PROGRESS;
    }
    
    /**
     * Checks whether this request type is available for volunteer assignments.
     * @return true for food, transportation, supplies or other assistance
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
     * @return true if this request duplicates an original request that is still open
     */
    public boolean hasOpenOriginal() {
        return isDuplicateOf != null && isDuplicateOf.isOpen();
    }

    /**
     * Changes an assigned request to IN_PROGRESS for its assigned volunteer.
     * @param volunteer volunteer attempting to claim or start the assignment
     * @return true if the assigned volunteer started the request; false otherwise
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

    /**
     * Requests a workflow transition using the same rules as normal status changes.
     * @param status requested workflow state
     */
    void updateStatus(RequestStatus status) {
        setStatus(status);
    }

    /**
     * Returns the hurricane associated with this request.
     * @return associated hurricane, or null when none is linked
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
     * Checks whether the request has been flagged for review.
     * @return true if the suspicious flag is set
     */
    public boolean isSuspicious() {
        return isSuspicious;
    }

    /**
     * Returns the original request when this request is a duplicate.
     * @return original request, or null when no duplicate is linked
     */
    public ReliefRequest getIsDuplicateOf() {
        return isDuplicateOf;
    }

    /**
     * Sets the request id.
     * @param requestId unique identifier of the relief request
     */
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }
    /**
     * Sets the type.
     * @param type kind of relief assistance requested
     */
    public void setType(RequestType type) {
        this.type = type;
        priority = calculatePriority();
    }
    /**
     * Sets the description.
     * @param description description of the household situation and requested help
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Applies a permitted workflow transition and releases the volunteer when work ends.
     * Loading uses {@link #restoreStatus(RequestStatus)} instead.
     * @param next requested workflow state; invalid transitions are ignored
     */
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

    /**
     * Restores persisted state without replaying workflow side effects.
     * @param saved stored workflow state; legacy COMPLETE is normalized to FULFILLED
     */
    @SuppressWarnings("deprecation")
    void restoreStatus(RequestStatus saved) {
        status = saved == RequestStatus.COMPLETE ? RequestStatus.FULFILLED : saved;
    }

    /**
     * Sets the priority.
     * @param priority urgency level to store
     */
    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    /**
     * Sets the date submitted.
     * @param dateSubmitted time when the request was submitted
     */
    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    /**
     * Sets the location.
     * @param location full street address where help is needed
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Sets the number of people needed.
     * @param numberOfPeopleNeeded number of people needing assistance
     */
    public void setNumberOfPeopleNeeded(int numberOfPeopleNeeded) {
        this.numberOfPeopleNeeded = numberOfPeopleNeeded;
        priority = calculatePriority();
    }

    /**
     * Sets the number of animals.
     * @param numberOfAnimals number of accompanying animals
     */
    public void setNumberOfAnimals(int numberOfAnimals) {
        this.numberOfAnimals = numberOfAnimals;
    }

    /**
     * Sets the animal notes.
     * @param animalNotes description of accompanying animals
     */
    public void setAnimalNotes(String animalNotes) {
        this.animalNotes = animalNotes;
    }

    /**
     * Sets the photo url.
     * @param photoUrl address of an optional supporting photo
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Updates the request's review flag.
     * @param isSuspicious whether the request has been flagged for review
     */
    public void setSuspicious(boolean isSuspicious) {
        this.isSuspicious = isSuspicious;
    }

    /**
     * Links this request to its original when a duplicate is identified.
     * @param isDuplicateOf original request, or null when no duplicate is known
     */
    public void setIsDuplicateOf(ReliefRequest isDuplicateOf) {
        this.isDuplicateOf = isDuplicateOf;
    }

    /**
     * Links this request to a hurricane event.
     * @param forHurricane associated hurricane event, or null when none is selected
     */
    public void setForHurricane(HurricaneEvent forHurricane) {
        this.forHurricane = forHurricane;
    }

    /**
     * Returns the account that submitted the request.
     * @return submitting account, or null for legacy requests without an owner
     */
    public User getSubmittedBy() { return submittedBy; }

    /**
     * Records which account submitted the request.
     * @param user account that submitted this request
     */
    public void setSubmittedBy(User user) { submittedBy = user; }

    /**
     * Returns the assigned volunteer.
     * @return assigned volunteer profile, or null before assignment
     */
    public VolunteerProfile getAssignedVolunteer() {
        return assignedVolunteer;
    }

    /**
     * Sets the assigned volunteer.
     * @param assignedVolunteer assigned volunteer profile, or null when unassigned
     */
    public void setAssignedVolunteer(VolunteerProfile assignedVolunteer) {
        this.assignedVolunteer = assignedVolunteer;
    }


    /**
     * Formats this object for display.
     * @return request details, workflow status, priority and linked hurricane
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


