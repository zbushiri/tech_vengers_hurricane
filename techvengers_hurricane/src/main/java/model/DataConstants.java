package model;

/**
 * File paths and JSON key names shared by DataLoader and DataWriter.
 * 
 * @author Tavien Smith
 */
public abstract class DataConstants {
    protected static final String SHELTER_FILE = "../json/shelters.json";
    protected static final String USER_FILE = "json/users.json";
    protected static final String REQUEST_FILE = "../json/requests.json";
    protected static final String HURRICANE_FILE = "../json/hurricanes.json";

    protected static final String USER_ID = "userId";
    protected static final String USER_FIRST_NAME = "firstName";
    protected static final String USER_LAST_NAME = "lastName";
    protected static final String USER_PHONE_NUMBER = "phoneNumber";
    protected static final String USER_EMAIL = "email";
    protected static final String USER_ADDRESS = "address";
    protected static final String USER_USERNAME = "username";
    protected static final String USER_PASSWORD = "passwordHash";
<<<<<<< HEAD
    protected static final String USER_PHONE = "phoneNumber";
    protected static final String USER_ROLES = "roles";
    protected static final String USER_ASSOCIATED_PEOPLE = "associatedPeople";
    protected static final String USER_VERIFICATION = "verificationStatus";
    protected static final String USER_LOCATION = "lastKnownLocation";
    protected static final String USER_SAFETY = "safetyStatus";
    protected static final String USER_SAFETY_UPDATED = "safetyStatusUpdatedAt";

    protected static final String VOLUNTEER_SKILLS = "skills";
    protected static final String VOLUNTEER_EQUIPMENT = "equipment";
    protected static final String VOLUNTEER_BACKGROUND = "backgroundCheckStatus";
    protected static final String VOLUNTEER_TRANSPORTATION = "transportationStatus";
    protected static final String VOLUNTEER_AVAILABILITY = "availabilityStatus";
=======
    protected static final String USER_ROLES = "roles";
    protected static final String USER_LAST_KNOWN_LOCATION = "lastKnownLocation";
    protected static final String ASSOCIATED_PERSONS = "associatedPersons";
    protected static final String VERIFICATION_STATUS = "verificationStatus";
    protected static final String SAFETY_STATUS = "safetyStatus";
    protected static final String SAFETY_STATUS_UPDATED_AT = "safetyStatusUpdatedAt";
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
    
}
