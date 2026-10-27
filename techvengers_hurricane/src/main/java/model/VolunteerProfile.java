package model;

import java.util.ArrayList;
import java.util.List;

/** Stores the extra information needed for a volunteer. */
public class VolunteerProfile extends User {
    private List<String> skills;
    private List<String> equipment;
    private BackgroundCheckStatus backgroundCheckStatus;
    private boolean transportationAccess;
    private AvailabilityStatus availabilityStatus;

    public VolunteerProfile(List<String> skills, List<String> equipment,
            BackgroundCheckStatus backgroundCheckStatus, boolean transportationAccess,
            AvailabilityStatus availabilityStatus) {
        super("", "");
        setVolunteerFields(skills, equipment, backgroundCheckStatus,
                transportationAccess, availabilityStatus);
        getRoles().add(AccountRole.VOLUNTEER);
    }

    public VolunteerProfile(User user, List<String> skills, List<String> equipment,
            BackgroundCheckStatus backgroundCheckStatus, boolean transportationAccess,
            AvailabilityStatus availabilityStatus) {
        super(user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getPhoneNumber(), user.getAddress(), user.getUsername(), user.getPasswordHash(),
                user.getRoles(), user.getAssociatedPersons(), user.getVerificationStatus(),
                user.getLastKnownLocation(), user.getSafetyStatus(), user.getSafetyStatusUpdatedAt());
        setVolunteerFields(skills, equipment, backgroundCheckStatus,
                transportationAccess, availabilityStatus);
        if (!hasRole(AccountRole.VOLUNTEER)) getRoles().add(AccountRole.VOLUNTEER);
    }

    private void setVolunteerFields(List<String> skills, List<String> equipment,
            BackgroundCheckStatus background, boolean transportation,
            AvailabilityStatus availability) {
        this.skills = skills == null ? new ArrayList<>() : skills;
        this.equipment = equipment == null ? new ArrayList<>() : equipment;
        backgroundCheckStatus = background;
        transportationAccess = transportation;
        availabilityStatus = availability;
    }

    @Override
    public VolunteerProfile getVolunteerProfile() { return this; }

    public boolean canClaimRequests() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE
                && backgroundCheckStatus == BackgroundCheckStatus.VERIFIED;
    }
    public void setAvailability(AvailabilityStatus value) { availabilityStatus = value; }
    public List<String> getSkills() { return skills; }
    public List<String> getEquipment() { return equipment; }
    public BackgroundCheckStatus getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public boolean hasTransportationAccess() { return transportationAccess; }
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setSkills(List<String> value) { skills = value; }
    public void setEquipment(List<String> value) { equipment = value; }
    public void setBackgroundCheckStatus(BackgroundCheckStatus value) { backgroundCheckStatus = value; }
    public void setTransportationAccess(boolean value) { transportationAccess = value; }
    public void setAvailabilityStatus(AvailabilityStatus value) { availabilityStatus = value; }
}
