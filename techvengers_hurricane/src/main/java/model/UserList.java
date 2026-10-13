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
<<<<<<< HEAD

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
=======
    
    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPasswordHash().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public void addUser(String firstname, String lastname, String email, String phoneNumber, String address,
                        String username, String password, ArrayList<AccountRole> roles, String lastKnownLocation) {
        User user = new User(firstname, lastname, email, phoneNumber, address, username, password, roles, lastKnownLocation);
        users.add(user);
>>>>>>> 10043cc5d4a9b45cb863e7cb7f14e3a8d74f9f28
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
