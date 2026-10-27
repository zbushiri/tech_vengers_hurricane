package model;
import java.util.UUID;
import java.util.Date;

// TODO: Messages!
public class ReliefRequest {
    /** A request for at least this many people moves up one priority level. */
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

    public ReliefRequest(RequestType type, String description, String location, int numberOfPeopleNeeded) {
        this(UUID.randomUUID(), type, description, location, numberOfPeopleNeeded,
             0, "", "", false, null, null);
    }

    /**
     * Copies another request, including its status, priority and date.
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
    }


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

    public void flagAsSuspicious() {
        setSuspicious(true);
    }

    /**
     * Lets a volunteer take this request. It only works when the request has
     * not been claimed yet and the volunteer is available and verified.
     * The volunteer is then marked as on assignment.
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
     */
    public PriorityLevel calculatePriority() {
        PriorityLevel level;
        if (type == RequestType.MEDICAL || type == RequestType.HAZARD) {
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
    public boolean isOpen() {
        return status == RequestStatus.SUBMITTED || status == RequestStatus.ASSIGNED || status == RequestStatus.IN_PROGRESS;
    }
    
    public boolean isForVolunteers() {
        return type == RequestType.FOOD || type == RequestType.TRANSPORTATION ||
        type == RequestType.SUPPLIES || type == RequestType.OTHER;
    }

    public boolean completeRequest(VolunteerProfile volunteer) {
        if (volunteer == null || assignedVolunteer == null) {
            return false;
        }
        if (!assignedVolunteer.getUserId().equals(volunteer.getUserId())) {
            return false;
        }
        if (status != RequestStatus.ASSIGNED &&
            status != RequestStatus.IN_PROGRESS) {
            return false;
        }
        setStatus(RequestStatus.FULFILLED);
        return true;
    }

    /** A duplicate may enter the queue once its original is no longer open. */
    public boolean hasOpenOriginal() {
        return isDuplicateOf != null && isDuplicateOf.isOpen();
    }

    public boolean startRequest(VolunteerProfile volunteer) {
        if (!isAssignedTo(volunteer) || status != RequestStatus.ASSIGNED) return false;
        setStatus(RequestStatus.IN_PROGRESS);
        return true;
    }

    private boolean isAssignedTo(VolunteerProfile volunteer) {
        return volunteer != null && assignedVolunteer != null
                && assignedVolunteer.getUserId().equals(volunteer.getUserId());
    }

    public void updateStatus(RequestStatus status) {
        setStatus(status);
    }

    public HurricaneEvent getForHurricane() {
        return forHurricane;
    }

    public UUID getRequestId() {
        return requestId;
    }
    
    public RequestType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public Date getDateSubmitted() {
        return dateSubmitted;
    }

    public String getLocation() {
        return location;
    }

    public int getNumberOfPeopleNeeded() {
        return numberOfPeopleNeeded;
    }

    public int getNumberOfAnimals() {
        return numberOfAnimals;
    }

    public String getAnimalNotes() {
        return animalNotes;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public boolean isSuspicious() {
        return isSuspicious;
    }

    public ReliefRequest getIsDuplicateOf() {
        return isDuplicateOf;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }
    public void setType(RequestType type) {
        this.type = type;
        priority = calculatePriority();
    }
    public void setDescription(String description) {
        this.description = description;
    }

    /** Enforces transitions during normal use. Loading uses restoreStatus. */
    @SuppressWarnings("deprecation")
    public void setStatus(RequestStatus next) {
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

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public void setDateSubmitted(Date dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNumberOfPeopleNeeded(int numberOfPeopleNeeded) {
        this.numberOfPeopleNeeded = numberOfPeopleNeeded;
        priority = calculatePriority();
    }

    public void setNumberOfAnimals(int numberOfAnimals) {
        this.numberOfAnimals = numberOfAnimals;
    }

    public void setAnimalNotes(String animalNotes) {
        this.animalNotes = animalNotes;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setSuspicious(boolean isSuspicious) {
        this.isSuspicious = isSuspicious;
    }

    public void setIsDuplicateOf(ReliefRequest isDuplicateOf) {
        this.isDuplicateOf = isDuplicateOf;
    }

    public void setForHurricane(HurricaneEvent forHurricane) {
        this.forHurricane = forHurricane;
    }

    public User getSubmittedBy() { return submittedBy; }

    public void setSubmittedBy(User user) { submittedBy = user; }

    public VolunteerProfile getAssignedVolunteer() {
        return assignedVolunteer;
    }

    public void setAssignedVolunteer(VolunteerProfile assignedVolunteer) {
        this.assignedVolunteer = assignedVolunteer;
    }


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


