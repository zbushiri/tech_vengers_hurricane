package model;

import java.io.File;

/**
 * File paths and JSON key names shared by DataLoader and DataWriter.
 * 
 * @author Tavien Smith
 */
public abstract class DataConstants {
    // The json folder sits at the repo root, one level above pom.xml, so this
    // works whether the app is started from the repo root or from techvengers_hurricane/
    private static final String JSON_FOLDER = new File("json").isDirectory() ? "json/" : "../json/";
    protected static final String SHELTER_FILE = JSON_FOLDER + "shelters.json";
    protected static final String USER_FILE = JSON_FOLDER + "users.json";
    protected static final String REQUEST_FILE = JSON_FOLDER + "requests.json";
    protected static final String HURRICANE_FILE = JSON_FOLDER + "hurricanes.json";

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

    protected static final String VOLUNTEER_SKILLS = "skills";
    protected static final String VOLUNTEER_EQUIPMENT = "equipment";
    protected static final String VOLUNTEER_BACKGROUND_CHECK = "backgroundCheckStatus";
    protected static final String VOLUNTEER_TRANSPORTATION = "transportationStatus";
    // Misspelled key used by the sample data in users.json
    protected static final String VOLUNTEER_TRANSPORTATION_OLD = "transportatioinStatus";
    protected static final String VOLUNTEER_AVAILABILITY = "availabilityStatus";
    
    protected static final String PERSON_FIRST_NAME = "firstName";
    protected static final String PERSON_LAST_NAME = "lastName";
    protected static final String PERSON_AGE = "age";
    protected static final String PERSON_SPECIAL_NEEDS = "specialNeeds";
    protected static final String LATITUDE = "latitude";
    protected static final String LONGITUDE = "longitude";
    
    protected static final String SHELTER_ID = "shelterId";
    protected static final String SHELTER_NAME = "name";
    protected static final String SHELTER_STREET = "street";
    protected static final String SHELTER_CITY = "city";
    protected static final String SHELTER_STATE = "state";
    protected static final String SHELTER_ZIP = "zip";
    protected static final String SHELTER_LATITUDE = "latitude";
    protected static final String SHELTER_LONGITUDE = "longitude";
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

    protected static final String REQUEST_ID = "requestId";
    protected static final String REQUEST_TYPE = "type";
    protected static final String REQUEST_STATUS = "status";
    protected static final String REQUEST_LOCATION = "location";
    protected static final String REQUEST_DESCRIPTION = "description";
    protected static final String REQUEST_PRIORITY = "priority";
    protected static final String REQUEST_DATE_SUBMITTED = "dateSubmitted";
    protected static final String REQUEST_PEOPLE = "numberOfPeopleNeeded";
    protected static final String REQUEST_ANIMALS = "numberOfAnimals";
    protected static final String REQUEST_ANIMAL_NOTES = "animalNotes";
    protected static final String REQUEST_PHOTO_URL = "photoUrl";
    protected static final String REQUEST_IS_SUSPICIOUS = "isSuspicious";
    protected static final String REQUEST_DUPLICATE_OF = "isDuplicateOf";
    protected static final String REQUEST_HURRICANE = "forHurricane";
}
