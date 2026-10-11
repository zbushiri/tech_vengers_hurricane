package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/** Converts project objects to JSON and saves them to files. */
public class DataWriter extends DataConstants {
    private DataWriter() { }

    /** Saves all shelters to shelters.json. */
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

    /** Saves all users to users.json. */
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

    /** Saves all relief requests to requests.json. */
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

    /** Writes a finished JSON array to a file. */
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

    /** Converts one shelter into JSON. */
    private static JSONObject shelterToJSON(Shelter shelter) {
        JSONObject json = new JSONObject();

        json.put("shelterId", shelter.getShelterId().toString());
        json.put("name", shelter.getName());
        json.put("address", shelter.getAddress());
        json.put("capacity", shelter.getCapacity());
        json.put("occupancy", shelter.getOccupancy());
        json.put("status", shelter.getStatus().toString().toLowerCase());

        JSONArray accommodations = new JSONArray();
        if (shelter.getAccommodations() != null) {
            for (Accommodation accommodation : shelter.getAccommodations()) {
                accommodations.add(accommodation.toString().toLowerCase());
            }
        }
        json.put("accommodations", accommodations);

        json.put("lastUpdated", shelter.getLastUpdated().toString());

        JSONArray operators = new JSONArray();
        if (shelter.getShelterOperators() != null) {
            for (User operator : shelter.getShelterOperators()) {
                operators.add(operator.getUsername());
            }
        }
        json.put("shelterOperators", operators);

        return json;
    }

    /** Converts one user into JSON. */
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
        if (user.getRoles() != null) {
            for (AccountRole role : user.getRoles()) {
                roles.add(role.toString().toLowerCase());
            }
        }
        json.put("roles", roles);

        JSONArray people = new JSONArray();
        if (user.getAssociatedPersons() != null) {
            for (Person person : user.getAssociatedPersons()) {
                people.add(personToJSON(person));
            }
        }
        json.put("associatedPeople", people);

        json.put("verificationStatus", user.getVerificationStatus().toString().toLowerCase());
        json.put("lastKnownLocation", user.getLastKnownLocation());
        json.put("safetyStatus", user.getSafetyStatus().toString().toLowerCase());
        json.put("safetyStatusUpdatedAt", user.getSafetyStatusUpdatedAt().toString());

        // VolunteerProfile has extra fields that a normal User does not have.
        if (user instanceof VolunteerProfile) {
            VolunteerProfile volunteer = (VolunteerProfile) user;

            JSONArray skills = new JSONArray();
            if (volunteer.getSkills() != null) {
                skills.addAll(volunteer.getSkills());
            }
            json.put("skills", skills);

            JSONArray equipment = new JSONArray();
            if (volunteer.getEquipment() != null) {
                equipment.addAll(volunteer.getEquipment());
            }
            json.put("equipment", equipment);

            json.put("backgroundCheckStatus",
                    volunteer.getBackgroundCheckStatus().toString().toLowerCase());
            json.put("transportationStatus", volunteer.hasTransportationAccess());
            json.put("availabilityStatus",
                    volunteer.getAvailabilityStatus().toString().toLowerCase());
        }

        return json;
    }

    /** Converts one associated person into JSON. */
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

    /** Converts one relief request into JSON. */
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

    /** Converts one hurricane into JSON. */
    private static JSONObject hurricaneToJSON(HurricaneEvent hurricane) {
        JSONObject json = new JSONObject();

        json.put("name", hurricane.getName());
        json.put("startDate", hurricane.getStartDate().toInstant().toString());
        json.put("endDate", hurricane.getEndDate().toInstant().toString());
        json.put("status", hurricane.getStatus().toString().toLowerCase());

        JSONArray affectedAreas = new JSONArray();
        if (hurricane.getAffectedAreas() != null) {
            affectedAreas.addAll(hurricane.getAffectedAreas());
        }
        json.put("affectedAreas", affectedAreas);

        return json;
    }
}
