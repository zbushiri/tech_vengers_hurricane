package model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Loads current and legacy JSON using Jackson's typed JSON nodes.
 * 
 * @author Tavien Smith
 */
public class DataLoader extends DataConstants {
    private static final ObjectMapper JSON = new ObjectMapper();

    private DataLoader() { }

    private static BufferedReader getReaderFromFile(String fileName, String jsonFileName) {
        try {
            if (isJUnitTest()) {
                InputStream inputStream = DataLoader.class.getResourceAsStream(jsonFileName);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                return new BufferedReader(inputStreamReader);
            } else {
                FileReader reader = new FileReader(fileName);
                return new BufferedReader(reader);
            }
        } catch (Exception e) {
            System.out.println("Can't load");
            return null;
        }
    }

    private static JsonNode readArray(String file) throws IOException {
        JsonNode data = JSON.readTree(new File(file));
        if (data == null || !data.isArray()) throw new IOException("Expected a JSON array in " + file);
        return data;
    }

    private static String text(JsonNode node, String key) {
        return node.path(key).asText(null);
    }

    /**
     * Loads every user and volunteer from the users JSON file.
     * 
     * @return the shared UserList
     */
    public static UserList getUsers() {
        UserList users = UserList.getInstance();
        if (!users.getUsers().isEmpty()) return users;
        try {
            for (JsonNode row : readArray(USER_FILE)) {
                ArrayList<AccountRole> roles = new ArrayList<>();
                for (String role : strings(row.path(USER_ROLES))) {
                    try { roles.add(AccountRole.valueOf(role.toUpperCase(Locale.ROOT))); }
                    catch (IllegalArgumentException e) { /* Ignore obsolete role names. */ }
                }
                ArrayList<Person> people = new ArrayList<>();
                JsonNode peopleJSON = row.path(ASSOCIATED_PERSONS);
                if (peopleJSON.isArray()) {
                    for (JsonNode person : peopleJSON) {
                        ArrayList<String> needs = strings(person.path(PERSON_SPECIAL_NEEDS));
                        if (person.path(PERSON_SPECIAL_NEEDS).isTextual()
                                && !"none".equalsIgnoreCase(text(person, PERSON_SPECIAL_NEEDS))) {
                            needs.add(text(person, PERSON_SPECIAL_NEEDS));
                        }
                        people.add(new Person(text(person, PERSON_FIRST_NAME), text(person, PERSON_LAST_NAME),
                                person.path(PERSON_AGE).asInt(), needs));
                    }
                }
                JsonNode location = row.path(USER_LAST_KNOWN_LOCATION);
                String lastLocation = location.isObject()
                        ? location.path(LATITUDE).asText() + ", " + location.path(LONGITUDE).asText()
                        : location.asText(null);
                User user = new User(readID(row.path(USER_ID)), text(row, USER_FIRST_NAME),
                        text(row, USER_LAST_NAME), text(row, USER_EMAIL), text(row, USER_PHONE_NUMBER),
                        text(row, USER_ADDRESS), text(row, USER_USERNAME), text(row, USER_PASSWORD),
                        roles, people, enumValue(VerificationStatus.class, text(row, VERIFICATION_STATUS),
                                VerificationStatus.UNVERIFIED), lastLocation,
                        enumValue(SafetyStatusType.class, text(row, SAFETY_STATUS), SafetyStatusType.UNKNOWN),
                        dateTime(text(row, SAFETY_STATUS_UPDATED_AT)));
                if (user.hasRole(AccountRole.VOLUNTEER)) {
                    JsonNode transport = row.path(VOLUNTEER_TRANSPORTATION);
                    if (transport.isMissingNode() || transport.isNull()) transport = row.path(VOLUNTEER_TRANSPORTATION_OLD);
                    user = new VolunteerProfile(user, strings(row.path(VOLUNTEER_SKILLS)),
                            strings(row.path(VOLUNTEER_EQUIPMENT)),
                            enumValue(BackgroundCheckStatus.class, text(row, VOLUNTEER_BACKGROUND_CHECK),
                                    BackgroundCheckStatus.PENDING),
                            Boolean.parseBoolean(transport.asText()),
                            enumValue(AvailabilityStatus.class, text(row, VOLUNTEER_AVAILABILITY),
                                    AvailabilityStatus.AVAILABLE));
                }
                users.addUser(user);
            }
        } catch (Exception e) { throw new IllegalStateException("Could not load saved application data.", e); }
        return users;
    }

