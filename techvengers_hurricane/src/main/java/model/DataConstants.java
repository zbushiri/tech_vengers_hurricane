package model;

/**
 * File paths and JSON key names shared by DataLoader and DataWriter.
 * 
 * @author Tavien Smith
 */
public abstract class DataConstants {
    protected static final String SHELTER_FILE = "../json/shelters.json";
    protected static final String USER_FILE = "../json/users.json";
    protected static final String REQUEST_FILE = "../json/requests.json";
    protected static final String HURRICANE_FILE = "../json/hurricanes.json";

    protected static final String USER_ID = "userId";
    protected static final String USER_FIRST_NAME = "firstName";
    protected static final String USER_LAST_NAME = "lastName";
    protected static final String USER_EMAIL = "email";
    protected static final String USER_ADDRESS = "address";
    protected static final String USER_USERNAME = "username";
    protected static final String USER_PASSWORD = "passwordHash";
    
}
