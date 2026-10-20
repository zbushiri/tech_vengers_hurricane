package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Saves project data in JSON files. */
public class DataWriter extends DataConstants {
    private DataWriter() { }

    @SuppressWarnings("unchecked")
    public static boolean saveUsers(List<User> users) {
        JSONArray data = new JSONArray();
        for (User user : users) data.add(userToJSON(user));
        return writeFile(USER_FILE, data);
    }

    public static boolean saveUsers() {
        return saveUsers(UserList.getInstance().getUsers());
    }

    @SuppressWarnings("unchecked")
    public static boolean saveShelters(List<Shelter> shelters) {
        JSONArray data = new JSONArray();
        for (Shelter shelter : shelters) data.add(shelterToJSON(shelter));
        return writeFile(SHELTER_FILE, data);
    }

    @SuppressWarnings("unchecked")
    public static boolean saveRequests(List<ReliefRequest> requests) {
        JSONArray data = new JSONArray();
        for (ReliefRequest request : requests) data.add(requestToJSON(request));
        return writeFile(REQUEST_FILE, data);
    }

    public static boolean saveRequests() {
        return saveRequests(ReliefRequestCatalog.getInstance().getRequests());
    }

    private static boolean writeFile(String fileName, JSONArray data) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(data.toJSONString());
            return true;
        } catch (IOException e) {
            System.out.println("Could not save " + fileName + ".");
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private static JSONObject userToJSON(User user) {
        JSONObject json = new JSONObject();
        json.put(USER_ID, user.getUserId().toString());
        json.put(USER_FIRST_NAME, user.getFirstName());
        json.put(USER_LAST_NAME, user.getLastName());
        json.put(USER_PHONE_NUMBER, user.getPhoneNumber());
        json.put(USER_EMAIL, user.getEmail());
        json.put(USER_ADDRESS, user.getAddress());
        json.put(USER_USERNAME, user.getUsername());
        json.put(USER_PASSWORD, user.getPasswordHash());
        json.put(USER_ROLES, enumArray(user.getRoles()));
        JSONArray people = new JSONArray();
        for (Person person : user.getAssociatedPersons()) people.add(personToJSON(person));
        json.put(ASSOCIATED_PERSONS, people);
        json.put(VERIFICATION_STATUS, enumText(user.getVerificationStatus()));
        json.put(USER_LAST_KNOWN_LOCATION, user.getLastKnownLocation());
        json.put(SAFETY_STATUS, enumText(user.getSafetyStatus()));
        json.put(SAFETY_STATUS_UPDATED_AT, String.valueOf(user.getSafetyStatusUpdatedAt()));
        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject personToJSON(Person person) {
        JSONObject json = new JSONObject();
        json.put(PERSON_FIRST_NAME, person.getFirstName());
        json.put(PERSON_LAST_NAME, person.getLastName());
        json.put(PERSON_AGE, person.getAge());
        JSONArray needs = new JSONArray();
        needs.addAll(person.getSpecialNeeds());
        json.put(PERSON_SPECIAL_NEEDS, needs);
        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject shelterToJSON(Shelter shelter) {
        JSONObject json = new JSONObject();
        json.put(SHELTER_ID, shelter.getShelterId().toString());
        json.put(SHELTER_NAME, shelter.getName());
        json.put(SHELTER_ADDRESS, shelter.getAddress());
        json.put(SHELTER_CAPACITY, shelter.getCapacity());
        json.put(SHELTER_OCCUPANCY, shelter.getOccupancy());
        json.put(SHELTER_STATUS, enumText(shelter.getStatus()));
        json.put(SHELTER_ACCOMMODATIONS, enumArray(shelter.getAccommodations()));
        json.put(SHELTER_LAST_UPDATED, String.valueOf(shelter.getLastUpdated()));
        JSONArray operators = new JSONArray();
        for (User user : shelter.getShelterOperators()) operators.add(user.getUsername());
        json.put(SHELTER_OPERATORS, operators);
        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject requestToJSON(ReliefRequest request) {
        JSONObject json = new JSONObject();
        json.put("requestId", request.getRequestId().toString());
        json.put("type", enumText(request.getType()));
        json.put("status", enumText(request.getStatus()));
        json.put("location", request.getLocation());
        json.put("description", request.getDescription());
        json.put("priority", enumText(request.getPriority()));
        return json;
    }

    private static String enumText(Enum<?> value) {
        return value == null ? "" : value.name().toLowerCase();
    }

    @SuppressWarnings("unchecked")
    private static JSONArray enumArray(List<? extends Enum<?>> values) {
        JSONArray data = new JSONArray();
        if (values != null) for (Enum<?> value : values) data.add(enumText(value));
        return data;
    }
}
