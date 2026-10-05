package model;
import java.util.List;

public class VolunteerProfile extends User {
    private List<String> skills;
    private List<String> equipment;
    private BackgroundCheckStatus backgroundCheckStatus;
    private boolean transportationAccess;
    private AvailabilityStatus availabilityStatus;

    public VolunteerProfile(List<String> skills, List<String> equipment, BackgroundCheckStatus backgroundCheckStatus,
                            boolean transportationAccess, AvailabilityStatus availabilityStatus) {
        
        // TODO: Initialize the User part 
        super("", "");
        setSkills(skills);
        setEquipment(equipment);
        setBackgroundCheckStatus(backgroundCheckStatus);
        setTransportationAccess(transportationAccess);
        setAvailabilityStatus(availabilityStatus);
    }

    public List<String> getSkills() {
        return skills;
    }

    public List<String> getEquipment() {
        return equipment;
    }

    public BackgroundCheckStatus getBackgroundCheckStatus() {
        return backgroundCheckStatus;
    }

    public boolean hasTransportationAccess() {
        return transportationAccess;
    }

    public AvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment;
    }

    public void setBackgroundCheckStatus(BackgroundCheckStatus backgroundCheckStatus) {
        this.backgroundCheckStatus = backgroundCheckStatus;
    }

    public void setTransportationAccess(boolean transportationAccess) {
        this.transportationAccess = transportationAccess;
    }

    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }
}
