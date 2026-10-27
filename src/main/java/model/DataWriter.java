package model;

import java.io.IOException;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Saves project data in JSON files.
 * @author Team Tech-vengers
 */
public class DataWriter extends DataConstants {
    private DataWriter() { }

    /**
     * Serializes the users to JSON and replaces the saved file after writing a temporary copy.
     * @param users user accounts to serialize to the users JSON file
     * @return true if the users JSON file was written successfully
     */
    @SuppressWarnings("unchecked")
    public static boolean saveUsers(List<User> users) {
        JSONArray data = new JSONArray();
        for (User user : users) data.add(userToJSON(user));
        return writeFile(USER_FILE, data);
    }

    /**
     * Serializes the users to JSON and replaces the saved file after writing a temporary copy.
     * @return true if the users JSON file was written successfully
     */
    public static boolean saveUsers() {
        return saveUsers(UserList.getInstance().getUsers());
    }

    /**
     * Serializes the shelters to JSON and replaces the saved file after writing a temporary copy.
     * @param shelters shelters to serialize to the shelters JSON file
     * @return true if the shelters JSON file was written successfully
     */
    @SuppressWarnings("unchecked")
    public static boolean saveShelters(List<Shelter> shelters) {
        JSONArray data = new JSONArray();
        for (Shelter shelter : shelters) data.add(shelterToJSON(shelter));
        return writeFile(SHELTER_FILE, data);
    }

    /**
     * Serializes the requests to JSON and replaces the saved file after writing a temporary copy.
     * @param requests relief requests to serialize to the requests JSON file
     * @return true if the requests JSON file was written successfully
     */
    @SuppressWarnings("unchecked")
    public static boolean saveRequests(List<ReliefRequest> requests) {
        JSONArray data = new JSONArray();
        for (ReliefRequest request : requests) data.add(requestToJSON(request));
        return writeFile(REQUEST_FILE, data);
    }

    /**
     * Serializes the requests to JSON and replaces the saved file after writing a temporary copy.
     * @return true if the requests JSON file was written successfully
     */
    public static boolean saveRequests() {
        return saveRequests(ReliefRequestCatalog.getInstance().getRequests());
    }

