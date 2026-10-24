package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

/** Starts the terminal app and shelter map. */
public class UI {
    private Scanner scanner;


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
            app.login(prompt("Username: "), prompt("Password: "));
        } else if (choice.equals("2")) {
            createAccountMenu();
        } else if (choice.equals("3")) {
            showShelters(app.findNearestShelters(prompt("Town or address: "), ShelterList.DEFAULT_RADIUS_MILES));
        } else {
            System.out.println("Please choose 1 to 4.");
        }
        return true;
    }

    private boolean homeMenu() {
        System.out.println();
        System.out.println("Logged in as " + app.getCurrentUser().getUsername());
        System.out.println("1. Submit a relief request");
        System.out.println("2. Find shelters near me");
        System.out.println("3. Mark myself safe");
        System.out.println("4. Hurricane information");
        System.out.println("5. View open requests");
        System.out.println("6. Log out");
        System.out.println("7. Exit");
        if (app.canVerifyVolunteers()) {
            System.out.println("8. Verify a volunteer's background check");
        }
        if (app.getCurrentUser().getRoles().contains(AccountRole.VOLUNTEER)) {
            System.out.println("9. View my assignments");
        }
        System.out.println("10. View or cancel my requests");
        String choice = prompt("Choose an option: ");

        if (choice == null || choice.equals("7")) {
            return false;
        } else if (choice.equals("1")) {
            submitRequestMenu();
        } else if (choice.equals("2")) {
            showShelters(app.findNearestShelters(null, ShelterList.DEFAULT_RADIUS_MILES));
        } else if (choice.equals("3")) {
            app.markSafe();
        } else if (choice.equals("4")) {
            showHurricane();
        } else if (choice.equals("5")) {
            openRequestsMenu();
        } else if (choice.equals("6")) {
            app.logout();
        } else if (choice.equals("8") && app.canVerifyVolunteers()) {
            app.verifyVolunteer(prompt("Volunteer's username: "));
        } else if (choice.equals("9") && app.getCurrentUser().getRoles().contains(AccountRole.VOLUNTEER)) {
            myAssignmentsMenu();
        } else if (choice.equals("10")) {
            myRequestsMenu();
        } else {
            System.out.println("Please choose one of the displayed options.");
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
        if (app.createAccount(role, details)) {
            app.login(details.get("username"), details.get("password"));
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
        app.submitReliefRequest(types[typeChoice - 1], description, location, people);
    }

    private void openRequestsMenu() {
        boolean isVolunteer = app.getCurrentUser().getRoles().contains(AccountRole.VOLUNTEER);
        List<ReliefRequest> requests = isVolunteer ? app.getOpenRequestsForVolunteers() : app.getOpenRequestsByUrgency();
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
            app.claimRequest(requests.get(choice - 1).getRequestId());
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
        HurricaneEvent active = app.getActiveHurricane();
        if (active != null) {
            User.getLatestInfo(active);
            return;
        }
        System.out.println("No active hurricane events at the moment.");
        for (HurricaneEvent hurricane : app.getHurricanes()) {
            System.out.println(hurricane.getName() + " (" + hurricane.getStatus() + ") "
                    + hurricane.getAffectedAreas());
        }
    }

    private void myRequestsMenu() {
        List<ReliefRequest> requests = app.getMyRequests();
        if (requests.isEmpty()) {
            System.out.println("You have not submitted any requests.");
            return;
        }
        for (int i = 0; i < requests.size(); i++) {
            ReliefRequest request = requests.get(i);
            System.out.println((i + 1) + ". " + request.getType() + " at "
                    + request.getLocation() + " (" + request.getStatus() + ")");
        }
        int choice = number(prompt("Choose a request to cancel, or Enter to go back: "));
        if (choice >= 1 && choice <= requests.size()) {
            app.cancelRequest(requests.get(choice - 1).getRequestId());
        }
    }

    private void myAssignmentsMenu() {
        ArrayList<ReliefRequest> myAssignments = app.getMyAssignments();
        if (myAssignments.isEmpty()) {
            System.out.println("You have no current assignments.");
            return;
        }
        for (int i = 0; i < myAssignments.size(); i++) {
            ReliefRequest request = myAssignments.get(i);
            System.out.println((i + 1) + ". [" + request.getPriority() + "] " + request.getType()
                    + " at " + request.getLocation() + " for " + request.getNumberOfPeopleNeeded()
                    + " people (" + request.getStatus() + ")");
        }

        int choice = number(prompt("Choose an assignment, or press Enter to go back: "));
        if (choice >= 1 && choice <= myAssignments.size()) {
            ReliefRequest selected = myAssignments.get(choice - 1);
            String action = prompt("1. Start working, 2. Complete, or Enter to go back: ");
            if ("1".equals(action)) app.startRequest(selected.getRequestId());
            else if ("2".equals(action)) app.completeRequest(selected.getRequestId());
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


    private HurricaneReliefApp app = new HurricaneReliefApp();

    public UI(HurricaneReliefApp app) {
        this.app = app;
    }

    public void run() {
        scanner = new Scanner(System.in);
        System.out.println("Welcome to the Gamecock Relief Network");

        boolean running = true;
        while (running) {
            running = app.getCurrentUser() == null ? startMenu() : homeMenu();
        }

        if (app.getCurrentUser() != null) {
            app.logout();
        }
        app.saveData();
        System.out.println("Goodbye!");
    }



    public static void main(String[] args) {
        Thread terminal = new Thread(() -> {
            HurricaneReliefApp app = new HurricaneReliefApp();
            app.initialize();
            new UI(app).run();
        });

        terminal.start();
        MapUI.main(args);
    }
}