    /**
     * Loads every shelter from the shelters JSON file.
     * 
     * @return the shared ShelterList
     */
    public static ShelterList getShelters() {
        ShelterList shelters = ShelterList.getInstance();
        if (!shelters.getShelters().isEmpty()) return shelters;
        UserList users = getUsers();
        try {
            for (JsonNode row : readArray(SHELTER_FILE)) {
                ArrayList<Accommodation> accommodations = new ArrayList<>();
                for (String value : strings(row.path(SHELTER_ACCOMMODATIONS))) {
                    try { accommodations.add(Accommodation.valueOf(value.toUpperCase(Locale.ROOT))); }
                    catch (IllegalArgumentException e) { /* Ignore unknown accommodations. */ }
                }
                Shelter shelter = new Shelter(readID(row.path(SHELTER_ID)), text(row, SHELTER_NAME),
                        text(row, SHELTER_ADDRESS), row.path(SHELTER_CAPACITY).asInt(),
                        row.path(SHELTER_OCCUPANCY).asInt(),
                        enumValue(ShelterStatus.class, text(row, SHELTER_STATUS), ShelterStatus.OPEN), accommodations);
                shelter.setLastUpdated(dateTime(text(row, SHELTER_LAST_UPDATED)));
                ArrayList<User> operators = new ArrayList<>();
                for (String username : strings(row.path(SHELTER_OPERATORS))) {
                    User operator = users.getUser(username);
                    if (operator != null) operators.add(operator);
                }
                shelter.setShelterOperators(operators);
                shelter.setPetCapacity(row.path("petCapacity").asInt(0));
                ArrayList<ShelterAdmission> admissions = new ArrayList<>();
                for (JsonNode item : row.path("admissions")) {
                    admissions.add(new ShelterAdmission(UUID.fromString(item.path("requestId").asText()),
                            people(item.path("people")), pets(item.path("pets"))));
                }
                shelter.restoreAdmissions(admissions);
                shelters.addShelter(shelter);
            }
        } catch (Exception e) { throw new IllegalStateException("Could not load saved application data.", e); }
        return shelters;
    }

    /**
     * Loads every relief request from the requests JSON file.
     * 
     * @return the shared ReliefRequestCatalog
     */
    public static ReliefRequestCatalog getRequests() {
        ReliefRequestCatalog requests = ReliefRequestCatalog.getInstance();
        if (!requests.getRequests().isEmpty()) return requests;
        UserList users = getUsers();
        Map<ReliefRequest, String> duplicateIds = new HashMap<>();
        Map<String, ReliefRequest> bySavedId = new HashMap<>();
        try {
            for (JsonNode row : readArray(REQUEST_FILE)) {
                JsonNode storm = row.path(REQUEST_HURRICANE);
                ReliefRequest request = new ReliefRequest(readID(row.path(REQUEST_ID)),
                        RequestType.valueOf(text(row, REQUEST_TYPE).toUpperCase(Locale.ROOT)),
                        text(row, REQUEST_DESCRIPTION), text(row, REQUEST_LOCATION),
                        row.path(REQUEST_PEOPLE).asInt(), row.path(REQUEST_ANIMALS).asInt(),
                        text(row, REQUEST_ANIMAL_NOTES), text(row, REQUEST_PHOTO_URL),
                        row.path(REQUEST_IS_SUSPICIOUS).asBoolean(), null,
                        storm.isObject() ? hurricane(storm) : null);
                request.setSubmittedBy(users.getUser(text(row, REQUEST_SUBMITTED_BY)));
                User assigned = users.getUser(text(row, REQUEST_ASSIGNED_VOLUNTEER));
                if (assigned != null) request.setAssignedVolunteer(assigned.getVolunteerProfile());
                request.setFamilyMembers(people(row.path("familyMembers")));
                if (row.path("pets").isArray()) request.setPets(pets(row.path("pets")));
                request.setMedicalNeeds(row.path("medicalNeeds").asText(""));
                String destinationId = text(row, "destinationShelterId");
                if (destinationId != null) request.setDestinationShelterId(UUID.fromString(destinationId));
                request.restoreStatus(enumValue(RequestStatus.class, text(row, REQUEST_STATUS), RequestStatus.SUBMITTED));
                request.setPriority(enumValue(PriorityLevel.class, text(row, REQUEST_PRIORITY), request.calculatePriority()));
                String submitted = text(row, REQUEST_DATE_SUBMITTED);
                if (submitted != null) request.setDateSubmitted(parseDate(submitted));
                String original = text(row, REQUEST_DUPLICATE_OF);
                if (original != null) duplicateIds.put(request, original);
                bySavedId.put(row.path(REQUEST_ID).asText(), request);
                requests.addLoadedRequest(request);
            }
        } catch (Exception e) { throw new IllegalStateException("Could not load saved application data.", e); }
        for (Map.Entry<ReliefRequest, String> entry : duplicateIds.entrySet()) {
            ReliefRequest original = bySavedId.get(entry.getValue());
            if (original != entry.getKey()) entry.getKey().setIsDuplicateOf(original);
        }
        return requests;
    }

