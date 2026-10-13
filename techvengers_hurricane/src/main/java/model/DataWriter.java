package model;

import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Saves project data to JSON files.
 *
 * DataWriter takes Java objects and converts them into JSON
 * so changes can be saved after the program closes.
 *
 * @author Zaki
 */
public class DataWriter extends DataConstants {
    private DataWriter() { }

    /**
     * Saves every user in UserList to users.json.
     *
     * @return true if the file was written
     */
    public static boolean saveUsers() {
        return saveUsers(UserList.getInstance().getUsers());
    }

    /**
     * Saves every shelter in ShelterList to shelters.json.
     *
     * @return true if the file was written
     */
    public static boolean saveShelters() {
        return saveShelters(ShelterList.getInstance().getShelters());
    }

    /**
     * Saves every request in ReliefRequestCatalog to requests.json.
     *
     * @return true if the file was written
     */
    public static boolean saveRequests() {
        return saveRequests(ReliefRequestCatalog.getInstance().getRequests());
    }

    /**
     * Saves all shelters to shelters.json.
     *
     * @param shelters the shelters that will be saved
     * @return true if the file was written
     */
    @SuppressWarnings("unchecked")
    public static boolean saveShelters(ArrayList<Shelter> shelters) {
        JSONArray shelterArray = new JSONArray();

        for (Shelter shelter : shelters) {
            shelterArray.add(shelterToJSON(shelter));
        }

        return writeFile(SHELTER_FILE, shelterArray, "shelters");
    }

    /**
     * Saves all users to users.json.
     *
     * @param users the users that will be saved
     * @return true if the file was written
     */
    @SuppressWarnings("unchecked")
    public static boolean saveUsers(ArrayList<User> users) {
        JSONArray userArray = new JSONArray();

        for (User user : users) {
            userArray.add(userToJSON(user));
        }

        return writeFile(USER_FILE, userArray, "users");
    }

    /**
     * Saves all relief requests to requests.json.
     *
     * @param requests the relief requests that will be saved
     * @return true if the file was written
     */
    @SuppressWarnings("unchecked")
    public static boolean saveRequests(ArrayList<ReliefRequest> requests) {
        JSONArray requestArray = new JSONArray();

        for (ReliefRequest request : requests) {
            requestArray.add(requestToJSON(request));
        }

        return writeFile(REQUEST_FILE, requestArray, "requests");
    }

