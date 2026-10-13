package model;

import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
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
    public static ShelterList getShelters() {
        ShelterList shelters = ShelterList.getInstance();

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
    public static UserList getUsers() {
        UserList users = UserList.getInstance();

        if (!users.getUsers().isEmpty()) {
            return users;
        }
        
        try {
            FileReader reader = new FileReader(USER_FILE);
            JSONArray usersJSON = (JSONArray) new JSONParser().parse(reader);

            for (int i = 0; i < usersJSON.size(); i++) {
                JSONObject userJSON = (JSONObject) usersJSON.get(i);
                String firstName = (String) userJSON.get(USER_FIRST_NAME);
                String lastName = (String) userJSON.get(USER_LAST_NAME);
                String email = (String) userJSON.get(USER_EMAIL);
                String phoneNumber = (String) userJSON.get(USER_PHONE_NUMBER);
                String address = (String) userJSON.get(USER_ADDRESS);
                String username = (String) userJSON.get(USER_USERNAME);
                String password = (String) userJSON.get(USER_PASSWORD);

                ArrayList<AccountRole> roles = new ArrayList<>();
                String lastKnownLocation = null;

                User user = new User(firstName, lastName, email, phoneNumber, address, username, password, roles,
                        lastKnownLocation);
                users.addUser(user);
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
    public static ReliefRequestCatalog getRequests() {
        ReliefRequestCatalog requests = ReliefRequestCatalog.getInstance();

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
            getUsers();
        System.out.println("Users loaded: " + getUsers().getUsers().size());
        }
}