    /** Writes through a temporary sibling so a failed write does not truncate saved data. */
    private static boolean writeFile(String fileName, JSONArray data) {
        Path target = Path.of(fileName).toAbsolutePath();
        Path temporary = null;
        try {
            Files.createDirectories(target.getParent());
            temporary = Files.createTempFile(target.getParent(), ".relief-", ".tmp");
            Files.writeString(temporary, data.toJSONString(), StandardCharsets.UTF_8);
            try {
                Files.move(temporary, target, java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (java.nio.file.AtomicMoveNotSupportedException e) {
                Files.move(temporary, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            System.err.println("Could not save " + fileName + ": " + e.getMessage());
            return false;
        } finally {
            if (temporary != null) {
                try { Files.deleteIfExists(temporary); } catch (IOException ignored) { /* Best-effort cleanup. */ }
            }
        }
    }

    /** Serializes the named people in a request or shelter admission. */
    @SuppressWarnings("unchecked")
    private static JSONArray peopleJSON(List<Person> people) {
        JSONArray data = new JSONArray();
        for (Person person : people) data.add(personToJSON(person));
        return data;
    }

    /** Serializes accompanying animals independently from human occupants. */
    @SuppressWarnings("unchecked")
    private static JSONArray petsJSON(List<Pet> pets) {
        JSONArray data = new JSONArray();
        for (Pet pet : pets) {
            JSONObject item = new JSONObject(); item.put("name", pet.name()); item.put("species", pet.species()); data.add(item);
        }
        return data;
    }

    /**
     * Exports a new UTF-8 trip sheet containing the household and both route legs.
     * Existing trip sheets are never overwritten.
     * @param request assigned request with named household members
     * @param shelter selected destination shelter
     * @param toPickup directions from the volunteer to the family
     * @param toShelter directions from the family to the shelter
     * @param directory output folder
     * @return path to the newly created text file
     * @throws IOException if the directory or file cannot be written
     */
    public static Path saveTripSheet(ReliefRequest request, Shelter shelter,
            location.RoutePlan toPickup, location.RoutePlan toShelter, Path directory) throws IOException {
        if (request == null || shelter == null || request.getSubmittedBy() == null
                || request.getAssignedVolunteer() == null || request.getFamilyMembers().isEmpty()
                || !shelter.getShelterId().equals(request.getDestinationShelterId())
                || toPickup == null || toShelter == null) throw new IllegalArgumentException("Select and assign a complete household request first.");
        StringBuilder text = new StringBuilder("Gamecock Relief Network - Trip Sheet\n\n");
        User volunteer = request.getAssignedVolunteer(); User contact = request.getSubmittedBy();
        text.append("Request: ").append(request.getRequestId()).append("\nVolunteer: ").append(volunteer.getFirstName()).append(' ').append(volunteer.getLastName());
        text.append("\nVolunteer departure: ").append(volunteer.getDepartureAddress());
        text.append("\n\nFamily members:\n");
        for (Person person : request.getFamilyMembers()) {
            text.append("  ").append(person.getFirstName()).append(' ').append(person.getLastName()).append(", age ").append(person.getAge()).append("\n");
        }
        text.append("Pets:\n");
        if (request.getPets().isEmpty()) text.append("  None\n");
        for (Pet pet : request.getPets()) text.append("  ").append(pet.name()).append(" (").append(pet.species()).append(")\n");
        text.append("Contact: ").append(contact.getFirstName()).append(' ').append(contact.getLastName())
                .append("; phone ").append(contact.getPhoneNumber()).append("; email ").append(contact.getEmail());
        text.append("\nPickup: ").append(request.getLocation()).append("\nMedical assistance: ").append(request.getMedicalNeeds());
        text.append("\nNotes: ").append(request.getDescription()).append("\n\nShelter: ").append(shelter.getName()).append("\nAddress: ").append(shelter.getAddress());
        text.append("\n\nVolunteer to pickup (meters: ").append(toPickup.distanceMeters()).append("):\n");
        toPickup.instructions().forEach(step -> text.append("  ").append(step).append("\n"));
        text.append("\nPickup to shelter (meters: ").append(toShelter.distanceMeters()).append("):\n");
        toShelter.instructions().forEach(step -> text.append("  ").append(step).append("\n"));
        Files.createDirectories(directory);
        Path file = Files.createTempFile(directory, "trip-sheet-" + request.getRequestId() + "-", ".txt");
        Files.writeString(file, text, StandardCharsets.UTF_8);
        return file;
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

        VolunteerProfile volunteer = user.getVolunteerProfile();
        if (volunteer != null) {
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
        json.put(SHELTER_ADDRESS, shelter.getAddress());
        json.put(SHELTER_CAPACITY, shelter.getCapacity());
        json.put(SHELTER_OCCUPANCY, shelter.getOccupancy());
        json.put(SHELTER_STATUS, enumText(shelter.getStatus()));
        json.put(SHELTER_ACCOMMODATIONS, enumArray(shelter.getAccommodations()));
        json.put(SHELTER_LAST_UPDATED, String.valueOf(shelter.getLastUpdated()));
        json.put("petCapacity", shelter.getPetCapacity());
        JSONArray admissions = new JSONArray();
        for (ShelterAdmission admission : shelter.getAdmissions()) {
            JSONObject item = new JSONObject();
            item.put("requestId", admission.requestId().toString());
            item.put("people", peopleJSON(admission.people()));
            item.put("pets", petsJSON(admission.pets()));
            admissions.add(item);
        }
        json.put("admissions", admissions);
        JSONArray operators = new JSONArray();
        for (User user : shelter.getShelterOperators()) operators.add(user.getUsername());
        json.put(SHELTER_OPERATORS, operators);
        return json;
    }

    @SuppressWarnings("unchecked")
    private static JSONObject requestToJSON(ReliefRequest request) {
        JSONObject json = new JSONObject();
        json.put(REQUEST_ID, request.getRequestId().toString());
        json.put("familyMembers", peopleJSON(request.getFamilyMembers()));
        json.put("pets", petsJSON(request.getPets()));
        json.put("medicalNeeds", request.getMedicalNeeds());
        json.put("destinationShelterId", request.getDestinationShelterId() == null ? null : request.getDestinationShelterId().toString());
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
        VolunteerProfile assignedVolunteer = request.getAssignedVolunteer();
        json.put(REQUEST_ASSIGNED_VOLUNTEER, assignedVolunteer == null ? null : assignedVolunteer.getUsername());
        User submitter = request.getSubmittedBy();
        json.put(REQUEST_SUBMITTED_BY, submitter == null ? null : submitter.getUsername());
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
        return value == null ? null : value.name().toLowerCase(java.util.Locale.ROOT);
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
