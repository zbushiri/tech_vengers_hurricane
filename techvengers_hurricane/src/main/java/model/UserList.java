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
    
    public void addUser(User user) {
        users.add(user);
    }
    
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
    }
}