package model;

import java.util.ArrayList;
<<<<<<< HEAD
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
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
    private Scanner scanner;

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
        for (User user : DataLoader.getUsers()) {
            userList.addUser(user);
        }
        for (Shelter shelter : DataLoader.getShelters()) {
            shelterList.addShelter(shelter);
        }
        for (ReliefRequest request : DataLoader.getRequests()) {
            reliefRequestCatalog.addLoadedRequest(request);
        }
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
        if (type == null || location == null || location.trim().isEmpty() || numberOfPeopleNeeded < 1) {
            System.out.println("A request needs a type, a location and at least one person.");
            return false;
        }
        ReliefRequest request = new ReliefRequest(type, description, location.trim(), numberOfPeopleNeeded);
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
        if (!(currentUser instanceof VolunteerProfile)) {
            System.out.println("Only volunteers can claim requests.");
            return false;
        }
        VolunteerProfile volunteer = (VolunteerProfile) currentUser;
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

    // ---- Terminal menus ----

    /**
     * Runs the terminal app until the user exits, then saves.
     */
    public void run() {
        scanner = new Scanner(System.in);
        System.out.println("Welcome to the Gamecock Relief Network");

        boolean running = true;
        while (running) {
            running = currentUser == null ? startMenu() : homeMenu();
        }

        if (currentUser != null) {
            logout();
        }
        saveData();
        System.out.println("Goodbye!");
    }

    private boolean startMenu() {
        System.out.println();
        System.out.println("1. Log in");
        System.out.println("2. Create an account");
        System.out.println("3. Find shelters");
        System.out.println("4. Exit");
        String choice = prompt("Choose an option: ");

        if (choice == null || choice.equals("4")) {
            return false;
        } else if (choice.equals("1")) {
            login(prompt("Username: "), prompt("Password: "));
        } else if (choice.equals("2")) {
            createAccountMenu();
        } else if (choice.equals("3")) {
            showShelters(findNearestShelters(prompt("Town or address: "), ShelterList.DEFAULT_RADIUS_MILES));
        } else {
            System.out.println("Please choose 1 to 4.");
        }
        return true;
    }

    private boolean homeMenu() {
        System.out.println();
        System.out.println("Logged in as " + currentUser.getUsername());
        System.out.println("1. Submit a relief request");
        System.out.println("2. Find shelters near me");
        System.out.println("3. Mark myself safe");
        System.out.println("4. Hurricane information");
        System.out.println("5. View open requests");
        System.out.println("6. Log out");
        System.out.println("7. Exit");
        String choice = prompt("Choose an option: ");

        if (choice == null || choice.equals("7")) {
            return false;
        } else if (choice.equals("1")) {
            submitRequestMenu();
        } else if (choice.equals("2")) {
            showShelters(findNearestShelters(null, ShelterList.DEFAULT_RADIUS_MILES));
        } else if (choice.equals("3")) {
            markSafe();
        } else if (choice.equals("4")) {
            showHurricane();
        } else if (choice.equals("5")) {
            openRequestsMenu();
        } else if (choice.equals("6")) {
            logout();
        } else {
            System.out.println("Please choose 1 to 7.");
        }
        return true;
    }

    private void createAccountMenu() {
        Map<String, String> details = new HashMap<>();
        details.put("username", prompt("Choose a username: "));
        details.put("password", prompt("Choose a password: "));
        details.put("firstName", prompt("First name: "));
        details.put("lastName", prompt("Last name: "));
        details.put("phoneNumber", prompt("Phone number: "));
        details.put("email", prompt("Email: "));
        details.put("address", prompt("Address: "));
        String roleChoice = prompt("Are you a 1. Resident or 2. Volunteer? ");

        AccountRole role = "2".equals(roleChoice) ? AccountRole.VOLUNTEER : AccountRole.RESIDENT;
        if (role == AccountRole.VOLUNTEER) {
            details.put("skills", prompt("Skills (comma separated): "));
            details.put("equipment", prompt("Equipment (comma separated): "));
            details.put("transportationAccess", String.valueOf("y".equalsIgnoreCase(prompt("Do you have transportation? (y/n) "))));
        }
        if (createAccount(role, details)) {
            login(details.get("username"), details.get("password"));
        }
    }

    private void submitRequestMenu() {
        RequestType[] types = RequestType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i]);
        }
        int typeChoice = number(prompt("What do you need? "));
        if (typeChoice < 1 || typeChoice > types.length) {
            System.out.println("Please choose one of the listed types.");
            return;
        }
        String description = prompt("Describe what you need: ");
        String location = prompt("Location: ");
        int people = number(prompt("How many people need help? "));
        submitReliefRequest(types[typeChoice - 1], description, location, people);
    }

    private void openRequestsMenu() {
        boolean isVolunteer = currentUser instanceof VolunteerProfile;
        List<ReliefRequest> requests = isVolunteer ? getOpenRequestsForVolunteers() : getOpenRequestsByUrgency();
        if (requests.isEmpty()) {
            System.out.println("There are no open requests right now.");
            return;
        }
        for (int i = 0; i < requests.size(); i++) {
            ReliefRequest request = requests.get(i);
            System.out.println((i + 1) + ". [" + request.getPriority() + "] " + request.getType()
                    + " at " + request.getLocation() + " for " + request.getNumberOfPeopleNeeded()
                    + " people (" + request.getStatus() + ")");
        }
        if (!isVolunteer) {
            return;
        }
        int choice = number(prompt("Enter a number to claim a request, or press Enter to go back: "));
        if (choice >= 1 && choice <= requests.size()) {
            claimRequest(requests.get(choice - 1).getRequestId());
        }
    }

    private void showShelters(List<Shelter> shelters) {
        if (shelters.isEmpty()) {
            System.out.println("No open shelters were found near that location.");
            return;
        }
        for (Shelter shelter : shelters) {
            System.out.println(shelter.getName() + " - " + shelter.getAddress() + " ("
                    + shelter.getStatus() + ", " + shelter.getOccupancy() + "/" + shelter.getCapacity()
                    + ", " + shelter.getAccommodations() + ")");
        }
    }

    private void showHurricane() {
        HurricaneEvent active = getActiveHurricane();
        if (active != null) {
            User.getLatestInfo(active);
            return;
        }
        System.out.println("No active hurricane events at the moment.");
        for (HurricaneEvent hurricane : hurricanes) {
            System.out.println(hurricane.getName() + " (" + hurricane.getStatus() + ") "
                    + hurricane.getAffectedAreas());
        }
    }

    /**
     * Prints a prompt and reads one line.
     *
     * @return the trimmed line, or null if there is no more input
     */
    private String prompt(String message) {
        System.out.print(message);
        if (!scanner.hasNextLine()) {
            System.out.println();
            return null;
        }
        return scanner.nextLine().trim();
    }

    private static int number(String text) {
        try {
            return text == null ? -1 : Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        HurricaneReliefApp app = new HurricaneReliefApp();
        app.initialize();
        app.run();
=======

public class HurricaneReliefApp {
    private UserList userList;
   // private ShelterList shelterList;
    private ReliefRequestCatalog reliefRequestCatalog;
    private User currentUser;

    public HurricaneReliefApp() {
        userList = UserList.getInstance();
        //shelterList = ShelterList.getInstance();
        reliefRequestCatalog = ReliefRequestCatalog.getInstance();
        loadData();
    }

    private void loadData() {
        DataLoader.getUsers();
    }

    public boolean login(String username, String password) {
        currentUser = userList.authenticate(username, password);
        return currentUser != null;
    }

    public void createAccount(String firstName, String lastName, String email, String phoneNumber, String address,
        String username, String password, ArrayList<AccountRole> roles,String lastKnownLocation) {
        userList.addUser(firstName, lastName, email, phoneNumber, address, username, password, roles, lastKnownLocation);
    }

    public boolean submitReliefRequest(ReliefRequest request) {
        if (currentUser == null) {
            return false;
        }
        reliefRequestCatalog.submitRequest(request);
        return true;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
    }
}
