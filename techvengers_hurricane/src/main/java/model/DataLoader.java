package model;

import java.util.ArrayList;
import org.json.simple.JSONArray;

/**
 * Loads project data from JSON files.
 *
 * @author Tavien Smith
 */
public class DataLoader {
    private static final String SHELTER_FILE = "../json/shelters.json";
    private static final String USER_FILE = "../json/users.json";
    private static final String REQUEST_FILE = "../json/requests.json";
    /**
     * Prevents a DataLoader object from being created.
     */
    private DataLoader() {

    }

    /**
     * Loads the shelters from the shelters JSON file.
     * 
     * @return the list of shelters or an empty list if none load
     */
    public static ArrayList<Shelter> getShelters() {

        ArrayList<Shelter> shelters = new ArrayList<>();

        // TODO: Read SHELTER_FILE and add each shelter to the list.
        return shelters;
    }

    /**
     * Loads the users from the users JSON file.
     * Volunteers are stored in this file too.
     * 
     * @return the list of users or an empty list if none load
     */
    public static ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();

        // TODO: Read USER_FILE and add each user to the list.
        return users;
    }

    /**
     * Loads the relief requests from the requests JSON file.
     * 
     * @return the list of requests or an empty list if none load
     */
    public static ArrayList<ReliefRequest> getRequests() {
        ArrayList<ReliefRequest> requests = new ArrayList<>();

        // TODO: Read REQUEST_FILE and add each request to the list. 
        return requests;
    }
}
