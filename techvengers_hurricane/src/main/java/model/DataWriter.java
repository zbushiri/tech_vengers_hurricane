package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
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

        if (user instanceof VolunteerProfile) {
            VolunteerProfile volunteer = (VolunteerProfile) user;
            json.put(VOLUNTEER_SKILLS, textArray(volunteer.getSkills()));
            json.put(VOLUNTEER_EQUIPMENT, textArray(volunteer.getEquipment()));
            json.put(VOLUNTEER_BACKGROUND_CHECK, enumText(volunteer.getBackgroundCheckStatus()));
            json.put(VOLUNTEER_TRANSPORTATION, volunteer.hasTransportationAccess());
            json.put(VOLUNTEER_AVAILABILITY, enumText(volunteer.getAvailabilityStatus()));
        }
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
        json.put(SHELTER_STREET, shelter.getLocation().getAddress().getStreet());
        json.put(SHELTER_CITY, shelter.getLocation().getAddress().getCity());
        json.put(SHELTER_STATE, shelter.getLocation().getAddress().getState());
        json.put(SHELTER_ZIP, shelter.getLocation().getAddress().getZip());
        if (shelter.getLocation().getCoordinates() != null) {
            json.put(SHELTER_LATITUDE, shelter.getLocation().getCoordinates().getLatitude());
            json.put(SHELTER_LONGITUDE, shelter.getLocation().getCoordinates().getLongitude());
        }
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
        json.put(REQUEST_ID, request.getRequestId().toString());
        json.put(REQUEST_TYPE, enumText(request.getType()));
        json.put(REQUEST_STATUS, enumText(request.getStatus()));
        json.put(REQUEST_LOCATION, request.getLocation());
        json.put(REQUEST_DESCRIPTION, request.getDescription());
        json.put(REQUEST_PRIORITY, enumText(request.getPriority()));
        json.put(REQUEST_DATE_SUBMITTED, dateText(request.getDateSubmitted()));
        json.put(REQUEST_PEOPLE, request.getNumberOfPeopleNeeded());
        json.put(REQUEST_ANIMALS, request.getNumberOfAnimals());
        json.put(REQUEST_ANIMAL_NOTES, request.getAnimalNotes());
        json.put(REQUEST_PHOTO_URL, request.getPhotoUrl());
        json.put(REQUEST_IS_SUSPICIOUS, request.isSuspicious());
        ReliefRequest original = request.getIsDuplicateOf();
        json.put(REQUEST_DUPLICATE_OF, original == null ? null : original.getRequestId().toString());
        HurricaneEvent hurricane = request.getForHurricane();
        json.put(REQUEST_HURRICANE, hurricane == null ? null : hurricaneToJSON(hurricane));
        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject hurricaneToJSON(HurricaneEvent hurricane) {
        JSONObject json = new JSONObject();
        json.put(HURRICANE_NAME, hurricane.getName());
        json.put(HURRICANE_START, dateText(hurricane.getStartDate()));
        json.put(HURRICANE_END, dateText(hurricane.getEndDate()));
        json.put(HURRICANE_STATUS, enumText(hurricane.getStatus()));
        json.put(HURRICANE_AREAS, textArray(hurricane.getAffectedAreas()));
        return json;
    }

    /** Writes a date like "2024-06-01T12:00:00Z", which DataLoader can read back. */
    private static String dateText(Date date) {
        return date == null ? null : date.toInstant().toString();
    }

    /** A missing value is saved as null so DataLoader falls back to its default. */
    private static String enumText(Enum<?> value) {
        return value == null ? null : value.name().toLowerCase();
    }

    @SuppressWarnings("unchecked")
    private static JSONArray textArray(List<String> values) {
        JSONArray data = new JSONArray();
        if (values != null) data.addAll(values);
        return data;
    }

    @SuppressWarnings("unchecked")
    private static JSONArray enumArray(List<? extends Enum<?>> values) {
        JSONArray data = new JSONArray();
        if (values != null) for (Enum<?> value : values) data.add(enumText(value));
        return data;
    }
}
