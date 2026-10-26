package model;

/**
 * The different roles an account can have.
 * @author Team Tech-vengers
 */
public enum AccountRole {
    /**
     * Resident who can request assistance and report safety information.
     */
    RESIDENT,
    /**
     * Volunteer who can claim eligible requests and transport households.
     */
    VOLUNTEER,
    /**
     * Shelter manager who can check in households and complete eligible requests.
     */
    SHELTER_OPERATOR,
    /**
     * Coordinator with oversight of volunteer verification and relief operations.
     */
    COORDINATOR
}
