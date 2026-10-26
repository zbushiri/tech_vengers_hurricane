package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The facade for the Gamecock Relief Network. A UI only needs this class:
 * it loads and saves the data, keeps track of who is logged in, and passes
 * each action on to UserList, ShelterList and ReliefRequestCatalog.
 * 
 * DataLoader and DataWriter are used through their static methods, since
 * both are utility classes that cannot be created with new.
 * @author Team Tech-vengers
 */
public class HurricaneReliefApp {
    private UserList userList;
    private ShelterList shelterList;
    private ReliefRequestCatalog reliefRequestCatalog;
    private ArrayList<HurricaneEvent> hurricanes;
    private User currentUser;
    private final location.RoutingService routes;

    /**
     * Creates hurricane relief app.
     */
    public HurricaneReliefApp() { this(location.MapTools.getInstance()); }

    /**
     * Creates the facade with an interchangeable driving-route provider.
     * @param routes driving distances and directions for matching and exports
     */
    public HurricaneReliefApp(location.RoutingService routes) {
        this.routes = java.util.Objects.requireNonNull(routes);
        userList = UserList.getInstance();
        shelterList = ShelterList.getInstance();
        reliefRequestCatalog = ReliefRequestCatalog.getInstance();
        hurricanes = new ArrayList<>();
        currentUser = null;
    }

    /**
     * Gets the app ready to use by loading everything from the JSON files.
     */
    public void initialize() {
        loadData();
    }

    /**
     * Reads users, shelters, requests and hurricanes from the JSON files.
     * Users load first so shelters can find their operators. Anything that is
     * already loaded is skipped, so calling this twice is safe.
     */
    public void loadData() {
        userList = DataLoader.getUsers();
        shelterList = DataLoader.getShelters();
        reliefRequestCatalog = DataLoader.getRequests();
        hurricanes = DataLoader.getHurricanes();
        System.out.println("Data loaded: " + userList.getUsers().size() + " users, "
                + shelterList.getShelters().size() + " shelters, "
                + reliefRequestCatalog.getRequests().size() + " requests, "
                + hurricanes.size() + " hurricanes.");
    }

    /**
     * Writes users, shelters and requests back to the JSON files.
     * @return true when the operation succeeds
     */
    public boolean saveData() {
        boolean usersSaved = userList.save();
        boolean sheltersSaved = shelterList.save();
        boolean requestsSaved = reliefRequestCatalog.save();
        return usersSaved && sheltersSaved && requestsSaved;
    }

    /**
     * Login.
     * @param username username for this operation
     * @param password password for this operation
     * @return true when the operation succeeds
     */
    public boolean login(String username, String password) {
        User user = userList.getUser(username, password);
        if (user == null) {
            System.out.println("Incorrect username or password. Please try again.");
            return false;
        }
        currentUser = user;
        System.out.println("Login successful for user: " + user.getUsername());
        return true;
    }

    /**
     * Search by proximity.
     * @param location location for this operation
     * @param maxDistance max distance for this operation
     * @return the result of search by proximity
     */
    public ArrayList<Shelter> searchByProximity(String location, double maxDistance) {
        return shelterList.searchByProximity(location, maxDistance);
    }

    /**
     * Logout.
     */
    public void logout() {
        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        currentUser.logout();
        currentUser = null;
    }

    /**
     * Returns the current user.
     * @return the current user
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Is logged in.
     * @return whether is logged in
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Creates an account through AccountFactory and adds it to UserList.
     * See AccountFactory for the keys details can hold.
     * 
     * @return false if details are missing or the username is taken
     * @param role role for this operation
     * @param details details for this operation
     */
    public boolean createAccount(AccountRole role, Map<String, ?> details) {
        User user = AccountFactory.createAccount(role, details);
        if (user == null) {
            System.out.println("A role, username and password are required.");
            return false;
        }
        if (!userList.addUser(user)) {
            System.out.println("Username already exists. Please choose a different username.");
            return false;
        }
        System.out.println("Account created successfully for user: " + user.getUsername());
        return true;
    }

