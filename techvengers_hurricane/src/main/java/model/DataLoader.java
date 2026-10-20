package model;

import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/** Loads project data from JSON files. */
public class DataLoader extends DataConstants {
    private DataLoader() { }

    public static UserList getUsers() {
        UserList users = UserList.getInstance();
        if (!users.getUsers().isEmpty()) return users;

        for (Object item : readArray(USER_FILE)) {
            try {
                JSONObject json = (JSONObject) item;
                ArrayList<AccountRole> roles = readEnums(json.get(USER_ROLES), AccountRole.class);
                ArrayList<Person> people = readPeople(json.get(ASSOCIATED_PERSONS));
                User user = new User(
                        readId(json.get(USER_ID)), text(json, USER_FIRST_NAME), text(json, USER_LAST_NAME),
                        text(json, USER_EMAIL), text(json, USER_PHONE_NUMBER), text(json, USER_ADDRESS),
                        text(json, USER_USERNAME), text(json, USER_PASSWORD), roles, people,
                        readEnum(json.get(VERIFICATION_STATUS), VerificationStatus.class, VerificationStatus.UNVERIFIED),
                        readLocation(json.get(USER_LAST_KNOWN_LOCATION)),
                        readEnum(json.get(SAFETY_STATUS), SafetyStatusType.class, SafetyStatusType.UNKNOWN),
                        readDateTime(json.get(SAFETY_STATUS_UPDATED_AT)));
                users.addUser(user);
            } catch (RuntimeException e) {
                System.out.println("Skipped a user: " + e.getMessage());
            }
        }
        return users;
    }

    public static ShelterList getShelters() {
        ShelterList shelters = ShelterList.getInstance();
        if (!shelters.getShelters().isEmpty()) return shelters;
        UserList users = getUsers();

        for (Object item : readArray(SHELTER_FILE)) {
            try {
                JSONObject json = (JSONObject) item;
                Shelter shelter = new Shelter(readId(json.get(SHELTER_ID)), text(json, SHELTER_NAME),
                        text(json, SHELTER_ADDRESS), number(json.get(SHELTER_CAPACITY)),
                        number(json.get(SHELTER_OCCUPANCY)),
                        readEnum(json.get(SHELTER_STATUS), ShelterStatus.class, ShelterStatus.OPEN),
                        readEnums(json.get(SHELTER_ACCOMMODATIONS), Accommodation.class));
                shelter.setLastUpdated(readDateTime(json.get(SHELTER_LAST_UPDATED)));
                shelter.setShelterOperators(readOperators(json.get(SHELTER_OPERATORS), users));
                shelters.addShelter(shelter);
            } catch (RuntimeException e) {
                System.out.println("Skipped a shelter: " + e.getMessage());
            }
        }
        return shelters;
    }

    public static ReliefRequestCatalog getRequests() {
        return ReliefRequestCatalog.getInstance();
    }

    public static ArrayList<HurricaneEvent> getHurricanes() {
        return new ArrayList<>();
    }

    private static JSONArray readArray(String fileName) {
        try (FileReader reader = new FileReader(fileName)) {
            Object data = new JSONParser().parse(reader);
            return data instanceof JSONArray ? (JSONArray) data : new JSONArray();
        } catch (Exception e) {
            System.out.println("Could not load " + fileName + ".");
            return new JSONArray();
        }
    }

    private static String text(JSONObject json, String key) {
        Object value = json.get(key);
        return value == null ? "" : String.valueOf(value);
    }

    private static int number(Object value) {
        return value instanceof Number ? ((Number) value).intValue() : 0;
    }

    private static UUID readId(Object value) {
        try { return UUID.fromString(String.valueOf(value)); }
        catch (Exception e) { return UUID.randomUUID(); }
    }

    private static LocalDateTime readDateTime(Object value) {
        if (value == null) return LocalDateTime.now();
        String text = String.valueOf(value).replace("Z", "");
        try { return LocalDateTime.parse(text); }
        catch (Exception e) { return LocalDateTime.now(); }
    }

    private static String readLocation(Object value) {
        if (value instanceof JSONObject) {
            JSONObject location = (JSONObject) value;
            return location.get(LATITUDE) + ", " + location.get(LONGITUDE);
        }
        return value == null ? "" : String.valueOf(value);
    }

    private static <T extends Enum<T>> T readEnum(Object value, Class<T> type, T fallback) {
        if (value == null) return fallback;
        try { return Enum.valueOf(type, String.valueOf(value).toUpperCase()); }
        catch (IllegalArgumentException e) { return fallback; }
    }

    private static <T extends Enum<T>> ArrayList<T> readEnums(Object value, Class<T> type) {
        ArrayList<T> items = new ArrayList<>();
        if (!(value instanceof JSONArray)) return items;
        for (Object item : (JSONArray) value) {
            try { items.add(Enum.valueOf(type, String.valueOf(item).toUpperCase())); }
            catch (IllegalArgumentException e) { /* Skip unknown values. */ }
        }
        return items;
    }

    private static ArrayList<Person> readPeople(Object value) {
        ArrayList<Person> people = new ArrayList<>();
        if (!(value instanceof JSONArray)) return people;
        for (Object item : (JSONArray) value) {
            JSONObject json = (JSONObject) item;
            ArrayList<String> needs = new ArrayList<>();
            Object savedNeeds = json.get(PERSON_SPECIAL_NEEDS);
            if (savedNeeds instanceof JSONArray) {
                for (Object need : (JSONArray) savedNeeds) needs.add(String.valueOf(need));
            }
            people.add(new Person(text(json, PERSON_FIRST_NAME), text(json, PERSON_LAST_NAME),
                    number(json.get(PERSON_AGE)), needs));
        }
        return people;
    }

    private static ArrayList<User> readOperators(Object value, UserList users) {
        ArrayList<User> operators = new ArrayList<>();
        if (!(value instanceof JSONArray)) return operators;
        for (Object username : (JSONArray) value) {
            User user = users.getUser(String.valueOf(username));
            if (user != null) operators.add(user);
        }
        return operators;
    }
}
