package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
     * Saves all shelters to shelters.json.
     *
     * @param shelters the shelters that will be saved
     * @return true if the shelters were saved, otherwise false
     */
    @SuppressWarnings("unchecked")
    public static boolean saveShelters(ArrayList<Shelter> shelters) {
        if (shelters == null) {
            return false;
        }

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
     * @return true if the users were saved, otherwise false
     */
    @SuppressWarnings("unchecked")
    public static boolean saveUsers(ArrayList<User> users) {
        if (users == null) {
            return false;
        }

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
     * @return true if the requests were saved, otherwise false
     */
    @SuppressWarnings("unchecked")
    public static boolean saveRequests(ArrayList<ReliefRequest> requests) {
        if (requests == null) {
            return false;
        }

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
     * @return true if the file was written, otherwise false
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
        json.put("status", shelter.getStatus().toString().toLowerCase());

        JSONArray accommodations = new JSONArray();
        for (Accommodation accommodation : safeList(shelter.getAccommodations())) {
            accommodations.add(accommodation.toString().toLowerCase());
        }
        json.put("accommodations", accommodations);

        json.put("lastUpdated", shelter.getLastUpdated().toString());

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
        json.put(USER_EMAIL, user.getEmail());
        json.put(USER_ADDRESS, user.getAddress());
        json.put(USER_USERNAME, user.getUsername());
        json.put(USER_PASSWORD, user.getPasswordHash());

        JSONArray roles = new JSONArray();
        for (AccountRole role : safeList(user.getRoles())) {
            roles.add(role.toString().toLowerCase());
        }
        json.put("roles", roles);

        JSONArray people = new JSONArray();
        for (Person person : safeList(user.getAssociatedPersons())) {
            people.add(personToJSON(person));
        }
        json.put("associatedPeople", people);

        json.put("verificationStatus", user.getVerificationStatus().toString().toLowerCase());
        json.put("lastKnownLocation", user.getLastKnownLocation());
        json.put("safetyStatus", user.getSafetyStatus().toString().toLowerCase());
        json.put("safetyStatusUpdatedAt", user.getSafetyStatusUpdatedAt().toString());

        // VolunteerProfile has extra fields that a normal User does not have.
        if (user instanceof VolunteerProfile) {
            VolunteerProfile volunteer = (VolunteerProfile) user;
            json.put("skills", new JSONArray());
            ((JSONArray) json.get("skills")).addAll(safeList(volunteer.getSkills()));
            json.put("equipment", new JSONArray());
            ((JSONArray) json.get("equipment")).addAll(safeList(volunteer.getEquipment()));
            json.put("backgroundCheckStatus",
                    volunteer.getBackgroundCheckStatus().toString().toLowerCase());
            json.put("transportationStatus", volunteer.hasTransportationAccess());
            json.put("availabilityStatus",
                    volunteer.getAvailabilityStatus().toString().toLowerCase());
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
        json.put("type", request.getType().toString().toLowerCase());
        json.put("status", request.getStatus().toString());
        json.put("location", request.getLocation());
        json.put("description", request.getDescription());
        json.put("priority", request.getPriority().toString().toLowerCase());
        json.put("dateSubmitted", request.getDateSubmitted().toInstant().toString());
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
        json.put("startDate", hurricane.getStartDate().toInstant().toString());
        json.put("endDate", hurricane.getEndDate().toInstant().toString());
        json.put("status", hurricane.getStatus().toString().toLowerCase());

        JSONArray affectedAreas = new JSONArray();
        affectedAreas.addAll(safeList(hurricane.getAffectedAreas()));
        json.put("affectedAreas", affectedAreas);

        return json;
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
