package model;

import java.io.FileReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

                User user = new User(firstName, lastName, email, phoneNumber, address, username, password, roles, lastKnownLocation); 
                users.addUser(user);
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
            }
        }
        return users;
    }

    /**
     * Loads the shelters from the shelters JSON file. Shelter operators are
     * matched to the users already in UserList, so load users first.
     *
     * @return the list of shelters or an empty list if none load
     */
    public static ArrayList<Shelter> getShelters() {
        ArrayList<Shelter> shelters = new ArrayList<>();

        for (JSONObject shelterJSON : readArray(SHELTER_FILE)) {
            try {
                shelters.add(shelterFromJSON(shelterJSON));
            } catch (RuntimeException e) {
                System.out.println("Skipped a shelter that could not be read: " + e.getMessage());
            }
        }
        return shelters;
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

<<<<<<< HEAD
    private static ArrayList<JSONObject> readArray(String fileName) {
        ArrayList<JSONObject> objects = new ArrayList<>();
        try (FileReader reader = new FileReader(fileName)) {
            JSONArray array = (JSONArray) new JSONParser().parse(reader);
            for (Object item : array) {
                if (item instanceof JSONObject) {
                    objects.add((JSONObject) item);
                }
            }
        } catch (Exception e) {
            System.out.println("Could not read " + fileName + ": " + e.getMessage());
        }
        return objects;
    }

    private static User userFromJSON(JSONObject json) {
        String username = getString(json, USER_USERNAME);
        Object id = first(json, USER_ID, "id");

        User user = new User(
                id == null ? toUUID(username, "user") : toUUID(String.valueOf(id), "user"),
                getString(json, USER_FIRST_NAME),
                getString(json, USER_LAST_NAME),
                getString(json, USER_PHONE),
                getString(json, USER_EMAIL),
                getString(json, USER_ADDRESS),
                username,
                getString(json, USER_PASSWORD),
                toRoles(json.get(USER_ROLES)),
                toPeople(first(json, USER_ASSOCIATED_PEOPLE, "associated_people")),
                toEnum(VerificationStatus.class, first(json, USER_VERIFICATION, "verification_status"),
                        VerificationStatus.UNVERIFIED),
                toLocation(json.get(USER_LOCATION)),
                toEnum(SafetyStatusType.class, first(json, USER_SAFETY, "safety_status"),
                        SafetyStatusType.UNKNOWN),
                toDateTime(json.get(USER_SAFETY_UPDATED)));

        boolean isVolunteer = user.hasRole(AccountRole.VOLUNTEER)
                || json.containsKey(VOLUNTEER_SKILLS) || json.containsKey(VOLUNTEER_AVAILABILITY);
        if (!isVolunteer) {
            return user;
        }
        return new VolunteerProfile(user,
                toStringList(json.get(VOLUNTEER_SKILLS)),
                toStringList(json.get(VOLUNTEER_EQUIPMENT)),
                toEnum(BackgroundCheckStatus.class, json.get(VOLUNTEER_BACKGROUND), BackgroundCheckStatus.PENDING),
                Boolean.TRUE.equals(json.get(VOLUNTEER_TRANSPORTATION)),
                toEnum(AvailabilityStatus.class, json.get(VOLUNTEER_AVAILABILITY), AvailabilityStatus.UNAVAILABLE));
    }

    private static Shelter shelterFromJSON(JSONObject json) {
        ArrayList<Accommodation> accommodations = new ArrayList<>();
        for (String item : toStringList(json.get("accommodations"))) {
            Accommodation accommodation = toAccommodation(item);
            if (accommodation != null && !accommodations.contains(accommodation)) {
                accommodations.add(accommodation);
            }
        }

        Shelter shelter = new Shelter(
                toUUID(String.valueOf(json.get("shelterId")), "shelter"),
                getString(json, "name"),
                getString(json, "address"),
                toInt(json.get("capacity")),
                toInt(json.get("occupancy")),
                toEnum(ShelterStatus.class, json.get("status"), ShelterStatus.OPEN),
                accommodations);
        if (json.get("lastUpdated") != null) {
            shelter.setLastUpdated(toDateTime(json.get("lastUpdated")));
        }

        for (String operatorName : toStringList(json.get("shelterOperators"))) {
            shelter.getShelterOperators().add(findOperator(operatorName));
        }
        return shelter;
    }

    /**
     * Operators may be saved as a username or as a full name. If no user
     * matches, a placeholder keeps the saved value so it is not lost on save.
     */
    private static User findOperator(String name) {
        for (User user : UserList.getInstance().getUsers()) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            if (name.equalsIgnoreCase(user.getUsername()) || name.equalsIgnoreCase(fullName)) {
                return user;
            }
        }
        return new User(name, "");
    }

    private static ReliefRequest requestFromJSON(JSONObject json) {
        HurricaneEvent hurricane = null;
        if (json.get("forHurricane") instanceof JSONObject) {
            hurricane = hurricaneFromJSON((JSONObject) json.get("forHurricane"));
        }

        ReliefRequest request = new ReliefRequest(
                toUUID(String.valueOf(json.get("requestId")), "request"),
                toEnum(RequestType.class, json.get("type"), RequestType.OTHER),
                getString(json, "description"),
                getString(json, "location"),
                toInt(json.get("numberOfPeopleNeeded")),
                toInt(json.get("numberOfAnimals")),
                getString(json, "animalNotes"),
                getString(json, "photoUrl"),
                Boolean.TRUE.equals(json.get("isSuspicious")),
                null,
                hurricane);
        request.setStatus(toEnum(RequestStatus.class, json.get("status"), RequestStatus.SUBMITTED));
        request.setPriority(toEnum(PriorityLevel.class, json.get("priority"), request.calculatePriority()));
        if (json.get("dateSubmitted") != null) {
            request.setDateSubmitted(toDate(json.get("dateSubmitted")));
        }
        return request;
    }

    private static HurricaneEvent hurricaneFromJSON(JSONObject json) {
        return new HurricaneEvent(
                getString(json, "name"),
                toDate(json.get("startDate")),
                toDate(json.get("endDate")),
                toStringList(json.get("affectedAreas")),
                toEnum(EventStatus.class, json.get("status"), EventStatus.MONITORING));
    }

    // ---- Helpers that turn JSON values into Java values ----

    private static Object first(JSONObject json, String key, String otherKey) {
        return json.containsKey(key) ? json.get(key) : json.get(otherKey);
    }

    private static String getString(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private static int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return value == null ? 0 : Integer.parseInt(String.valueOf(value).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Ids saved by DataWriter are UUIDs. Older sample data uses numbers like
     * 1 or "1", which are turned into the same UUID every time they load.
     */
    private static UUID toUUID(String value, String kind) {
        if (value == null || value.equals("null") || value.trim().isEmpty()) {
            return UUID.randomUUID();
        }
        try {
            return UUID.fromString(value.trim());
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes((kind + "-" + value.trim()).getBytes());
        }
    }

    /**
     * Reads an enum value like "open", "IN_PROGRESS" or "on assignment".
     */
    private static <E extends Enum<E>> E toEnum(Class<E> type, Object value, E fallback) {
        if (value == null) {
            return fallback;
        }
        String name = String.valueOf(value).trim().toUpperCase().replace(' ', '_').replace('-', '_');
        try {
            return Enum.valueOf(type, name);
        } catch (IllegalArgumentException e) {
            return fallback;
        }
    }

    /**
     * Reads roles. "user" in the sample data is treated as a resident, and
     * words that are not roles are skipped.
     */
    private static List<AccountRole> toRoles(Object value) {
        List<AccountRole> roles = new ArrayList<>();
        for (String word : toStringList(value)) {
            AccountRole role = word.equalsIgnoreCase("user")
                    ? AccountRole.RESIDENT
                    : toEnum(AccountRole.class, word, null);
            if (role != null && !roles.contains(role)) {
                roles.add(role);
            }
        }
        return roles;
    }

    private static Accommodation toAccommodation(String word) {
        String lower = word.toLowerCase();
        if (lower.contains("pet")) {
            return Accommodation.PET_FRIENDLY;
        }
        if (lower.contains("wheelchair") || lower.contains("accessible")) {
            return Accommodation.WHEELCHAIR_ACCESSIBLE;
        }
        if (lower.contains("medical")) {
            return Accommodation.MEDICAL_SUPPORT;
        }
        return toEnum(Accommodation.class, word, null);
    }

    private static List<Person> toPeople(Object value) {
        List<Person> people = new ArrayList<>();
        if (!(value instanceof JSONArray)) {
            return people;
        }
        for (Object item : (JSONArray) value) {
            if (item instanceof JSONObject) {
                JSONObject json = (JSONObject) item;
                ArrayList<String> needs = new ArrayList<>();
                for (String need : toStringList(json.get("specialNeeds"))) {
                    if (!need.equalsIgnoreCase("none") && !need.isEmpty()) {
                        needs.add(need);
                    }
                }
                people.add(new Person(getString(json, "firstName"), getString(json, "lastName"),
                        toInt(json.get("age")), needs));
            }
        }
        return people;
    }

    /**
     * Accepts a JSON array of strings or a single string.
     */
    private static ArrayList<String> toStringList(Object value) {
        ArrayList<String> list = new ArrayList<>();
        if (value instanceof JSONArray) {
            for (Object item : (JSONArray) value) {
                if (item != null) {
                    list.add(String.valueOf(item).trim());
                }
            }
        } else if (value != null && !String.valueOf(value).trim().isEmpty()) {
            list.add(String.valueOf(value).trim());
        }
        return list;
    }

    /**
     * The location is a string, but sample data also stores it as
     * {"latitude": .., "longitude": ..}, which becomes "lat, long".
     */
    private static String toLocation(Object value) {
        if (value instanceof JSONObject) {
            JSONObject json = (JSONObject) value;
            return json.get("latitude") + ", " + json.get("longitude");
        }
        return value == null ? "" : String.valueOf(value);
    }

    /**
     * Reads "2024-06-01T12:00:00Z", "2024-06-01T12:00:00" or "2024-06-01".
     */
    private static LocalDateTime toDateTime(Object value) {
        if (value == null) {
            return LocalDateTime.now();
        }
        String text = String.valueOf(value).trim();
        try {
            return OffsetDateTime.parse(text).atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception ignored) {
        }
        try {
            return LocalDateTime.parse(text);
        } catch (Exception ignored) {
        }
        try {
            return LocalDate.parse(text).atStartOfDay();
        } catch (Exception ignored) {
        }
        return LocalDateTime.now();
    }

    private static Date toDate(Object value) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        try {
            return Date.from(Instant.parse(text));
        } catch (Exception ignored) {
        }
        return Date.from(toDateTime(text).atZone(ZoneId.systemDefault()).toInstant());
    }

    public static void main(String[] args) {
        System.out.println("Users loaded: " + getUsers().size());
        System.out.println("Shelters loaded: " + getShelters().size());
        System.out.println("Requests loaded: " + getRequests().size());
        System.out.println("Hurricanes loaded: " + getHurricanes().size());
    }
=======
        public static void main(String[] args) {
            getUsers();
        System.out.println("Users loaded: " + getUsers().getUsers().size());
        }
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
}
