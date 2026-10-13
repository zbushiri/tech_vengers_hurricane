package model;
import java.util.ArrayList;
import java.util.List;

public class VolunteerProfile extends User {
    private List<String> skills;
    private List<String> equipment;
    private BackgroundCheckStatus backgroundCheckStatus;
    private boolean transportationAccess;
    private AvailabilityStatus availabilityStatus;

    public VolunteerProfile(List<String> skills, List<String> equipment, BackgroundCheckStatus backgroundCheckStatus,
                            boolean transportationAccess, AvailabilityStatus availabilityStatus) {
        
<<<<<<< HEAD
        super("", "");
=======
        // TODO: Initialize the User part 
        super("", "", "", "", "", "", "", new ArrayList<>(), null);
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
        setSkills(skills);
        setEquipment(equipment);
        setBackgroundCheckStatus(backgroundCheckStatus);
        setTransportationAccess(transportationAccess);
        setAvailabilityStatus(availabilityStatus);
        getRoles().add(AccountRole.VOLUNTEER);
    }

    /**
     * Turns an existing account into a volunteer, keeping all of its
     * account details (username, password, name, roles and so on).
     */
    public VolunteerProfile(User user, List<String> skills, List<String> equipment,
                            BackgroundCheckStatus backgroundCheckStatus, boolean transportationAccess,
                            AvailabilityStatus availabilityStatus) {
        super(user.getUserId(), user.getFirstName(), user.getLastName(), user.getPhoneNumber(),
                user.getEmail(), user.getAddress(), user.getUsername(), user.getPasswordHash(),
                user.getRoles(), user.getAssociatedPersons(), user.getVerificationStatus(),
                user.getLastKnownLocation(), user.getSafetyStatus(), user.getSafetyStatusUpdatedAt());
        setSkills(skills);
        setEquipment(equipment);
        setBackgroundCheckStatus(backgroundCheckStatus);
        setTransportationAccess(transportationAccess);
        setAvailabilityStatus(availabilityStatus);
        if (!hasRole(AccountRole.VOLUNTEER)) {
            getRoles().add(AccountRole.VOLUNTEER);
        }
    }

    public void setAvailability(AvailabilityStatus status) {
        setAvailabilityStatus(status);
    }

    /**
     * A volunteer can take a request only when they are available and
     * their background check has passed.
     */
    public boolean canClaimRequests() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE
                && backgroundCheckStatus == BackgroundCheckStatus.VERIFIED;
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
