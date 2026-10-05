package model;
import java.util.UUID;
import java.util.Date;

// TODO: Messages!
public class ReliefRequest {
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

    public ReliefRequest(ReliefRequest request) {
        this(request.getRequestId(), request.getType(), request.getDescription(), request.getLocation(),
             request.getNumberOfPeopleNeeded(), request.getNumberOfAnimals(), request.getAnimalNotes(),
             request.getPhotoUrl(), request.isSuspicious(), request.getIsDuplicateOf(), request.getForHurricane());
    }


    public ReliefRequest(UUID requestId, RequestType type, String description, String location, int numberOfPeopleNeeded,
        int numberOfAnimals, String animalNotes, String photoUrl, boolean isSuspicious, ReliefRequest isDuplicateOf, HurricaneEvent forHurricane) {
        setRequestId(requestId);
        setType(type);
        setDescription(description);
        setStatus(RequestStatus.SUBMITTED);
        setPriority(calculatePriority());
        setDateSubmitted(new Date());
        setLocation(location);
        setNumberOfPeopleNeeded(numberOfPeopleNeeded);
        setNumberOfAnimals(numberOfAnimals);
        setAnimalNotes(animalNotes);
        setPhotoUrl(photoUrl);
        setSuspicious(isSuspicious);
        setIsDuplicateOf(isDuplicateOf);
        setForHurricane(forHurricane);
    }

    public void cancel() {
        setStatus(RequestStatus.CANCELLED);
        // TODO: Additional logic for canceling the request can be added here
    }

    public void addDetail(String detail) {
        setDescription(getDescription() + "\n" + detail);
    }

    public void flagAsSuspicious() {
        setSuspicious(true);
    }

    public void claimRequest(VolunteerProfile volunteer) {
        setStatus(RequestStatus.IN_PROGRESS);
        // TODO: Additional logic for claiming the request by a volunteer can be added here
    }

    public PriorityLevel calculatePriority() {
        // TODO: Implement the priority calculation logic here
        return PriorityLevel.LOW;
    }
    public boolean isOpen() {
        return status == RequestStatus.SUBMITTED || status == RequestStatus.IN_PROGRESS;
    }
    
    public boolean isForVolunteers() {
        return type == RequestType.FOOD || type == RequestType.TRANSPORTATION ||
        type == RequestType.SUPPLIES || type == RequestType.OTHER;
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
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
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


