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
 */
public class HurricaneReliefApp {
    private UserList userList;
    private ShelterList shelterList;
    private ReliefRequestCatalog reliefRequestCatalog;
    private ArrayList<HurricaneEvent> hurricanes;
    private User currentUser;

    public HurricaneReliefApp() {
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
     */
    public void saveData() {
        userList.save();
        shelterList.save();
        reliefRequestCatalog.save();
    }

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

    public ArrayList<Shelter> searchByProximity(String location, double maxDistance) {
        return shelterList.searchByProximity(location, maxDistance);
    }

    public void logout() {
        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        currentUser.logout();
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Creates an account through AccountFactory and adds it to UserList.
     * See AccountFactory for the keys details can hold.
     *
     * @return false if details are missing or the username is taken
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

    /** Creates an account from the original terminal UI fields. */
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
     */
    public List<Shelter> findNearestShelters(String location, double radiusMiles) {
        if ((location == null || location.trim().isEmpty()) && currentUser != null) {
            return currentUser.findNearestShelters();
        }
        return shelterList.searchByProximity(location, radiusMiles);
    }

    public void markSafe() {
        if (currentUser == null) {
            System.out.println("No user is currently logged in.");
            return;
        }
        currentUser.markSafe();
        System.out.println("Safety status updated to SAFE for user: " + currentUser.getUsername());
    }

    /**
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

    public List<HurricaneEvent> getHurricanes() {
        return hurricanes;
    }

    public List<ReliefRequest> getOpenRequestsByUrgency() {
        return reliefRequestCatalog.getOpenRequestsByUrgency();
    }

    public List<ReliefRequest> getOpenRequestsForVolunteers() {
        return reliefRequestCatalog.getOpenRequestsForVolunteers();
    }

    /**
     * Lets the logged in volunteer claim a request.
     *
     * @return false if the user is not a volunteer or cannot take the request
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

    public boolean canVerifyVolunteers() {
        return currentUser != null && (currentUser.hasRole(AccountRole.COORDINATOR) || currentUser.hasRole(AccountRole.SHELTER_OPERATOR));
    }

    public boolean completeRequest(UUID requestId) {
        if (currentUser == null || !(currentUser.getRoles().contains(AccountRole.VOLUNTEER))) {
            System.out.println("Only volunteers can complete requests.");
            return false;
        }
        ReliefRequest request = reliefRequestCatalog.getRequest(requestId);
        if (request == null) {
            System.out.println("Request not found.");
            return false;
        }
        VolunteerProfile volunteer = currentUser.getVolunteerProfile();
        if (volunteer == null) return false;
        if (!request.completeRequest(volunteer)) {
            System.out.println("You can only complete your own active assignments.");
            return false;
        }
        System.out.println("Request completed. Thank you for your help!");
        return true;
    }

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
    /** Includes request history. Legacy requests without an owner belong to nobody. */
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

}