    /**
     * Creates an account from the original terminal UI fields.
     * @param firstName first name for this operation
     * @param lastName last name for this operation
     * @param email email for this operation
     * @param phoneNumber phone number for this operation
     * @param address address for this operation
     * @param username username for this operation
     * @param password password for this operation
     * @param roles roles for this operation
     * @param lastKnownLocation last known location for this operation
     * @return true when the operation succeeds
     */
    public boolean createAccount(String firstName, String lastName, String email,
            String phoneNumber, String address, String username, String password,
            ArrayList<AccountRole> roles, String lastKnownLocation) {
        return userList.addUser(firstName, lastName, email, phoneNumber, address,
                username, password, roles, lastKnownLocation);
    }

    /**
     * Submits a request for the logged in user. The catalog checks for
     * duplicates at the same location and sets the priority. The request is
     * linked to the active hurricane, if there is one.
     * 
     * @return false if nobody is logged in or the details are missing
     * @param type type for this operation
     * @param description description for this operation
     * @param location location for this operation
     * @param numberOfPeopleNeeded number of people needed for this operation
     */
    public boolean submitReliefRequest(RequestType type, String description, String location,
                                       int numberOfPeopleNeeded) {
        if (currentUser == null) {
            System.out.println("You must be logged in to submit a relief request.");
            return false;
        }
        if (type == null || description == null || description.trim().isEmpty() || location == null || location.trim().isEmpty() || numberOfPeopleNeeded < 1) {
            System.out.println("A request needs a type, a location and at least one person.");
            return false;
        }
        ReliefRequest request = new ReliefRequest(type, description, location.trim(), numberOfPeopleNeeded);
        request.setSubmittedBy(currentUser);
        request.setForHurricane(getActiveHurricane());
        currentUser.submitReliefRequest(request);
        System.out.println("Relief request submitted with " + request.getPriority() + " priority.");
        if (request.getIsDuplicateOf() != null) {
            System.out.println("A similar request at this location is already open.");
        }
        return true;
    }

    /**
     * Finds shelters near a location. With no location, the logged in
     * user's own location or address is used.
     * @param location location for this operation
     * @param radiusMiles radius miles for this operation
     * @return the result of find nearest shelters
     */
    public List<Shelter> findNearestShelters(String location, double radiusMiles) {
        if ((location == null || location.trim().isEmpty()) && currentUser != null) {
            return currentUser.findNearestShelters();
        }
        return shelterList.searchByProximity(location, radiusMiles);
    }

    
    /**
     * Mark safe.
     */
    public void markSafe() {
        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        currentUser.markSafe();
        System.out.println("Safety status updated to SAFE for user: " + currentUser.getUsername());
    }

    /**
     * Returns the active hurricane.
     * @return the first ACTIVE hurricane, or null if none is active
     */
    public HurricaneEvent getActiveHurricane() {
        for (HurricaneEvent hurricane : hurricanes) {
            if (hurricane.getStatus() == EventStatus.ACTIVE) {
                return hurricane;
            }
        }
        return null;
    }

    /**
     * Returns the hurricanes.
     * @return the hurricanes
     */
    public List<HurricaneEvent> getHurricanes() {
        return hurricanes;
    }

    /**
     * Returns the open requests by urgency.
     * @return the open requests by urgency
     */
    public List<ReliefRequest> getOpenRequestsByUrgency() {
        return reliefRequestCatalog.getOpenRequestsByUrgency();
    }

    /**
     * Returns the open requests for volunteers.
     * @return the open requests for volunteers
     */
    public List<ReliefRequest> getOpenRequestsForVolunteers() {
        return reliefRequestCatalog.getOpenRequestsForVolunteers();
    }

    /**
     * Lets the logged in volunteer claim a request.
     * 
     * @return false if the user is not a volunteer or cannot take the request
     * @param requestId request id for this operation
     */
    public boolean claimRequest(UUID requestId) {
        if (currentUser == null || !(currentUser.getRoles().contains(AccountRole.VOLUNTEER))) {
            System.out.println("Only volunteers can claim requests.");
            return false;
        }
        VolunteerProfile volunteer = currentUser.getVolunteerProfile();
        if (volunteer == null) return false;
        if (!volunteer.canClaimRequests()) {
            System.out.println("You must be available and have a verified background check to claim requests.");
            return false;
        }
        if (!reliefRequestCatalog.claimRequest(requestId, volunteer)) {
            System.out.println("That request is no longer open.");
            return false;
        }
        System.out.println("Request claimed. Thank you for helping!");
        return true;
    }

