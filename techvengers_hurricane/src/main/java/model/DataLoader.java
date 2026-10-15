package model;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

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

        if (!shelters.getShelters().isEmpty()) {
            return shelters;
        }

        UserList users = getUsers();

        try(FileReader reader = new FileReader(SHELTER_FILE)) {
            JSONArray sheltersJSON = (JSONArray) new JSONParser().parse(reader);

            for (int i = 0; i < sheltersJSON.size(); i++) {
                JSONObject shelterJSON = (JSONObject) sheltersJSON.get(i);
                
                UUID shelterId = readID(shelterJSON.get(SHELTER_ID));
                String name = (String) shelterJSON.get(SHELTER_NAME);
                String address = (String) shelterJSON.get(SHELTER_ADDRESS);
                int capacity = ((Long) shelterJSON.get(SHELTER_CAPACITY)).intValue();
                int occupancy = ((Long) shelterJSON.get(SHELTER_OCCUPANCY)).intValue();

                ShelterStatus status = ShelterStatus.OPEN;
                String statusText = (String) shelterJSON.get(SHELTER_STATUS);
                if (statusText != null) {
                    status = ShelterStatus.valueOf(statusText.toUpperCase());
                }

                ArrayList<Accommodation> accommodations = readAccommodations (shelterJSON.get(SHELTER_ACCOMMODATIONS));

                Shelter shelter = new Shelter(shelterId, name, address, capacity,
                        occupancy, status, accommodations);
                shelter.setLastUpdated(readDateTime((String) shelterJSON.get(SHELTER_LAST_UPDATED)));
                shelter.setShelterOperators(readOperators(shelterJSON.get(SHELTER_OPERATORS), users));
                shelters.addShelter(shelter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shelters;
    }

    /**
     * Turns a JSON array of names like"pet_friendly" into Accommodations.
     * Names that are not an Accommodation are skipped.
    */
   private static ArrayList<Accommodation> readAccommodations(Object accommodationsValue) {
       ArrayList<Accommodation> accommodations = new ArrayList<>();
       if (!(accommodationsValue instanceof JSONArray)) {
        return accommodations;
       }

       for (Object accommodation : (JSONArray) accommodationsValue) {
           try {
               accommodations.add(Accommodation.valueOf(((String) accommodation).toUpperCase()));
           } catch (IllegalArgumentException e) {
               // not an Accommodation, so skip it
           }
       }
       return accommodations;
   }

   /**
    * Turns a JSON array of usernames into the matching Users.
    * Usernames that match no user are skipped.
   */
  private static ArrayList<User> readOperators(Object operatorsValue, UserList users) {
      ArrayList<User> operators = new ArrayList<>();
      if (!(operatorsValue instanceof JSONArray)) {
          return operators;
      }
      for (Object username : (JSONArray) operatorsValue) {
          for (User user : users.getUsers()) {
              if (user.getUsername().equals(username)) {
                  operators.add(user);
              }
          }
      }
      return operators;
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
    
    /**
     * Turns a saved id into a UUID.
     * Old data uses numbers for ids, so those get a new UUID.
     */
    private static UUID readID(Object idValue) {
        if (idValue instanceof String) {
            return UUID.fromString((String) idValue);
        }
        return UUID.randomUUID();
    }

    /**
     * Turns a JSON array of role names like "resident" into AccountRoles.
     * Names that are not a role (like "user") are skipped.
     */
    private static ArrayList<AccountRole> readRoles(Object rolesValue) {
        ArrayList<AccountRole> roles = new ArrayList<>();
        if (!(rolesValue instanceof JSONArray)) {
            return roles;
        }

        for (Object role : (JSONArray) rolesValue) {
            try {
                roles.add(AccountRole.valueOf(((String) role).toUpperCase()));
            } catch (IllegalArgumentException e) {
                // not a role in AccountRole, so skip it
            }
        }
        return roles;
    }

    /**
     * Turns a JSON array of people into Person objects.
     */
    private static ArrayList<Person> readPeople(Object peopleValue) {
        ArrayList<Person> people = new ArrayList<>();
        if (!(peopleValue instanceof JSONArray)) {
            return people;
        }

        for (Object item : (JSONArray) peopleValue) {
            JSONObject personJSON = (JSONObject) item;
            String firstName = (String) personJSON.get(PERSON_FIRST_NAME);
            String lastName = (String) personJSON.get(PERSON_LAST_NAME);
            int age = ((Long) personJSON.get(PERSON_AGE)).intValue();

            // SpecialNeeds is a list in DataWriter's format and a single string in older data
            ArrayList<String> specialNeeds = new ArrayList<>();
            Object needsValue = personJSON.get(PERSON_SPECIAL_NEEDS);
            if (needsValue instanceof JSONArray) {
                for (Object need : (JSONArray) needsValue) {
                    specialNeeds.add((String) need);
                }
            } else if (needsValue instanceof String && !needsValue.equals("none")) {
                specialNeeds.add((String) needsValue);
            }

            people.add(new Person(firstName, lastName, age, specialNeeds));
        }
        return people;
    }

    /**
     * Reads a date like "2026-10-13T13:58:27" or "2024-06-01T12:00:00Z".
     * Returns the current time if there is no date.
    */
   private static LocalDateTime readDateTime(String text) {
       if (text == null) {
           return LocalDateTime.now();
       }
       if (text.endsWith("Z")) {
           text = text.substring(0, text.length() - 1);
       }
       return LocalDateTime.parse(text);
   }

   /**
    * Reads the last known location. Older data stores it as
    * {"latitude": ..., "longitude": ...}, so that becomes "latitude, longitude".
   */
  private static String readLocation(Object locationValue) {
    if (locationValue instanceof JSONObject) {
        JSONObject locationJSON = (JSONObject) locationValue;
        return locationJSON.get(LATITUDE) + ", " + locationJSON.get(LONGITUDE);
    }
    return (String) locationValue;
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
}
