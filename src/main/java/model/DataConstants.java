package model;

import java.io.File;

/**
 * File paths and JSON key names shared by DataLoader and DataWriter.
 * 
 * @author Tavien Smith
 */
public abstract class DataConstants {
    /** Creates a DataConstants instance. */
    protected DataConstants() { }

    // Launch from the repository root; scenario and tests supply an isolated data directory.
    private static final String JSON_FOLDER = System.getProperty("hurricane.dataDir",
            "json") + File.separator;
    /**
     * Path to the saved shelter JSON file.
     */
    protected static final String SHELTER_FILE = JSON_FOLDER + "shelters.json";
    /**
     * Path to the saved user JSON file.
     */
    protected static final String USER_FILE = JSON_FOLDER + "users.json";
    /**
     * Path to the saved request JSON file.
     */
    protected static final String REQUEST_FILE = JSON_FOLDER + "requests.json";
    /**
     * Path to the saved hurricane JSON file.
     */
    protected static final String HURRICANE_FILE = JSON_FOLDER + "hurricanes.json";

    /**
     * JSON field name for {@code userId}.
     */
    protected static final String USER_ID = "userId";
    /**
     * JSON field name for {@code firstName}.
     */
    protected static final String USER_FIRST_NAME = "firstName";
    /**
     * JSON field name for {@code lastName}.
     */
    protected static final String USER_LAST_NAME = "lastName";
    /**
     * JSON field name for {@code phoneNumber}.
     */
    protected static final String USER_PHONE_NUMBER = "phoneNumber";
    /**
     * JSON field name for {@code email}.
     */
    protected static final String USER_EMAIL = "email";
    /**
     * JSON field name for {@code address}.
     */
    protected static final String USER_ADDRESS = "address";
    /**
     * JSON field name for {@code username}.
     */
    protected static final String USER_USERNAME = "username";
    /**
     * JSON field name for {@code passwordHash}.
     */
    protected static final String USER_PASSWORD = "passwordHash";
    /**
     * JSON field name for {@code roles}.
     */
    protected static final String USER_ROLES = "roles";
    /**
     * JSON field name for {@code lastKnownLocation}.
     */
    protected static final String USER_LAST_KNOWN_LOCATION = "lastKnownLocation";
    /**
     * JSON field name for {@code associatedPersons}.
     */
    protected static final String ASSOCIATED_PERSONS = "associatedPersons";
    /**
     * JSON field name for {@code verificationStatus}.
     */
    protected static final String VERIFICATION_STATUS = "verificationStatus";
    /**
     * JSON field name for {@code safetyStatus}.
     */
    protected static final String SAFETY_STATUS = "safetyStatus";
    /**
     * JSON field name for {@code safetyStatusUpdatedAt}.
     */
    protected static final String SAFETY_STATUS_UPDATED_AT = "safetyStatusUpdatedAt";

    /**
     * JSON field name for {@code skills}.
     */
    protected static final String VOLUNTEER_SKILLS = "skills";
    /**
     * JSON field name for {@code equipment}.
     */
    protected static final String VOLUNTEER_EQUIPMENT = "equipment";
    /**
     * JSON field name for {@code backgroundCheckStatus}.
     */
    protected static final String VOLUNTEER_BACKGROUND_CHECK = "backgroundCheckStatus";
    /**
     * JSON field name for {@code transportationStatus}.
     */
    protected static final String VOLUNTEER_TRANSPORTATION = "transportationStatus";
    // Misspelled key used by the sample data in users.json
    /**
     * JSON field name for {@code transportatioinStatus}.
     */
    protected static final String VOLUNTEER_TRANSPORTATION_OLD = "transportatioinStatus";
    /**
     * JSON field name for {@code availabilityStatus}.
     */
    protected static final String VOLUNTEER_AVAILABILITY = "availabilityStatus";
    
    /**
     * JSON field name for {@code firstName}.
     */
    protected static final String PERSON_FIRST_NAME = "firstName";
    /**
     * JSON field name for {@code lastName}.
     */
    protected static final String PERSON_LAST_NAME = "lastName";
    /**
     * JSON field name for {@code age}.
     */
    protected static final String PERSON_AGE = "age";
    /**
     * JSON field name for {@code specialNeeds}.
     */
    protected static final String PERSON_SPECIAL_NEEDS = "specialNeeds";
    /**
     * JSON field name for {@code latitude}.
     */
    protected static final String LATITUDE = "latitude";
    /**
     * JSON field name for {@code longitude}.
     */
    protected static final String LONGITUDE = "longitude";
    
