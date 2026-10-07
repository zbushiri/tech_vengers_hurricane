package model;

import java.util.ArrayList;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

/**
 * Loads project data from JSON files.
 *
 * @author Tavien Smith
 */
public class DataLoader extends DataConstants {
    private DataLoader() {
    }

    /**
     * Loads the shelters from the shelters JSON file.
     * 
     * @return the list of shelters or an empty list if none load
     */
    public static ArrayList<Shelter> getShelters() {

        ArrayList<Shelter> shelters = new ArrayList<>();

        try {
            FileReader reader = new FileReader(SHELTER_FILE);
            JSONArray sheltersJSON = (JSONArray) new JSONParser().parse(reader);

            for (int i = 0; i < sheltersJSON.size(); i++) {
                JSONObject shelterJSON = (JSONObject) sheltersJSON.get(i);
                String name = (String) shelterJSON.get("name");

                System.out.println(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        try {
            FileReader reader = new FileReader(USER_FILE);
            JSONArray usersJSON = (JSONArray) new JSONParser().parse(reader);

            for (int i = 0; i < usersJSON.size(); i++) {
                JSONObject userJSON = (JSONObject) usersJSON.get(i);
                String username = (String) userJSON.get(USER_USERNAME);
                String password = (String) userJSON.get(USER_PASSWORD);

                User user = new User(username, password);
                user.setFirstName((String) userJSON.get(USER_FIRST_NAME));
                user.setLastName((String) userJSON.get(USER_LAST_NAME));
                user.setEmail((String) userJSON.get(USER_EMAIL));
                user.setAddress((String) userJSON.get(USER_ADDRESS));
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Loads the Hurricane Event from the hurricanes JSON file.
     * 
     * @return the list of hurricanes or an empty list if none load.
     */
    public static ArrayList<HurricaneEvent> getHurricanes() {
        ArrayList<HurricaneEvent> hurricanes = new ArrayList<>();

        // TODO: Read HURRICANE_FILE and add each hurricane to the list.
        return hurricanes;
    }

    public static void main(String[] args) {
        System.out.println("Users loaded: " + getUsers().size());
    }
}
