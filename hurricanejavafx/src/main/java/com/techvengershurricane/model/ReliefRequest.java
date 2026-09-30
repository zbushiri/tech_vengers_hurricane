package com.techvengershurricane.model;

public class ReliefRequest implements Identifiable {
    private String requestId;
    private String type;
    private String status;
    private String location;
    private String description;
    private String priority;
    private String dateSubmitted;
    private int numberOfPeopleNeeded;
    private int numberOfAnimals;
    private String animalNotes;
    private String photoUrl;
    private boolean isSuspicious;
    private String isDuplicateOf;
    private HurricaneEvent forHurricane;
    private Integer assignedVolunteerId;

    public ReliefRequest() {
        /* JSON: required by Gson. */
    }

    public ReliefRequest(String requestId, String type, String description, String location) {
        this.requestId = requestId;
        this.type = type;
        this.description = description;
        this.location = location;
        this.status = "SUBMITTED";
    }

    @Override
    public String getId() { return requestId; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getDateSubmitted() { return dateSubmitted; }
    public void setDateSubmitted(String dateSubmitted) { this.dateSubmitted = dateSubmitted; }
    public int getNumberOfPeopleNeeded() { return numberOfPeopleNeeded; }
    public void setNumberOfPeopleNeeded(int value) { this.numberOfPeopleNeeded = value; }
    public int getNumberOfAnimals() { return numberOfAnimals; }
    public void setNumberOfAnimals(int value) { this.numberOfAnimals = value; }
    public String getAnimalNotes() { return animalNotes; }
    public void setAnimalNotes(String animalNotes) { this.animalNotes = animalNotes; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public boolean isSuspicious() { return isSuspicious; }
    public void setSuspicious(boolean suspicious) { isSuspicious = suspicious; }
    public String getDuplicateOf() { return isDuplicateOf; }
    public void setDuplicateOf(String duplicateOf) { isDuplicateOf = duplicateOf; }
    public HurricaneEvent getForHurricane() { return forHurricane; }
    public void setForHurricane(HurricaneEvent event) { this.forHurricane = event; }
    public Integer getAssignedVolunteerId() { return assignedVolunteerId; }
    public void setAssignedVolunteerId(Integer value) { this.assignedVolunteerId = value; }
}
