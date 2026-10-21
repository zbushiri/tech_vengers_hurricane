package model;

/**
 * File paths and JSON key names shared by DataLoader and DataWriter.
 * 
 * @author Tavien Smith
 */
public abstract class DataConstants {
    protected static final String SHELTER_FILE ="json/shelters.json";
    protected static final String USER_FILE = "json/users.json";
    protected static final String REQUEST_FILE = "json/requests.json";
    protected static final String HURRICANE_FILE = "json/hurricanes.json";

    protected static final String USER_ID = "userId";
    protected static final String USER_FIRST_NAME = "firstName";
    protected static final String USER_LAST_NAME = "lastName";
    protected static final String USER_PHONE_NUMBER = "phoneNumber";
    protected static final String USER_EMAIL = "email";
    protected static final String USER_ADDRESS = "address";
    protected static final String USER_USERNAME = "username";
    protected static final String USER_PASSWORD = "passwordHash";
    protected static final String USER_ROLES = "roles";
    protected static final String USER_LAST_KNOWN_LOCATION = "lastKnownLocation";
    protected static final String ASSOCIATED_PERSONS = "associatedPersons";
    protected static final String VERIFICATION_STATUS = "verificationStatus";
    protected static final String SAFETY_STATUS = "safetyStatus";
    protected static final String SAFETY_STATUS_UPDATED_AT = "safetyStatusUpdatedAt";
    
    protected static final String PERSON_FIRST_NAME = "firstName";
    protected static final String PERSON_LAST_NAME = "lastName";
    protected static final String PERSON_AGE = "age";
    protected static final String PERSON_SPECIAL_NEEDS = "specialNeeds";
    protected static final String LATITUDE = "latitude";
    protected static final String LONGITUDE = "longitude";
    
    protected static final String SHELTER_ID = "shelterId";
    protected static final String SHELTER_NAME = "name";
    protected static final String SHELTER_ADDRESS = "address";
    protected static final String SHELTER_CAPACITY = "capacity";
    protected static final String SHELTER_OCCUPANCY = "occupancy";
    protected static final String SHELTER_STATUS = "status";
    protected static final String SHELTER_ACCOMMODATIONS = "accommodations";
    protected static final String SHELTER_LAST_UPDATED = "lastUpdated";
    protected static final String SHELTER_OPERATORS = "shelterOperators";

    protected static final String HURRICANE_NAME = "name";
    protected static final String HURRICANE_START = "startDate";
    protected static final String HURRICANE_END = "endDate";
    protected static final String HURRICANE_STATUS = "status";
    protected static final String HURRICANE_AREAS = "affectedAreas";
}