    /**
     * Marks a volunteer's background check as verified so they can claim
     * requests. Only a coordinator or shelter operator may do this.
     * 
     * @return false if the user may not verify or the volunteer was not found
     * @param username username for this operation
     */
    public boolean verifyVolunteer(String username) {
        if (currentUser == null || !canVerifyVolunteers()) {
            System.out.println("Only coordinators and shelter operators can verify volunteers.");
            return false;
        }
        User user = userList.getUser(username);
        if (user == null || !(user.getRoles().contains(AccountRole.VOLUNTEER))) {
            System.out.println("No volunteer was found with that username.");
            return false;
        }
        VolunteerProfile volunteer = user.getVolunteerProfile();
        if (volunteer == null) return false;
        volunteer.setBackgroundCheckStatus(BackgroundCheckStatus.VERIFIED);
        System.out.println("Background check verified for " + user.getUsername() + ".");
        return true;
    }

    /**
     * Can verify volunteers.
     * @return whether can verify volunteers
     */
    public boolean canVerifyVolunteers() {
        return currentUser != null && (currentUser.hasRole(AccountRole.COORDINATOR) || currentUser.hasRole(AccountRole.SHELTER_OPERATOR));
    }

    /**
     * Complete request.
     * @param requestId request id for this operation
     * @return true when the operation succeeds
     */
    public boolean completeRequest(UUID requestId) {
        if (currentUser == null || (!currentUser.hasRole(AccountRole.SHELTER_OPERATOR)
                && !currentUser.hasRole(AccountRole.COORDINATOR))) {
            System.out.println("Your volunteer role does not have permission to complete relief requests. A shelter manager must complete the request after check-in.");
            return false;
        }
        ReliefRequest request = reliefRequestCatalog.getRequest(requestId);
        if (request == null || !request.completeRequest(currentUser)) {
            System.out.println("Only the destination shelter's manager may complete a checked-in assignment (or a coordinator).");
            return false;
        }
        System.out.println("Request Complete (saved status: " + request.getStatus() + ").");
        return true;
    }

    /**
     * Returns the my assignments.
     * @return the my assignments
     */
    public ArrayList<ReliefRequest> getMyAssignments() {
        ArrayList<ReliefRequest> myAssignments = new ArrayList<>();
        if (currentUser == null || !(currentUser.getRoles().contains(AccountRole.VOLUNTEER))) {
            return myAssignments;
        }


        for (ReliefRequest request : reliefRequestCatalog.getRequests()) {
              VolunteerProfile volunteer = request.getAssignedVolunteer();
            if (volunteer != null &&
                volunteer.getUserId().equals(currentUser.getUserId()) &&
                (request.getStatus() == RequestStatus.ASSIGNED
                || request.getStatus() == RequestStatus.IN_PROGRESS)) {
                myAssignments.add(request);
            }
        }
        return myAssignments;
    }
    /**
     * Includes request history. Legacy requests without an owner belong to nobody.
     * @return the my requests
     */
    public ArrayList<ReliefRequest> getMyRequests() {
        ArrayList<ReliefRequest> result = new ArrayList<>();
        if (currentUser == null) return result;
        for (ReliefRequest request : reliefRequestCatalog.getRequests()) {
            User owner = request.getSubmittedBy();
            if (owner != null && owner.getUserId().equals(currentUser.getUserId())) {
                result.add(request);
            }
        }
        return result;
    }

    /**
     * Cancel request.
     * @param requestId request id for this operation
     * @return whether cancel request
     */
    public boolean cancelRequest(UUID requestId) {
        if (currentUser == null) return false;
        ReliefRequest request = reliefRequestCatalog.getRequest(requestId);
        if (request == null || request.getSubmittedBy() == null
                || !request.getSubmittedBy().getUserId().equals(currentUser.getUserId())
                || !request.isOpen()) {
            System.out.println("You can only cancel your own open requests.");
            return false;
        }
        request.cancel();
        System.out.println("Request cancelled.");
        return true;
    }

