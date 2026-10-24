package model;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import location.Address;
import location.Coordinates;
import location.MapLocation;

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
                MapLocation location = readShelterLocation(shelterJSON);
                int capacity = readInt(shelterJSON.get(SHELTER_CAPACITY));
                int occupancy = readInt(shelterJSON.get(SHELTER_OCCUPANCY));

                ShelterStatus status = ShelterStatus.OPEN;
                String statusText = (String) shelterJSON.get(SHELTER_STATUS);
                if (statusText != null) {
                    status = ShelterStatus.valueOf(statusText.toUpperCase());
                }

                ArrayList<Accommodation> accommodations = readAccommodations (shelterJSON.get(SHELTER_ACCOMMODATIONS));

                Shelter shelter = new Shelter(shelterId, name, location, capacity,
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
     * Builds a MapLocation from the persisted address and coordinates.
     * Older shelter files stored one comma-separated address string, so that
     * format is still accepted.
     */
    private static MapLocation readShelterLocation(JSONObject shelterJSON) {
        Address address = readShelterAddress(shelterJSON);
        double latitude = readDouble(shelterJSON.get(SHELTER_LATITUDE));
        double longitude = readDouble(shelterJSON.get(SHELTER_LONGITUDE));
        return new MapLocation(address, new Coordinates(latitude, longitude));
    }

    private static Address readShelterAddress(JSONObject shelterJSON) {
        String street = (String) shelterJSON.get(SHELTER_STREET);
        String city = (String) shelterJSON.get(SHELTER_CITY);
        String state = (String) shelterJSON.get(SHELTER_STATE);
        String zip = (String) shelterJSON.get(SHELTER_ZIP);

        if (street != null || city != null || state != null || zip != null) {
            return new Address(defaultText(street), defaultText(city),
                    defaultText(state), defaultText(zip));
        }

        Object legacyAddress = shelterJSON.get("address");
        if (legacyAddress instanceof String) {
            return parseLegacyAddress((String) legacyAddress);
        }
        return new Address("", "", "", "");
    }

    private static Address parseLegacyAddress(String text) {
        String[] parts = text.split("\\s*,\\s*");
        String street = parts.length > 0 ? parts[0] : "";
        String city = parts.length > 1 ? parts[1] : "";
        String state = parts.length > 2 ? parts[2] : "";
        String zip = parts.length > 3 ? parts[3] : "";

        if (parts.length == 3) {
            String[] stateAndZip = parts[2].trim().split("\\s+");
            if (stateAndZip.length > 1) {
                zip = stateAndZip[stateAndZip.length - 1];
                state = parts[2].substring(0, parts[2].lastIndexOf(zip)).trim();
            }
        }
        return new Address(street, city, state, zip);
    }

    private static String defaultText(String value) {
        return value == null ? "" : value;
    }

    private static double readDouble(Object numberValue) {
        return numberValue instanceof Number ? ((Number) numberValue).doubleValue() : 0.0;
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
          User user = users.getUser((String) username);
          if (user != null) {
              operators.add(user);
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
                Address address = readAddressValue(userJSON.get(USER_ADDRESS));
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
                MapLocation lastKnownLocation = readLocation(userJSON.get(USER_LAST_KNOWN_LOCATION));

                User user = new User(userId, firstName, lastName, email, phoneNumber, address, 
                        username, password, roles, associatedPersons, verificationStatus,
                        lastKnownLocation, safetyStatus, safetyStatusUpdatedAt);
                if (user.hasRole(AccountRole.VOLUNTEER)) {
                    user = readVolunteer(user, userJSON);
                }
                users.addUser(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return users;
    }
    
    /**
     * Builds a VolunteerProfile from a user and the volunteer fields saved with it.
     */
    private static VolunteerProfile readVolunteer(User user, JSONObject userJSON) {
        ArrayList<String> skills = readStrings(userJSON.get(VOLUNTEER_SKILLS));
        ArrayList<String> equipment = readStrings(userJSON.get(VOLUNTEER_EQUIPMENT));

        BackgroundCheckStatus backgroundCheck = BackgroundCheckStatus.PENDING;
        String backgroundText = (String) userJSON.get(VOLUNTEER_BACKGROUND_CHECK);
        if (backgroundText != null) {
            backgroundCheck = BackgroundCheckStatus.valueOf(backgroundText.toUpperCase());
        }

        // Saved as a boolean by DataWriter and as "true"/"false" in the sample data
        Object transportationValue = userJSON.get(VOLUNTEER_TRANSPORTATION);
        if (transportationValue == null) {
            transportationValue = userJSON.get(VOLUNTEER_TRANSPORTATION_OLD);
        }
        boolean transportation = Boolean.parseBoolean(String.valueOf(transportationValue));

        AvailabilityStatus availability = AvailabilityStatus.AVAILABLE;
        String availabilityText = (String) userJSON.get(VOLUNTEER_AVAILABILITY);
        if (availabilityText != null) {
            availability = AvailabilityStatus.valueOf(availabilityText.toUpperCase());
        }

        return new VolunteerProfile(user, skills, equipment, backgroundCheck, transportation, availability);
    }

    /**
     * Turns a JSON array of text into a list. Anything else is an empty list.
     */
    private static ArrayList<String> readStrings(Object value) {
        ArrayList<String> items = new ArrayList<>();
        if (value instanceof JSONArray) {
            for (Object item : (JSONArray) value) {
                items.add((String) item);
            }
        }
        return items;
    }

    /**
     * Turns a saved id into a UUID.
     * Old data uses numbers for ids, so those get a new UUID.
     */
    private static UUID readID(Object idValue) {
        if (idValue instanceof String) {
            try {
                return UUID.fromString((String) idValue);
            } catch (IllegalArgumentException e) {
                // an old id like "1" is not a UUID, so it gets a new one
            }
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
  private static MapLocation readLocation(Object locationValue) {
    if (locationValue instanceof JSONObject) {
        JSONObject locationJSON = (JSONObject) locationValue;
      double latitude = readDouble(locationJSON.get(LATITUDE));
      double longitude = readDouble(locationJSON.get(LONGITUDE));
      return new MapLocation(new Address("", "", "", ""),
              new Coordinates(latitude, longitude));
  }
  if (locationValue instanceof String) {
      return new MapLocation(parseLegacyAddress((String) locationValue),
              new Coordinates(0.0, 0.0));
  }
  return null;
  }

  private static Address readAddressValue(Object addressValue) {
  if (addressValue instanceof String) {
      return parseLegacyAddress((String) addressValue);
  }
  if (addressValue instanceof JSONObject) {
      JSONObject addressJSON = (JSONObject) addressValue;
      return new Address(
              defaultText((String) addressJSON.get(SHELTER_STREET)),
              defaultText((String) addressJSON.get(SHELTER_CITY)),
              defaultText((String) addressJSON.get(SHELTER_STATE)),
              defaultText((String) addressJSON.get(SHELTER_ZIP)));
  }
  return new Address("", "", "", "");
  }

    /**
     * Loads the relief requests from the requests JSON file.
     * 
     * @return the list of requests or an empty list if none load
     */
    public static ReliefRequestCatalog getRequests() {
        ReliefRequestCatalog requests = ReliefRequestCatalog.getInstance();

        if (!requests.getRequests().isEmpty()) {
            return requests;
        }

        // isDuplicateOf is saved as an id, so the links are made after every request is loaded
        HashMap<ReliefRequest, String> duplicateIds = new HashMap<>();
        HashMap<String, ReliefRequest> requestsById = new HashMap<>();

        try (FileReader reader = new FileReader(REQUEST_FILE)) {
            JSONArray requestsJSON = (JSONArray) new JSONParser().parse(reader);

            for (int i = 0; i < requestsJSON.size(); i++) {
                JSONObject requestJSON = (JSONObject) requestsJSON.get(i);

                String savedId = String.valueOf(requestJSON.get(REQUEST_ID));
                UUID requestId = readID(requestJSON.get(REQUEST_ID));
                RequestType type = RequestType.valueOf(((String) requestJSON.get(REQUEST_TYPE)).toUpperCase());
                String description = (String) requestJSON.get(REQUEST_DESCRIPTION);
                String location = (String) requestJSON.get(REQUEST_LOCATION);
                int people = readInt(requestJSON.get(REQUEST_PEOPLE));
                int animals = readInt(requestJSON.get(REQUEST_ANIMALS));
                String animalNotes = (String) requestJSON.get(REQUEST_ANIMAL_NOTES);
                String photoUrl = (String) requestJSON.get(REQUEST_PHOTO_URL);
                boolean isSuspicious = Boolean.TRUE.equals(requestJSON.get(REQUEST_IS_SUSPICIOUS));

                HurricaneEvent hurricane = null;
                Object hurricaneValue = requestJSON.get(REQUEST_HURRICANE);
                if (hurricaneValue instanceof JSONObject) {
                    hurricane = hurricaneFromJSON((JSONObject) hurricaneValue);
                }

                ReliefRequest request = new ReliefRequest(requestId, type, description, location, people, 
                        animals, animalNotes, photoUrl, isSuspicious, null, hurricane);

                // The constructor sets SUBMITTED, today's date and a calculated priority, so put back the saved values 
                String statusText = (String) requestJSON.get(REQUEST_STATUS);
                if (statusText != null) {
                    request.setStatus(RequestStatus.valueOf(statusText.toUpperCase()));
                }
                String priorityText = (String) requestJSON.get(REQUEST_PRIORITY);
                if (priorityText != null) {
                    request.setPriority(PriorityLevel.valueOf(priorityText.toUpperCase()));
                }
                String dateText = (String) requestJSON.get(REQUEST_DATE_SUBMITTED);
                if (dateText != null) {
                    request.setDateSubmitted(parseDate(dateText));
                }

                Object duplicateValue = requestJSON.get(REQUEST_DUPLICATE_OF);
                if (duplicateValue != null) {
                    duplicateIds.put(request, String.valueOf(duplicateValue));
                }
                requestsById.put(savedId, request);
                requests.addLoadedRequest(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (ReliefRequest request : duplicateIds.keySet()) {
            request.setIsDuplicateOf(requestsById.get(duplicateIds.get(request)));
        }
        return requests;
    }

    /**
     * Reads a whole number. json-simple gives back a Long, and a missing value is 0.
     */
    private static int readInt(Object numberValue) {
        return numberValue instanceof Number ? ((Number) numberValue).intValue() : 0;
    }

    /**
     * Turns a date like "2005-08-23" or "2005-08-24T00:00:00Z" into a Date.
     */
    private static Date parseDate(String text) {
        if (text == null) {
            return null;
        }
        if (text.length() == 10) {
            return Date.from(LocalDate.parse(text).atStartOfDay(ZoneOffset.UTC).toInstant());
        }
        return Date.from(Instant.parse(text));
    }

    /**
     * Turns one hurricane's JSON into a HurricaneEvent.
     */
    private static HurricaneEvent hurricaneFromJSON(JSONObject json) {
        String name = (String) json.get(HURRICANE_NAME);
        Date start = parseDate((String) json.get(HURRICANE_START));
        Date end = parseDate((String) json.get(HURRICANE_END));

        EventStatus status = EventStatus.MONITORING;
        String statusText = (String) json.get(HURRICANE_STATUS);
        if (statusText != null) {
            status = EventStatus.valueOf(statusText.toUpperCase());
        }

        ArrayList<String> areas = readStrings(json.get(HURRICANE_AREAS));

        return new HurricaneEvent(name, start, end, areas, status);
    }

    /**
     * Loads the Hurricane Event from the hurricanes JSON file.
     * 
     * @return the list of hurricanes or an empty list if none load.
     */
    public static ArrayList<HurricaneEvent> getHurricanes() {
        ArrayList<HurricaneEvent> hurricanes = new ArrayList<>();

        try (FileReader reader = new FileReader(HURRICANE_FILE)) {
            JSONArray hurricanesJSON = (JSONArray) new JSONParser().parse(reader);

            for (int i = 0; i < hurricanesJSON.size(); i++) {
                JSONObject hurricaneJSON = (JSONObject) hurricanesJSON.get(i);
                hurricanes.add(hurricaneFromJSON(hurricaneJSON));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return hurricanes;
        }
}
