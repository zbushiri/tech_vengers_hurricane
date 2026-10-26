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
     * shelter file.
     */
    protected static final String SHELTER_FILE = JSON_FOLDER + "shelters.json";
    /**
     * user file.
     */
    protected static final String USER_FILE = JSON_FOLDER + "users.json";
    /**
     * request file.
     */
    protected static final String REQUEST_FILE = JSON_FOLDER + "requests.json";
    /**
     * hurricane file.
     */
    protected static final String HURRICANE_FILE = JSON_FOLDER + "hurricanes.json";

    /**
     * user id.
     */
    protected static final String USER_ID = "userId";
    /**
     * user first name.
     */
    protected static final String USER_FIRST_NAME = "firstName";
    /**
     * user last name.
     */
    protected static final String USER_LAST_NAME = "lastName";
    /**
     * user phone number.
     */
    protected static final String USER_PHONE_NUMBER = "phoneNumber";
    /**
     * user email.
     */
    protected static final String USER_EMAIL = "email";
    /**
     * user address.
     */
    protected static final String USER_ADDRESS = "address";
    /**
     * user username.
     */
    protected static final String USER_USERNAME = "username";
    /**
     * user password.
     */
    protected static final String USER_PASSWORD = "passwordHash";
    /**
     * user roles.
     */
    protected static final String USER_ROLES = "roles";
    /**
     * user last known location.
     */
    protected static final String USER_LAST_KNOWN_LOCATION = "lastKnownLocation";
    /**
     * associated persons.
     */
    protected static final String ASSOCIATED_PERSONS = "associatedPersons";
    /**
     * verification status.
     */
    protected static final String VERIFICATION_STATUS = "verificationStatus";
    /**
     * safety status.
     */
    protected static final String SAFETY_STATUS = "safetyStatus";
    /**
     * safety status updated at.
     */
    protected static final String SAFETY_STATUS_UPDATED_AT = "safetyStatusUpdatedAt";

    /**
     * volunteer skills.
     */
    protected static final String VOLUNTEER_SKILLS = "skills";
    /**
     * volunteer equipment.
     */
    protected static final String VOLUNTEER_EQUIPMENT = "equipment";
    /**
     * volunteer background check.
     */
    protected static final String VOLUNTEER_BACKGROUND_CHECK = "backgroundCheckStatus";
    /**
     * volunteer transportation.
     */
    protected static final String VOLUNTEER_TRANSPORTATION = "transportationStatus";
    // Misspelled key used by the sample data in users.json
    /**
     * volunteer transportation old.
     */
    protected static final String VOLUNTEER_TRANSPORTATION_OLD = "transportatioinStatus";
    /**
     * volunteer availability.
     */
    protected static final String VOLUNTEER_AVAILABILITY = "availabilityStatus";
    
    /**
     * person first name.
     */
    protected static final String PERSON_FIRST_NAME = "firstName";
    /**
     * person last name.
     */
    protected static final String PERSON_LAST_NAME = "lastName";
    /**
     * person age.
     */
    protected static final String PERSON_AGE = "age";
    /**
     * person special needs.
     */
    protected static final String PERSON_SPECIAL_NEEDS = "specialNeeds";
    /**
     * latitude.
     */
    protected static final String LATITUDE = "latitude";
    /**
     * longitude.
     */
    protected static final String LONGITUDE = "longitude";
    
    /**
     * shelter id.
     */
    protected static final String SHELTER_ID = "shelterId";
    /**
     * shelter name.
     */
    protected static final String SHELTER_NAME = "name";
    /**
     * shelter address.
     */
    protected static final String SHELTER_ADDRESS = "address";
    /**
     * shelter capacity.
     */
    protected static final String SHELTER_CAPACITY = "capacity";
    /**
     * shelter occupancy.
     */
    protected static final String SHELTER_OCCUPANCY = "occupancy";
    /**
     * shelter status.
     */
    protected static final String SHELTER_STATUS = "status";
    /**
     * shelter accommodations.
     */
    protected static final String SHELTER_ACCOMMODATIONS = "accommodations";
    /**
     * shelter last updated.
     */
    protected static final String SHELTER_LAST_UPDATED = "lastUpdated";
    /**
     * shelter operators.
     */
    protected static final String SHELTER_OPERATORS = "shelterOperators";

    /**
     * hurricane name.
     */
    protected static final String HURRICANE_NAME = "name";
    /**
     * hurricane start.
     */
    protected static final String HURRICANE_START = "startDate";
    /**
     * hurricane end.
     */
    protected static final String HURRICANE_END = "endDate";
    /**
     * hurricane status.
     */
    protected static final String HURRICANE_STATUS = "status";
    /**
     * hurricane areas.
     */
    protected static final String HURRICANE_AREAS = "affectedAreas";

    /**
     * request id.
     */
    protected static final String REQUEST_ID = "requestId";
    /**
     * request type.
     */
    protected static final String REQUEST_TYPE = "type";
    /**
     * request status.
     */
    protected static final String REQUEST_STATUS = "status";
    /**
     * request location.
     */
    protected static final String REQUEST_LOCATION = "location";
    /**
     * request description.
     */
    protected static final String REQUEST_DESCRIPTION = "description";
    /**
     * request priority.
     */
    protected static final String REQUEST_PRIORITY = "priority";
    /**
     * request date submitted.
     */
    protected static final String REQUEST_DATE_SUBMITTED = "dateSubmitted";
    /**
     * request people.
     */
    protected static final String REQUEST_PEOPLE = "numberOfPeopleNeeded";
    /**
     * request animals.
     */
    protected static final String REQUEST_ANIMALS = "numberOfAnimals";
    /**
     * request animal notes.
     */
    protected static final String REQUEST_ANIMAL_NOTES = "animalNotes";
    /**
     * request photo url.
     */
    protected static final String REQUEST_PHOTO_URL = "photoUrl";
    /**
     * request is suspicious.
     */
    protected static final String REQUEST_IS_SUSPICIOUS = "isSuspicious";
    /**
     * request duplicate of.
     */
    protected static final String REQUEST_DUPLICATE_OF = "isDuplicateOf";
    /**
     * request hurricane.
     */
    protected static final String REQUEST_HURRICANE = "forHurricane";
    /**
     * request submitted by.
     */
    protected static final String REQUEST_SUBMITTED_BY = "submittedBy";
    /**
     * request assigned volunteer.
     */
    protected static final String REQUEST_ASSIGNED_VOLUNTEER = "assignedVolunteer";
}