    /**
     * Start request.
     * @param requestId request id for this operation
     * @return true when the operation succeeds
     */
    public boolean startRequest(UUID requestId) {
        if (currentUser == null) return false;
        VolunteerProfile volunteer = currentUser.getVolunteerProfile();
        ReliefRequest request = reliefRequestCatalog.getRequest(requestId);
        if (volunteer == null || request == null || !request.startRequest(volunteer)) {
            System.out.println("You can only start your own assigned requests.");
            return false;
        }
        System.out.println("Request is now in progress.");
        return true;
    }

    /**
     * Submits a named household with explicit medical needs and accompanying pets.
     * @param type kind of help
     * @param description household situation
     * @param location pickup address
     * @param people household members including the account holder
     * @param pets accompanying animals
     * @param medicalNeeds medical assistance required
     * @return created request, or null if validation or authentication fails
     */
    public ReliefRequest submitHouseholdRequest(RequestType type, String description, String location,
            List<Person> people, List<Pet> pets, String medicalNeeds) {
        if (currentUser == null || type == null || description == null || description.isBlank()
                || location == null || !validAddress(location) || people == null || people.isEmpty() || pets == null
                || people.stream().anyMatch(p -> p == null || p.getAge() < 0 || p.getAge() > 120
                        || p.getFirstName() == null || p.getFirstName().isBlank()
                        || p.getLastName() == null || p.getLastName().isBlank()) || pets.stream().anyMatch(java.util.Objects::isNull)) return null;
        ReliefRequest request = new ReliefRequest(type, description, location.trim(), people.size());
        request.setFamilyMembers(people);
        request.setPets(pets);
        request.setMedicalNeeds(medicalNeeds);
        request.setForHurricane(getActiveHurricane());
        currentUser.setAssociatedPersons(new ArrayList<>(people));
        currentUser.submitReliefRequest(request);
        return request;
    }

    /**
     * Ranks open requests by nearby specialty match, then priority and distance.
     * Already assigned work remains visible with the assignee's name in the UI.
     * @return explained recommendations for the current volunteer
     */
    public List<RequestRecommendation> getRecommendedRequests() {
        if (currentUser == null || currentUser.getVolunteerProfile() == null) return List.of();
        VolunteerProfile volunteer = currentUser.getVolunteerProfile();
        String departure = volunteer.getLastKnownLocation();
        if (departure == null || departure.isBlank()) departure = volunteer.getAddress();
        List<RequestRecommendation> result = new ArrayList<>();
        for (ReliefRequest request : reliefRequestCatalog.getOpenRequestsByUrgency()) {
            boolean specialty = volunteer.getSkills().stream().anyMatch(skill -> skill.equalsIgnoreCase(request.getType().name()));
            double distance = Double.POSITIVE_INFINITY;
            String explanation;
            try {
                distance = routes.route(location.Address.fromString(departure), location.Address.fromString(request.getLocation())).distanceMeters();
                boolean nearby = distance <= 25 * 1609.344;
                explanation = specialty && nearby ? String.format(java.util.Locale.ROOT,
                        "Matches your %s specialty; nearby at %.1f driving miles.", request.getType().name().toLowerCase(java.util.Locale.ROOT), distance / 1609.344)
                        : "No nearby specialty match.";
            } catch (java.io.IOException e) { explanation = "Driving distance unavailable; proximity cannot be verified."; }
            boolean recommended = specialty && distance <= 25 * 1609.344 && request.getStatus() == RequestStatus.SUBMITTED
                    && request.isForVolunteers() && !request.hasOpenOriginal();
            result.add(new RequestRecommendation(request, recommended, explanation, distance));
        }
        result.sort(java.util.Comparator.comparing(RequestRecommendation::recommended).reversed()
                .thenComparing(r -> r.request().getPriority(), java.util.Comparator.reverseOrder())
                .thenComparingDouble(RequestRecommendation::distanceMeters)
                .thenComparing(r -> r.request().getDateSubmitted()));
        return result;
    }