    /**
     * Writes a finished JSON array to a file.
     *
     * @param fileName the JSON file being written
     * @param data the JSON data being saved
     * @param dataName simple name used for the message
     * @return true if the file was written
     */
    private static boolean writeFile(String fileName, JSONArray data, String dataName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(data.toJSONString());
            System.out.println(dataName + " saved successfully.");
            return true;
        } catch (IOException exception) {
            System.out.println("Could not save " + dataName + ".");
            return false;
        }
    }

    /**
     * Converts one Shelter into JSON.
     *
     * @param shelter the shelter being converted
     * @return the shelter as JSON
     */
    @SuppressWarnings("unchecked")
    private static JSONObject shelterToJSON(Shelter shelter) {
        JSONObject json = new JSONObject();

        json.put("shelterId", shelter.getShelterId().toString());
        json.put("name", shelter.getName());
        json.put("address", shelter.getAddress());
        json.put("capacity", shelter.getCapacity());
        json.put("occupancy", shelter.getOccupancy());
        json.put("status", enumText(shelter.getStatus()));

        JSONArray accommodations = new JSONArray();
        for (Accommodation accommodation : safeList(shelter.getAccommodations())) {
            accommodations.add(accommodation.toString().toLowerCase());
        }
        json.put("accommodations", accommodations);

        json.put("lastUpdated", String.valueOf(shelter.getLastUpdated()));

        JSONArray operators = new JSONArray();
        for (User operator : safeList(shelter.getShelterOperators())) {
            operators.add(operator.getUsername());
        }
        json.put("shelterOperators", operators);

        return json;
    }

    /**
     * Converts one User into JSON.
     *
     * @param user the user being converted
     * @return the user as JSON
     */
    @SuppressWarnings("unchecked")
    private static JSONObject userToJSON(User user) {
        JSONObject json = new JSONObject();

        json.put(USER_ID, user.getUserId().toString());
        json.put(USER_FIRST_NAME, user.getFirstName());
        json.put(USER_LAST_NAME, user.getLastName());
        json.put(USER_PHONE, user.getPhoneNumber());
        json.put(USER_EMAIL, user.getEmail());
        json.put(USER_ADDRESS, user.getAddress());
        json.put(USER_USERNAME, user.getUsername());
        json.put(USER_PASSWORD, user.getPasswordHash());

        JSONArray roles = new JSONArray();
        for (AccountRole role : safeList(user.getRoles())) {
            roles.add(role.toString().toLowerCase());
        }
        json.put(USER_ROLES, roles);

        JSONArray people = new JSONArray();
        for (Person person : safeList(user.getAssociatedPersons())) {
            people.add(personToJSON(person));
        }
        json.put(USER_ASSOCIATED_PEOPLE, people);

        json.put(USER_VERIFICATION, enumText(user.getVerificationStatus()));
        json.put(USER_LOCATION, user.getLastKnownLocation());
        json.put(USER_SAFETY, enumText(user.getSafetyStatus()));
        json.put(USER_SAFETY_UPDATED, String.valueOf(user.getSafetyStatusUpdatedAt()));

        // VolunteerProfile has extra fields that a normal User does not have.
        if (user instanceof VolunteerProfile) {
            VolunteerProfile volunteer = (VolunteerProfile) user;
            JSONArray skills = new JSONArray();
            skills.addAll(safeList(volunteer.getSkills()));
            json.put(VOLUNTEER_SKILLS, skills);
            JSONArray equipment = new JSONArray();
            equipment.addAll(safeList(volunteer.getEquipment()));
            json.put(VOLUNTEER_EQUIPMENT, equipment);
            json.put(VOLUNTEER_BACKGROUND, enumText(volunteer.getBackgroundCheckStatus()));
            json.put(VOLUNTEER_TRANSPORTATION, volunteer.hasTransportationAccess());
            json.put(VOLUNTEER_AVAILABILITY, enumText(volunteer.getAvailabilityStatus()));
        }

        return json;
    }

    /**
     * Converts one associated Person into JSON.
     *
     * @param person the person being converted
     * @return the person as JSON
     */
    @SuppressWarnings("unchecked")
    private static JSONObject personToJSON(Person person) {
        JSONObject json = new JSONObject();

        json.put("firstName", person.getFirstName());
        json.put("lastName", person.getLastName());
        json.put("age", person.getAge());

        JSONArray specialNeeds = new JSONArray();
        specialNeeds.addAll(safeList(person.getSpecialNeeds()));
        json.put("specialNeeds", specialNeeds);

        return json;
    }

    /**
     * Converts one ReliefRequest into JSON.
     *
     * @param request the relief request being converted
     * @return the request as JSON
     */
    @SuppressWarnings("unchecked")
    private static JSONObject requestToJSON(ReliefRequest request) {
        JSONObject json = new JSONObject();

        json.put("requestId", request.getRequestId().toString());
        json.put("type", enumText(request.getType()));
        json.put("status", enumText(request.getStatus()));
        json.put("location", request.getLocation());
        json.put("description", request.getDescription());
        json.put("priority", enumText(request.getPriority()));
        json.put("dateSubmitted", request.getDateSubmitted() == null
                ? null : request.getDateSubmitted().toInstant().toString());
        json.put("numberOfPeopleNeeded", request.getNumberOfPeopleNeeded());
        json.put("numberOfAnimals", request.getNumberOfAnimals());
        json.put("animalNotes", request.getAnimalNotes());
        json.put("photoUrl", request.getPhotoUrl());
        json.put("isSuspicious", request.isSuspicious());

        if (request.getIsDuplicateOf() == null) {
            json.put("isDuplicateOf", null);
        } else {
            json.put("isDuplicateOf", request.getIsDuplicateOf().getRequestId().toString());
        }

        if (request.getForHurricane() == null) {
            json.put("forHurricane", null);
        } else {
            json.put("forHurricane", hurricaneToJSON(request.getForHurricane()));
        }

        return json;
    }

    /**
     * Converts a HurricaneEvent used by a relief request into JSON.
     *
     * @param hurricane the hurricane being converted
     * @return the hurricane as JSON
     */
    @SuppressWarnings("unchecked")
    private static JSONObject hurricaneToJSON(HurricaneEvent hurricane) {
        JSONObject json = new JSONObject();

        json.put("name", hurricane.getName());
        json.put("startDate", dayText(hurricane.getStartDate()));
        json.put("endDate", dayText(hurricane.getEndDate()));
        json.put("status", enumText(hurricane.getStatus()));

        JSONArray affectedAreas = new JSONArray();
        affectedAreas.addAll(safeList(hurricane.getAffectedAreas()));
        json.put("affectedAreas", affectedAreas);

        return json;
    }
    /**
     * Writes an enum value in lower case, the same way the JSON files store it.
     */
    private static String enumText(Enum<?> value) {
        return value == null ? null : value.name().toLowerCase();
    }

    /**
     * Writes a date as "2005-08-23" so DataLoader can read it back.
     * Date.toString() gives text like "Tue Aug 23 00:00:00 EDT 2005" that
     * cannot be loaded again.
     */
    private static String dayText(Date date) {
        return date == null ? null : date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
    }

    /**
     * Returns an empty list when a model list has not been set yet.
     * This keeps saving simple and prevents null list errors.
     *
     * @param list the list being checked
     * @return the original list, or an empty list when null
     */
    private static <T> java.util.List<T> safeList(java.util.List<T> list) {
        return list == null ? java.util.Collections.emptyList() : list;
    }
}
