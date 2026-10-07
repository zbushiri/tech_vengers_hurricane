package model;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class UI {
    private static final String USER_FILE = "../json/users.json";

    private Scanner scanner;
    private UserList userList;
    private ArrayList<User> newUsers;
    private User currentUser;

    /**
     * Constructs a new ui instance.
     */
    public UI() {
        scanner = new Scanner(System.in);
        userList = UserList.getInstance();
        newUsers = new ArrayList<>();
        for (User user : DataLoader.getUsers()) {
            userList.addUser(user);
        }
    }

    /**
     * Runs the ui.
     */
    public void run() {
        System.out.println("Welcome to the Gamecock Relief Network");

        boolean running = true;
        while (running) {
            if (currentUser == null) {
                running = startMenu();
            } else {
                running = homeMenu();
            }
        }

        currentUser = null;
        saveNewUsers();
        System.out.println("Goodbye!");
    }

    
    /**
     * Starts the menu.
     *
     * @return true if successful, false otherwise
     */
    private boolean startMenu() {
        System.out.println();
        System.out.println("1. Log in");
        System.out.println("2. Create an account");
        System.out.println("3. Exit");
        String choice = prompt("Choose an option: ");

        if (choice == null || choice.equals("3")) {
            return false;
        } else if (choice.equals("1")) {
            login();
        } else if (choice.equals("2")) {
            createAccount();
        } else {
            System.out.println("Please choose 1, 2, or 3.");
        }
        return true;
    }

    
    /**
     * Home menus.
     *
     * @return true if successful, false otherwise
     */
    private boolean homeMenu() {
        System.out.println();
        System.out.println("Logged in as " + currentUser.getUsername());
        System.out.println("1. View my profile");
        System.out.println("2. Log out");
        System.out.println("3. Exit");
        String choice = prompt("Choose an option: ");

        if (choice == null || choice.equals("3")) {
            return false;
        } else if (choice.equals("1")) {
            showProfile(currentUser);
        } else if (choice.equals("2")) {
            currentUser = null;
            System.out.println("You have been logged out.");
        } else {
            System.out.println("Please choose 1, 2, or 3.");
        }
        return true;
    }

    /**
     * Logins.
     */
    private void login() {
        String username = prompt("Username: ");
        String password = prompt("Password: ");

        for (User user : userList.getUsers()) {
            if (user.getUsername() != null && user.login(username, password)) {
                currentUser = user;
                System.out.println("Welcome back, " + user.getFirstName() + "!");
                return;
            }
        }
        System.out.println("Incorrect username or password.");
    }

    /**
     * Creates a new the account.
     */
    private void createAccount() {
        String username = prompt("Choose a username: ");
        if (isBlank(username)) {
            System.out.println("A username is required.");
            return;
        }
        if (findUser(username) != null) {
            System.out.println("That username is already taken.");
            return;
        }
        String password = prompt("Choose a password: ");
        String firstName = prompt("First name: ");
        String lastName = prompt("Last name: ");
        String email = prompt("Email: ");
        String address = prompt("Address: ");
        String roleChoice = prompt("Are you a 1. Resident or 2. Volunteer? ");

        if (isBlank(password) || isBlank(firstName) || isBlank(lastName)) {
            System.out.println("Could not create the account. Password and name are required.");
            return;
        }

        User user = new User(username, password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email == null ? "" : email);
        user.setAddress(address == null ? "" : address);
        user.getRoles().add("2".equals(roleChoice) ? AccountRole.VOLUNTEER : AccountRole.RESIDENT);

        userList.addUser(user);
        newUsers.add(user);
        currentUser = user;
        System.out.println("Account created. Welcome, " + firstName + "!");
    }

    /**
     * Show profiles.
     *
     * @param user user object
     */
    private void showProfile(User user) {
        System.out.println();
        System.out.println("Name: " + user.getFirstName() + " " + user.getLastName());
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Address: " + user.getAddress());
        System.out.println("Roles: " + user.getRoles());
        System.out.println("Verification: " + user.getVerificationStatus());
        System.out.println("Safety status: " + user.getSafetyStatus());
    }

    /**
     * Finds the user.
     *
     * @param username username
     * @return the resulting user
     */
    private User findUser(String username) {
        for (User user : userList.getUsers()) {
            if (username.equalsIgnoreCase(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    
    /**
     * Saves the new users.
     */
    @SuppressWarnings("unchecked")
    private void saveNewUsers() {
        if (newUsers.isEmpty()) {
            return;
        }

        JSONArray userArray;
        /**
         * Constructs a new ui instance with the specified parameters.
         *
         * @param FileReader(USER_FILE) file reader(user file)
         */
        try (FileReader reader = new FileReader(USER_FILE)) {
            userArray = (JSONArray) new JSONParser().parse(reader);
        } catch (Exception exception) {
            System.out.println("Could not read users before saving.");
            return;
        }

        ArrayList<String> savedUsernames = new ArrayList<>();
        for (Object item : userArray) {
            savedUsernames.add(String.valueOf(((JSONObject) item).get("username")));
        }

        for (User user : newUsers) {
            if (savedUsernames.contains(user.getUsername())) {
                continue;
            }
            JSONObject userJSON = new JSONObject();
            userJSON.put("userId", user.getUserId().toString());
            userJSON.put("firstName", user.getFirstName());
            userJSON.put("lastName", user.getLastName());
            userJSON.put("email", user.getEmail());
            userJSON.put("address", user.getAddress());
            userJSON.put("username", user.getUsername());
            userJSON.put("passwordHash", user.getPasswordHash());
            JSONArray roles = new JSONArray();
            for (AccountRole role : user.getRoles()) {
                roles.add(role.name().toLowerCase());
            }
            userJSON.put("roles", roles);
            userJSON.put("associatedPeople", new JSONArray());
            userJSON.put("verificationStatus", user.getVerificationStatus().name().toLowerCase());
            userJSON.put("safetyStatus", user.getSafetyStatus().name().toLowerCase());
            userJSON.put("safetyStatusUpdatedAt", user.getSafetyStatusUpdatedAt().toString());
            userArray.add(userJSON);
        }

        // ! Do not overwrite the file until the entire JSON array is ready.
        /**
         * Constructs a new ui instance with the specified parameters.
         *
         * @param FileWriter(USER_FILE) file writer(user file)
         */
        try (FileWriter file = new FileWriter(USER_FILE)) {
            userArray.writeJSONString(file);
            System.out.println("Your account was saved.");
        } catch (IOException exception) {
            System.out.println("Could not save users.");
        }
    }

    /**
     * Checks whether the blank.
     *
     * @param value value
     * @return true if the condition holds, false otherwise
     */
    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    
    /**
     * Prompts.
     *
     * @param message message
     * @return the resulting string
     */
    private String prompt(String message) {
        System.out.print(message);
        if (!scanner.hasNextLine()) {
            System.out.println();
            return null;
        }
        return scanner.nextLine().trim();
    }

    /**
     * Mains.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        UI ui = new UI();
        ui.run();
    }
}
