package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores the extra information needed for a volunteer.
 * @author Team Tech-vengers
 */
public class VolunteerProfile extends User {
    private List<String> skills;
    private List<String> equipment;
    private BackgroundCheckStatus backgroundCheckStatus;
    private boolean transportationAccess;
    private AvailabilityStatus availabilityStatus;

    /**
     * Creates a volunteer profile with an initially empty username and password.
     * @param skills volunteer's specialties, including request-type names used for matching
     * @param equipment equipment available to the volunteer
     * @param backgroundCheckStatus volunteer's background-check result
     * @param transportationAccess whether the volunteer has access to transportation
     * @param availabilityStatus volunteer's current availability
     */
    public VolunteerProfile(List<String> skills, List<String> equipment,
            BackgroundCheckStatus backgroundCheckStatus, boolean transportationAccess,
            AvailabilityStatus availabilityStatus) {
        super("", "");
        setVolunteerFields(skills, equipment, backgroundCheckStatus,
                transportationAccess, availabilityStatus);
        getRoles().add(AccountRole.VOLUNTEER);
    }

    /**
     * Copies the existing account into a volunteer profile with its specialties and availability.
     * @param user existing account whose identity and profile are copied
     * @param skills volunteer's specialties, including request-type names used for matching
     * @param equipment equipment available to the volunteer
     * @param backgroundCheckStatus volunteer's background-check result
     * @param transportationAccess whether the volunteer has access to transportation
     * @param availabilityStatus volunteer's current availability
     */
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

    /**
     * Returns this account's volunteer profile.
     * @return this volunteer profile
     */
    @Override
    public VolunteerProfile getVolunteerProfile() { return this; }

    /**
     * Checks the availability and background-check conditions for claiming work.
     * @return true if the volunteer is AVAILABLE and the background check is VERIFIED
     */
    public boolean canClaimRequests() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE
                && backgroundCheckStatus == BackgroundCheckStatus.VERIFIED;
    }
    /**
     * Sets the availability.
     * @param value replacement availability for volunteer assignments
     */
    public void setAvailability(AvailabilityStatus value) { availabilityStatus = value; }
    /**
     * Returns the skills.
     * @return the skills
     */
    public List<String> getSkills() { return skills; }
    /**
     * Returns the equipment.
     * @return the equipment
     */
    public List<String> getEquipment() { return equipment; }
    /**
     * Returns the background check status.
     * @return the background check status
     */
    public BackgroundCheckStatus getBackgroundCheckStatus() { return backgroundCheckStatus; }
    /**
     * Checks whether the volunteer has access to transportation.
     * @return true if transportation access is recorded
     */
    public boolean hasTransportationAccess() { return transportationAccess; }
    /**
     * Returns the availability status.
     * @return the availability status
     */
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    /**
     * Sets the skills.
     * @param value replacement volunteer's specialties
     */
    public void setSkills(List<String> value) { skills = value; }
    /**
     * Sets the equipment.
     * @param value replacement equipment available to the volunteer
     */
    public void setEquipment(List<String> value) { equipment = value; }
    /**
     * Sets the background check status.
     * @param value replacement background-check result
     */
    public void setBackgroundCheckStatus(BackgroundCheckStatus value) { backgroundCheckStatus = value; }
    /**
     * Sets the transportation access.
     * @param value replacement whether transportation is available to the volunteer
     */
    public void setTransportationAccess(boolean value) { transportationAccess = value; }
    /**
     * Sets the availability status.
     * @param value replacement availability for volunteer assignments
     */
    public void setAvailabilityStatus(AvailabilityStatus value) { availabilityStatus = value; }
}
