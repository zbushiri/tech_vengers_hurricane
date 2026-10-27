package model;

import java.util.ArrayList;

/** Stores all user accounts. */
public class UserList {
    private static UserList instance;
    private final ArrayList<User> users = new ArrayList<>();

    private UserList() { }
    public static UserList getInstance() {
        if (instance == null) instance = new UserList();
        return instance;
    }
    public ArrayList<User> getUsers() { return users; }

    public User getUser(String username) {
        if (username == null) return null;
        for (User user : users) {
            if (username.equalsIgnoreCase(user.getUsername())) return user;
        }
        return null;
    }

    public User getUser(String username, String password) {
        User user = getUser(username);
        return user != null && user.login(username, password) ? user : null;
    }

    public User authenticate(String username, String password) { return getUser(username, password); }

    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || getUser(user.getUsername()) != null) return false;
        return users.add(user);
    }

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

    public boolean save() { return DataWriter.saveUsers(users); }
}
