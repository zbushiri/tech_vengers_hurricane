package model;

import java.io.FileReader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Loads project data from JSON files.
 *
 * The loader only reads the files and builds objects. It does not add them
 * to UserList, ShelterList or ReliefRequestCatalog; HurricaneReliefApp does
 * that, so loading twice never creates duplicates.
 *
 * Some of the sample JSON uses different key spellings (for example
 * "associated_people" and "id"), numeric ids, and words that are not enum
 * values. Those are all accepted here, and DataWriter saves them back in
 * one consistent format.
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
     * Volunteers are stored in this file too and are loaded as VolunteerProfile.
     *
     * @return the list of users or an empty list if none load
     */
    public static ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();

<<<<<<< HEAD
        for (JSONObject userJSON : readArray(USER_FILE)) {
            try {
                users.add(userFromJSON(userJSON));
            } catch (RuntimeException e) {
                System.out.println("Skipped a user that could not be read: " + e.getMessage());
=======
        if (!users.getUsers().isEmpty()) {
            return users;
        }
        
        try (FileReader reader= new FileReader(USER_FILE)){
            JSONArray usersJSON = (JSONArray) new JSONParser().parse(reader);

            for (int i = 0; i < usersJSON.size(); i++) {
                JSONObject userJSON = (JSONObject) usersJSON.get(i);

                UUID userId = readID(userJSON.get(USER_ID));
                String firstName = (String) userJSON.get(USER_FIRST_NAME);
                String lastName = (String) userJSON.get(USER_LAST_NAME);
                String phoneNumber = (String) userJSON.get(USER_PHONE_NUMBER);
                String email = (String) userJSON.get(USER_EMAIL);
                String address = (String) userJSON.get(USER_ADDRESS);
                String username = (String) userJSON.get(USER_USERNAME);
                String password = (String) userJSON.get(USER_PASSWORD);

                ArrayList<AccountRole> roles = readRoles(userJSON.get(USER_ROLES));
                ArrayList<Person> associatedPersons = readPeople(userJSON.get(ASSOCIATED_PERSONS));

                VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;
                String verificationText = (String) userJSON.get(VERIFICATION_STATUS);
                if (verificationText != null) {
                    verificationStatus = VerificationStatus.valueOf(verificationText.toUpperCase());
                }

                SafetyStatusType safetyStatus = SafetyStatusType.UNKNOWN;
                String safetyText = (String) userJSON.get(SAFETY_STATUS);
                if (safetyText != null) {
                    safetyStatus = SafetyStatusType.valueOf(safetyText.toUpperCase());
                }

                LocalDateTime safetyStatusUpdatedAt =
                        readDateTime((String) userJSON.get(SAFETY_STATUS_UPDATED_AT));
                String lastKnownLocation = readLocation(userJSON.get(USER_LAST_KNOWN_LOCATION));

                User user = new User(userId, firstName, lastName, email, phoneNumber, address, 
                        username, password, roles, associatedPersons, verificationStatus,
                        lastKnownLocation, safetyStatus, safetyStatusUpdatedAt);
                users.addUser(user);
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
            }
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
        HashMap<ReliefRequest, String> duplicateIds = new HashMap<>();

        for (JSONObject requestJSON : readArray(REQUEST_FILE)) {
            try {
                ReliefRequest request = requestFromJSON(requestJSON);
                requests.add(request);
                Object duplicateOf = requestJSON.get("isDuplicateOf");
                if (duplicateOf != null) {
                    duplicateIds.put(request, String.valueOf(duplicateOf));
                }
            } catch (RuntimeException e) {
                System.out.println("Skipped a request that could not be read: " + e.getMessage());
            }
        }

        // isDuplicateOf holds another request's id, so link them once all are loaded.
        for (ReliefRequest request : duplicateIds.keySet()) {
            UUID originalId = toUUID(duplicateIds.get(request), "request");
            for (ReliefRequest original : requests) {
                if (original.getRequestId().equals(originalId)) {
                    request.setIsDuplicateOf(original);
                }
            }
        }
        return requests;
    }

    /**
     * Loads the Hurricane Event from the hurricanes JSON file.
     *
     * @return the list of hurricanes or an empty list if none load.
     */
    public static ArrayList<HurricaneEvent> getHurricanes() {
        ArrayList<HurricaneEvent> hurricanes = new ArrayList<>();

        for (JSONObject hurricaneJSON : readArray(HURRICANE_FILE)) {
            try {
                hurricanes.add(hurricaneFromJSON(hurricaneJSON));
            } catch (RuntimeException e) {
                System.out.println("Skipped a hurricane that could not be read: " + e.getMessage());
            }
        }
        return hurricanes;
    }

        public static void main(String[] args) {
            getUsers();
        System.out.println("Users loaded: " + getUsers().getUsers().size());
        }
}