    /**
     * Finds the closest shelter by driving distance with room and required services.
     * @param request household needing shelter
     * @return closest eligible shelter, or null if none has room
     * @throws java.io.IOException if routes cannot be compared
     */
    public Shelter findShelter(ReliefRequest request) throws java.io.IOException {
        ArrayList<Accommodation> needs = new ArrayList<>();
        if (request.getNumberOfAnimals() > 0) needs.add(Accommodation.PET_FRIENDLY);
        if (!request.getMedicalNeeds().isBlank()) needs.add(Accommodation.MEDICAL_SUPPORT);
        return shelterList.findClosestShelter(location.Address.fromString(request.getLocation()), needs,
                request.getNumberOfPeopleNeeded(), request.getNumberOfAnimals(), routes);
    }

    /**
     * Selects a suitable shelter for the current volunteer's assignment.
     * @param requestId request being transported
     * @param shelter destination with room and required services
     * @return true if the destination was set
     */
    public boolean selectShelter(UUID requestId, Shelter shelter) {
        ReliefRequest request = reliefRequestCatalog.getRequest(requestId);
        if (request == null || currentUser == null || shelter == null || !shelterList.getShelters().contains(shelter)
                || request.getAssignedVolunteer() == null
                || !request.getAssignedVolunteer().getUserId().equals(currentUser.getUserId())
                || !request.isOpen() || !shelter.canAccommodate(request)) return false;
        if (shelterList.getShelters().stream().anyMatch(s -> s.hasAdmission(requestId))) return false;
        request.setDestinationShelterId(shelter.getShelterId());
        return true;
    }

    /**
     * Exports both trip legs for the signed-in volunteer's selected assignment.
     * @param requestId assigned household request
     * @param directory folder for uniquely named trip sheets
     * @return new UTF-8 text file
     * @throws java.io.IOException if routes or the output file are unavailable
     */
    public java.nio.file.Path exportTripSheet(UUID requestId, java.nio.file.Path directory) throws java.io.IOException {
        ReliefRequest request = reliefRequestCatalog.getRequest(requestId);
        if (request == null || currentUser == null || request.getAssignedVolunteer() == null
                || !request.getAssignedVolunteer().getUserId().equals(currentUser.getUserId())) {
            throw new IllegalStateException("Only the assigned volunteer can export this trip.");
        }
        Shelter shelter = shelterList.getShelters().stream().filter(s -> s.getShelterId().equals(request.getDestinationShelterId()))
                .findFirst().orElseThrow(() -> new IllegalStateException("Select a shelter first."));
        String departure = currentUser.getLastKnownLocation();
        if (departure == null || departure.isBlank()) departure = currentUser.getAddress();
        location.Address pickup = location.Address.fromString(request.getLocation());
        return DataWriter.saveTripSheet(request, shelter, routes.route(location.Address.fromString(departure), pickup),
                routes.route(pickup, location.Address.fromString(shelter.getAddress())), directory);
    }

    /**
     * Returns the managed shelters.
     * @return shelters managed by the current user, or all shelters for a coordinator
     */
    public List<Shelter> getManagedShelters() {
        if (currentUser == null) return List.of();
        return shelterList.getShelters().stream().filter(s -> currentUser.hasRole(AccountRole.COORDINATOR)
                || (currentUser.hasRole(AccountRole.SHELTER_OPERATOR) && s.isShelterOperator(currentUser))).toList();
    }

    /**
     * Check in.
     * @param requestId household request
     * @param shelter destination shelter
     * @return whether the authorized manager checked the household in
     */
    public boolean checkIn(UUID requestId, Shelter shelter) {
        return shelter != null && getManagedShelters().contains(shelter)
                && shelter.checkIn(reliefRequestCatalog.getRequest(requestId), currentUser);
    }

    /** Checks full pickup-address syntax before accepting household details. */
    private static boolean validAddress(String text) { return location.Address.fromString(text) != null; }

}
