package model;

import java.util.ArrayList;

/**
 * Stores all user accounts.
 * @author Team Tech-vengers
 */
public class UserList {
    private static UserList instance;
    private final ArrayList<User> users = new ArrayList<>();

    private UserList() { }
    /**
     * Returns the instance.
     * @return the instance
     */
    public static UserList getInstance() {
        if (instance == null) instance = new UserList();
        return instance;
    }
    /**
     * Returns the users.
     * @return the users
     */
    public ArrayList<User> getUsers() { return users; }

    /**
     * Returns the user.
     * @param username username for this operation
     * @return the user
     */
    public User getUser(String username) {
        if (username == null) return null;
        for (User user : users) {
            if (username.equalsIgnoreCase(user.getUsername())) return user;
        }
        return null;
    }

    /**
     * Returns the user.
     * @param username username for this operation
     * @param password password for this operation
     * @return the user
     */
    public User getUser(String username, String password) {
        User user = getUser(username);
        return user != null && user.login(username, password) ? user : null;
    }

    /**
     * Authenticate.
     * @param username username for this operation
     * @param password password for this operation
     * @return the result of authenticate
     */
    public User authenticate(String username, String password) { return getUser(username, password); }

    /**
     * Add user.
     * @param user user for this operation
     * @return true when the operation succeeds
     */
    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || getUser(user.getUsername()) != null) return false;
        return users.add(user);
    }

    /**
     * Add user.
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
    public boolean addUser(String firstName, String lastName, String email, String phoneNumber,
            String address, String username, String password, ArrayList<AccountRole> roles,
            String lastKnownLocation) {

        User user = new User(firstName, lastName, email, phoneNumber, address,
                username, password, roles, lastKnownLocation);

        if(user.hasRole(AccountRole.VOLUNTEER)) {
            user = new VolunteerProfile(user, new ArrayList<String>(), new ArrayList<String>(),
                                        BackgroundCheckStatus.PENDING, false,
                                        AvailabilityStatus.AVAILABLE);
        }
        return addUser(user);
    }

    /**
     * Save.
     * @return true when the operation succeeds
     */
    public boolean save() { return DataWriter.saveUsers(users); }
}
