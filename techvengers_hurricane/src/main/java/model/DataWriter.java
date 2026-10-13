package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

// * Saves project information in JSON files.
public class DataWriter extends DataConstants {
    private DataWriter() { }
    /**
     * Saves all shelters to shelters.json.
     *
     * @param shelters the shelters that will be saved
     */
    @SuppressWarnings("unchecked")
    public static void saveShelters(ArrayList<Shelter> shelters) {
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
     */
    @SuppressWarnings("unchecked")
    public static void saveUsers(ArrayList<User> users) {
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
     */
    @SuppressWarnings("unchecked")
    public static void saveRequests(ArrayList<ReliefRequest> requests) {
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
     */
    private static void writeFile(String fileName, JSONArray data, String dataName) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(data.toJSONString());
            System.out.println(dataName + " saved successfully.");
            return true;
        } catch (IOException exception) {
            System.out.println("Could not save " + dataName + ".");
            return false;
        }
    }

    // * Builds the JSON for one shelter.
    private static JSONObject shelterToJSON(Shelter shelter) {
        JSONObject json = new JSONObject();

        json.put("shelterId", shelter.getShelterId().toString());
        json.put("name", shelter.getName());
        json.put("address", shelter.getAddress());
        json.put("capacity", shelter.getCapacity());
        json.put("occupancy", shelter.getOccupancy());
        json.put("status", enumText(shelter.getStatus()));

        JSONArray accommodations = new JSONArray();
        if (shelter.getAccommodations() != null) {
            for (Accommodation accommodation : shelter.getAccommodations()) {
                accommodations.add(accommodation.toString().toLowerCase());
            }
        }
        json.put("accommodations", accommodations);

        json.put("lastUpdated", String.valueOf(shelter.getLastUpdated()));

        JSONArray operators = new JSONArray();
        if (shelter.getShelterOperators() != null) {
            for (User operator : shelter.getShelterOperators()) {
                operators.add(operator.getUsername());
            }
        }
        json.put("shelterOperators", operators);

        return json;
    }

    // * Builds the JSON for one user.
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
        if (user.getRoles() != null) {
            for (AccountRole role : user.getRoles()) {
                roles.add(role.toString().toLowerCase());
            }
        }
        json.put(USER_ROLES, roles);

        JSONArray people = new JSONArray();
        if (user.getAssociatedPersons() != null) {
            for (Person person : user.getAssociatedPersons()) {
                people.add(personToJSON(person));
            }
        }
        json.put(USER_ASSOCIATED_PEOPLE, people);

        json.put(USER_VERIFICATION, enumText(user.getVerificationStatus()));
        json.put(USER_LOCATION, user.getLastKnownLocation());
        json.put(USER_SAFETY, enumText(user.getSafetyStatus()));
        json.put(USER_SAFETY_UPDATED, String.valueOf(user.getSafetyStatusUpdatedAt()));

        // * Adds information that only volunteers have.
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

    // * Builds the JSON for one person.
    private static JSONObject personToJSON(Person person) {
        JSONObject json = new JSONObject();

        json.put("firstName", person.getFirstName());
        json.put("lastName", person.getLastName());
        json.put("age", person.getAge());

        JSONArray specialNeeds = new JSONArray();
        if (person.getSpecialNeeds() != null) {
            specialNeeds.addAll(person.getSpecialNeeds());
        }
        json.put("specialNeeds", specialNeeds);

        return json;
    }

    // * Builds the JSON for one relief request.
    private static JSONObject requestToJSON(ReliefRequest request) {
        JSONObject json = new JSONObject();

        json.put("requestId", request.getRequestId().toString());
        json.put("type", enumText(request.getType()));
        json.put("status", enumText(request.getStatus()));
        json.put("location", request.getLocation());
        json.put("description", request.getDescription());
        json.put("priority", request.getPriority().toString().toLowerCase());
        json.put("dateSubmitted", request.getDateSubmitted().toString());
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

    // * Builds the JSON for one hurricane.
    private static JSONObject hurricaneToJSON(HurricaneEvent hurricane) {
        JSONObject json = new JSONObject();

        json.put("name", hurricane.getName());
        json.put("startDate", hurricane.getStartDate().toString());
        json.put("endDate", hurricane.getEndDate().toString());
        json.put("status", hurricane.getStatus().toString().toLowerCase());

        JSONArray affectedAreas = new JSONArray();
        if (hurricane.getAffectedAreas() != null) {
            affectedAreas.addAll(hurricane.getAffectedAreas());
        }
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
