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
     * Creates volunteer profile.
     * @param skills skills for this operation
     * @param equipment equipment for this operation
     * @param backgroundCheckStatus background check status for this operation
     * @param transportationAccess transportation access for this operation
     * @param availabilityStatus availability status for this operation
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
     * Creates volunteer profile.
     * @param user user for this operation
     * @param skills skills for this operation
     * @param equipment equipment for this operation
     * @param backgroundCheckStatus background check status for this operation
     * @param transportationAccess transportation access for this operation
     * @param availabilityStatus availability status for this operation
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
     * Returns the volunteer profile.
     * @return the volunteer profile
     */
    @Override
    public VolunteerProfile getVolunteerProfile() { return this; }

    /**
     * Can claim requests.
     * @return whether can claim requests
     */
    public boolean canClaimRequests() {
        return availabilityStatus == AvailabilityStatus.AVAILABLE
                && backgroundCheckStatus == BackgroundCheckStatus.VERIFIED;
    }
    /**
     * Sets the availability.
     * @param value value for this operation
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
     * Has transportation access.
     * @return whether has transportation access
     */
    public boolean hasTransportationAccess() { return transportationAccess; }
    /**
     * Returns the availability status.
     * @return the availability status
     */
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    /**
     * Sets the skills.
     * @param value value for this operation
     */
    public void setSkills(List<String> value) { skills = value; }
    /**
     * Sets the equipment.
     * @param value value for this operation
     */
    public void setEquipment(List<String> value) { equipment = value; }
    /**
     * Sets the background check status.
     * @param value value for this operation
     */
    public void setBackgroundCheckStatus(BackgroundCheckStatus value) { backgroundCheckStatus = value; }
    /**
     * Sets the transportation access.
     * @param value value for this operation
     */
    public void setTransportationAccess(boolean value) { transportationAccess = value; }
    /**
     * Sets the availability status.
     * @param value value for this operation
     */
    public void setAvailabilityStatus(AvailabilityStatus value) { availabilityStatus = value; }
}
