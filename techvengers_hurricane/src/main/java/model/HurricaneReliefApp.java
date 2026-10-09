package model;
import java.util.List;
public class HurricaneReliefApp {
    private UserList userList;
    private ReliefRequestCatalog reliefRequestCatalog;
    private ShelterList shelterList;
    private DataLoader dataLoader;
    private DataWriter dataWriter;
    private User currentUser;
    public HurricaneReliefApp() {
        userList = UserList.getInstance();
        reliefRequestCatalog = ReliefRequestCatalog.getInstance();
        shelterList = ShelterList.getInstance();
        currentUser = null;
    }
    public void initialize() {
        userList.getUsers();
        if (currentUser.getVerificationStatus() == VerificationStatus.VERIFIED) {
            reliefRequestCatalog = ReliefRequestCatalog.getInstance();
        }
        shelterList = ShelterList.getInstance();
        currentUser = null;
    }
    public void run() {
        // Main application logic goes here
    }
    public void loadData() {
        dataLoader.getUsers();
        if (currentUser.getVerificationStatus() == VerificationStatus.VERIFIED) {
            dataLoader.getRequests();
        }
        dataLoader.getHurricanes();
        System.out.println("Data loaded successfully.");
    }
    public void saveData() {
        dataWriter.saveUsers(userList.getUsers());
        if (currentUser.getVerificationStatus() == VerificationStatus.VERIFIED) {
            dataWriter.saveRequests(reliefRequestCatalog.getOpenRequestsForVolunteers());
        }
    }
    public boolean login(String username, String password) {
        for (User user : userList.getUsers()) {
            if (user.getUsername().equals(username) && user.getPasswordHash().equals(password)) {
                System.out.println("Login successful for user: " + username);
                currentUser = user;
                return true;
            }
        }
        System.out.println("Incorrect username or password. Please try again.");
        return false;
    }
    public void logout() {
        if (currentUser != null) {
            System.out.println("User " + currentUser.getUsername() + " logged out.");
            currentUser = null;
        } else {
            System.out.println("No user is currently logged in.");
        }
    }
    public boolean createAccount(AccountRole role, Map details, String username = "", String password = "") {
        for (User user : userList.getUsers()) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists. Please choose a different username.");
                return false;
            }
        }
        User newUser = new User(username, password);
        userList.addUser(newUser);
        System.out.println("Account created successfully for user: " + username);
        return true;
    }
   public boolean submitReliefRequest(RequestType type, String location, String description, boolean forVolunteers) {
        if (currentUser != null && currentUser.getVerificationStatus() == VerificationStatus.VERIFIED) {
            reliefRequestCatalog.submitRequest(new ReliefRequest(type, location, description, forVolunteers));
            System.out.println("Relief request submitted successfully.");
            return true;
        } else {
            System.out.println("You must be logged in and verified to submit a relief request.");
            return false;
        }
    }
   public List<Shelter> findNearestShelters(String location, double radiusMiles) {
        return shelterList.searchByProximity(location, radiusMiles);
    }
    public void markSafe() {
        if (currentUser != null) {
            currentUser.markSafe();
            System.out.println("Safety status updated to SAFE for user: " + currentUser.getUsername());
        } else {
            System.out.println("No user is currently logged in.");
        }
    }
    public HurricaneEvent getActiveHurricane() {
        for (HurricaneEvent hurricane : dataLoader.getHurricanes()) {
            if (hurricane.getStatus() == EventStatus.ACTIVE) {
                return hurricane;
            }
        }
        System.out.println("No active hurricane events at the moment.");
        return null;
    }
    public static void main(String[] args) {
        HurricaneReliefApp app = new HurricaneReliefApp();
        app.initialize();
        app.loadData();
        // Example usage
        app.login("testUser", "testPassword");
        app.markSafe();
        HurricaneEvent activeHurricane = app.getActiveHurricane();
        if (activeHurricane != null) {
            System.out.println("Active Hurricane: " + activeHurricane.getName());
        }
        app.logout();
        app.saveData();
    }
}