    /**
     * JSON field name for {@code shelterId}.
     */
    protected static final String SHELTER_ID = "shelterId";
    /**
     * JSON field name for {@code name}.
     */
    protected static final String SHELTER_NAME = "name";
    /**
     * JSON field name for {@code address}.
     */
    protected static final String SHELTER_ADDRESS = "address";
    /**
     * JSON field name for {@code capacity}.
     */
    protected static final String SHELTER_CAPACITY = "capacity";
    /**
     * JSON field name for {@code occupancy}.
     */
    protected static final String SHELTER_OCCUPANCY = "occupancy";
    /**
     * JSON field name for {@code status}.
     */
    protected static final String SHELTER_STATUS = "status";
    /**
     * JSON field name for {@code accommodations}.
     */
    protected static final String SHELTER_ACCOMMODATIONS = "accommodations";
    /**
     * JSON field name for {@code lastUpdated}.
     */
    protected static final String SHELTER_LAST_UPDATED = "lastUpdated";
    /**
     * JSON field name for {@code shelterOperators}.
     */
    protected static final String SHELTER_OPERATORS = "shelterOperators";

    /**
     * JSON field name for {@code name}.
     */
    protected static final String HURRICANE_NAME = "name";
    /**
     * JSON field name for {@code startDate}.
     */
    protected static final String HURRICANE_START = "startDate";
    /**
     * JSON field name for {@code endDate}.
     */
    protected static final String HURRICANE_END = "endDate";
    /**
     * JSON field name for {@code status}.
     */
    protected static final String HURRICANE_STATUS = "status";
    /**
     * JSON field name for {@code affectedAreas}.
     */
    protected static final String HURRICANE_AREAS = "affectedAreas";

    /**
     * JSON field name for {@code requestId}.
     */
    protected static final String REQUEST_ID = "requestId";
    /**
     * JSON field name for {@code type}.
     */
    protected static final String REQUEST_TYPE = "type";
    /**
     * JSON field name for {@code status}.
     */
    protected static final String REQUEST_STATUS = "status";
    /**
     * JSON field name for {@code location}.
     */
    protected static final String REQUEST_LOCATION = "location";
    /**
     * JSON field name for {@code description}.
     */
    protected static final String REQUEST_DESCRIPTION = "description";
    /**
     * JSON field name for {@code priority}.
     */
    protected static final String REQUEST_PRIORITY = "priority";
    /**
     * JSON field name for {@code dateSubmitted}.
     */
    protected static final String REQUEST_DATE_SUBMITTED = "dateSubmitted";
    /**
     * JSON field name for {@code numberOfPeopleNeeded}.
     */
    protected static final String REQUEST_PEOPLE = "numberOfPeopleNeeded";
    /**
     * JSON field name for {@code numberOfAnimals}.
     */
    protected static final String REQUEST_ANIMALS = "numberOfAnimals";
    /**
     * JSON field name for {@code animalNotes}.
     */
    protected static final String REQUEST_ANIMAL_NOTES = "animalNotes";
    /**
     * JSON field name for {@code photoUrl}.
     */
    protected static final String REQUEST_PHOTO_URL = "photoUrl";
    /**
     * JSON field name for {@code isSuspicious}.
     */
    protected static final String REQUEST_IS_SUSPICIOUS = "isSuspicious";
    /**
     * JSON field name for {@code isDuplicateOf}.
     */
    protected static final String REQUEST_DUPLICATE_OF = "isDuplicateOf";
    /**
     * JSON field name for {@code forHurricane}.
     */
    protected static final String REQUEST_HURRICANE = "forHurricane";
    /**
     * JSON field name for {@code submittedBy}.
     */
    protected static final String REQUEST_SUBMITTED_BY = "submittedBy";
    /**
     * JSON field name for {@code assignedVolunteer}.
     */
    protected static final String REQUEST_ASSIGNED_VOLUNTEER = "assignedVolunteer";
}
