package model;

import java.util.ArrayList;

public class UserList {
    private static UserList instance;
    private ArrayList<User> users;

    private UserList() {
        users = new ArrayList<>();
    }

    public static UserList getInstance() {
        if (instance == null) {
            instance = new UserList();
        }
        return instance;
    }
    public ArrayList<User> getUsers() {
        return users;
    }

    /**
     * Finds a user by username, ignoring case.
     *
     * @return the user, or null if no account has that username
     */
    public User getUser(String userName) {
        if (userName == null) {
            return null;
        }
        for (User user : users) {
            if (userName.equalsIgnoreCase(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    /**
     * Finds the user whose username and password both match.
     *
     * @return the user, or null if the login details are wrong
     */
    public User getUser(String userName, String password) {
        User user = getUser(userName);
        if (user != null && user.login(userName, password)) {
            return user;
        }
        return null;
    }

    /**
     * Adds a user unless another account already has the same username.
     *
     * @return true if the user was added
     */
    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || getUser(user.getUsername()) != null) {
            return false;
        }
        return users.add(user);
    }

    public boolean addUser(String userName, String password) {
        return addUser(new User(userName, password));
    }

    public boolean addUser(String firstName, String lastName, String userName, String email) {
        User user = new User(userName, "");
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        return addUser(user);
    }

    public boolean save() {
        return DataWriter.saveUsers();
    }
}
