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
     * Returns the shared user list.
     * @return the shared instance, created on first access
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
     * Finds an account by username, checking its password when supplied.
     * @param username account username, matched without regard to case
     * @return matching account, or null when the account or supplied credentials do not match
     */
    public User getUser(String username) {
        if (username == null) return null;
        for (User user : users) {
            if (username.equalsIgnoreCase(user.getUsername())) return user;
        }
        return null;
    }

    /**
     * Finds an account by username, checking its password when supplied.
     * @param username account username, matched without regard to case
     * @param password password text compared directly with the stored value; no hashing is performed
     * @return matching account, or null when the account or supplied credentials do not match
     */
    public User getUser(String username, String password) {
        User user = getUser(username);
        return user != null && user.login(username, password) ? user : null;
    }

    /**
     * Finds the account whose username and password match the supplied credentials.
     * @param username account username, matched without regard to case
     * @param password password text compared directly with the stored value; no hashing is performed
     * @return matching account, or null if authentication fails
     */
    public User authenticate(String username, String password) { return getUser(username, password); }

    /**
     * Adds an account only when its username is not already registered.
     * @param user account to add to the shared list
     * @return true if the account was added; false if it is invalid or its username is taken
     */
    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || getUser(user.getUsername()) != null) return false;
        return users.add(user);
    }

    /**
     * Creates and adds an account; volunteer roles receive a pending background-check profile.
     * @param firstName account holder's first name
     * @param lastName account holder's last name
     * @param email contact email address
     * @param phoneNumber contact telephone number
     * @param address account holder's street address
     * @param username account username, matched without regard to case
     * @param password password text compared directly with the stored value; no hashing is performed
     * @param roles account roles that determine available actions
     * @param lastKnownLocation most recently reported location or address
     * @return true if the account was added; false if it is invalid or its username is taken
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
     * Saves all accounts in the shared user list to JSON.
     * @return true if the users JSON file was written successfully
     */
    public boolean save() { return DataWriter.saveUsers(users); }
}