    /**
     * Loads every hurricane from the hurricanes JSON file.
     * 
     * @return the hurricanes, or an empty list if none load
     */
    public static ArrayList<HurricaneEvent> getHurricanes() {
        ArrayList<HurricaneEvent> storms = new ArrayList<>();
        try {
            for (JsonNode row : readArray(HURRICANE_FILE)) storms.add(hurricane(row));
        } catch (Exception e) { throw new IllegalStateException("Could not load saved application data.", e); }
        return storms;
    }

    /** Restores named family members and their documented special needs. */
    private static ArrayList<Person> people(JsonNode rows) {
        ArrayList<Person> result = new ArrayList<>();
        if (rows.isArray()) for (JsonNode row : rows) {
            result.add(new Person(text(row, PERSON_FIRST_NAME), text(row, PERSON_LAST_NAME),
                    row.path(PERSON_AGE).asInt(), strings(row.path(PERSON_SPECIAL_NEEDS))));
        }
        return result;
    }

    /** Restores pets separately from human occupants. */
    private static ArrayList<Pet> pets(JsonNode rows) {
        ArrayList<Pet> result = new ArrayList<>();
        if (rows.isArray()) for (JsonNode row : rows) result.add(new Pet(text(row, "name"), text(row, "species")));
        return result;
    }

    private static HurricaneEvent hurricane(JsonNode row) {
        return new HurricaneEvent(text(row, HURRICANE_NAME), parseDate(text(row, HURRICANE_START)),
                parseDate(text(row, HURRICANE_END)), strings(row.path(HURRICANE_AREAS)),
                enumValue(EventStatus.class, text(row, HURRICANE_STATUS), EventStatus.MONITORING));
    }

    private static ArrayList<String> strings(JsonNode node) {
        ArrayList<String> values = new ArrayList<>();
        if (node.isArray()) for (JsonNode value : node) values.add(value.asText());
        return values;
    }

    private static UUID readID(JsonNode value) {
        try { return UUID.fromString(value.asText()); }
        catch (IllegalArgumentException e) { return UUID.randomUUID(); }
    }

    private static <E extends Enum<E>> E enumValue(Class<E> type, String value, E fallback) {
        return value == null ? fallback : Enum.valueOf(type, value.toUpperCase(Locale.ROOT));
    }

    private static LocalDateTime dateTime(String text) {
        if (text == null) return LocalDateTime.now();
        if (text.endsWith("Z")) text = text.substring(0, text.length() - 1);
        return LocalDateTime.parse(text);
    }

    private static Date parseDate(String text) {
        if (text == null) return null;
        if (text.length() == 10) return Date.from(LocalDate.parse(text).atStartOfDay(ZoneOffset.UTC).toInstant());
        return Date.from(Instant.parse(text));
    }
}
