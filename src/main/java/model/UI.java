package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Presents interactive terminal menus and delegates user actions to {@link HurricaneReliefApp}.
 * @author Team Tech-vengers
 */
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
            showShelters(app.findNearestShelters(prompt("Full address (street, city, ST ZIP): "), ShelterList.DEFAULT_RADIUS_MILES));
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
        if (!app.getManagedShelters().isEmpty()) System.out.println("11. Manage shelter / check in / complete requests");
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
        } else if (choice.equals("11") && !app.getManagedShelters().isEmpty()) {
            manageShelterMenu();
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
        if (people < 1 || people > 100) { System.out.println("Enter between 1 and 100 people."); return; }
        List<Person> family = new ArrayList<>();
        for (int i = 0; i < people; i++) {
            System.out.println("Person " + (i + 1) + " (include the account holder):");
            String first = prompt("First name: "); String last = prompt("Last name: ");
            int age = number(prompt("Age: ")); String needs = prompt("Special needs, or Enter for none: ");
            if (age < 0 || age > 120 || first == null || first.isBlank() || last == null || last.isBlank()) {
                System.out.println("Each person needs a name and valid age."); return;
            }
            family.add(new Person(first, last, age, new ArrayList<>(needs == null || needs.isBlank() ? List.of() : List.of(needs))));
        }
        int petCount = number(prompt("How many accompanying pets? (0 for none): "));
        if (petCount < 0 || petCount > 100) { System.out.println("Enter a valid pet count."); return; }
        List<Pet> pets = new ArrayList<>();
        for (int i = 0; i < petCount; i++) pets.add(new Pet(prompt("Pet name: "), prompt("Species (for example dog): ")));
        String medical = prompt("Medical assistance required, or Enter for none: ");
        ReliefRequest created = app.submitHouseholdRequest(types[typeChoice - 1], description, location, family, pets, medical);
        System.out.println(created == null ? "Request could not be submitted." : "Request submitted: " + created.getRequestId() + " [" + created.getPriority() + "]");
    }

    /** Displays recommended requests with explanations and existing assignments. */
    private void openRequestsMenu() {
        boolean volunteer = app.getCurrentUser().getVolunteerProfile() != null;
        List<RequestRecommendation> recommendations = volunteer ? app.getRecommendedRequests() : List.of();
        List<ReliefRequest> requests = volunteer ? recommendations.stream().map(RequestRecommendation::request).toList()
                : app.getOpenRequestsByUrgency();
        if (requests.isEmpty()) { System.out.println("There are no open requests."); return; }
        for (int i = 0; i < requests.size(); i++) {
            ReliefRequest request = requests.get(i);
            System.out.println((i + 1) + ". " + requestSummary(request));
            if (volunteer) {
                RequestRecommendation match = recommendations.get(i);
                System.out.println("   " + (match.recommended() ? "RECOMMENDED: " : "") + match.explanation());
            }
        }
        int choice = number(prompt("Open a request number, or Enter to return: "));
        if (choice < 1 || choice > requests.size()) return;
        ReliefRequest request = requests.get(choice - 1);
        showHousehold(request);
        if (volunteer && request.getStatus() == RequestStatus.SUBMITTED
                && "y".equalsIgnoreCase(prompt("Claim this request? (y/n): "))) app.claimRequest(request.getRequestId());
    }

    /** Formats the current status and makes the assigned volunteer visible. */
    private String requestSummary(ReliefRequest request) {
        return "[" + request.getPriority() + "] " + request.getType() + " at " + request.getLocation()
                + " (" + request.getStatus() + ")" + (request.getAssignedVolunteer() == null ? ""
                : "; assigned to " + request.getAssignedVolunteer().getUsername());
    }

    /** Prints names, ages, animals, medical needs and contact information. */
    private void showHousehold(ReliefRequest request) {
        System.out.println(requestSummary(request));
        request.getFamilyMembers().forEach(p -> System.out.println("  " + p.getFirstName() + " " + p.getLastName() + ", age " + p.getAge()));
        request.getPets().forEach(p -> System.out.println("  " + p.name() + " (" + p.species() + ")"));
        System.out.println("Medical assistance: " + request.getMedicalNeeds());
        System.out.println("Notes: " + request.getDescription());
        if (request.getSubmittedBy() != null) System.out.println("Contact: " + request.getSubmittedBy().getPhoneNumber()
                + "; " + request.getSubmittedBy().getEmail());
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
            showHousehold(selected);
            String action = prompt("1. Start working, 2. Attempt completion, 3. Find/select closest suitable shelter, 4. Export trip sheet, or Enter: ");
            if ("1".equals(action)) app.startRequest(selected.getRequestId());
            else if ("2".equals(action)) app.completeRequest(selected.getRequestId());
            else if ("3".equals(action)) {
                try {
                    Shelter shelter = app.findShelter(selected);
                    if (shelter == null) System.out.println("No shelter has all required services and space.");
                    else {
                        showShelters(List.of(shelter));
                        if ("y".equalsIgnoreCase(prompt("Select this shelter? (y/n): ")))
                            System.out.println(app.selectShelter(selected.getRequestId(), shelter) ? "Destination selected." : "Destination could not be selected.");
                    }
                } catch (java.io.IOException e) { System.out.println("Cannot compare driving routes: " + e.getMessage()); }
            } else if ("4".equals(action)) {
                try { System.out.println("Saved " + app.exportTripSheet(selected.getRequestId(), java.nio.file.Path.of("trip-sheets"))); }
                catch (java.io.IOException e) { System.out.println("Trip export failed: " + e.getMessage()); }
            }
        }
    }



    /** Displays managed shelter occupants and supports authorized check-in and completion. */
    private void manageShelterMenu() {
        List<Shelter> shelters = app.getManagedShelters();
        for (int i = 0; i < shelters.size(); i++) System.out.println((i + 1) + ". " + shelters.get(i).getName());
        int choice = number(prompt("Choose your shelter, or Enter: "));
        if (choice < 1 || choice > shelters.size()) return;
        Shelter shelter = shelters.get(choice - 1);
        showShelters(List.of(shelter));
        System.out.println("Available spaces: " + shelter.getAvailableSpaces() + "; pets: " + shelter.getPetOccupancy() + "/" + shelter.getPetCapacity());
        shelter.getOccupants().forEach(p -> System.out.println("Occupant: " + p.getFirstName() + " " + p.getLastName() + ", age " + p.getAge()));
        shelter.getPets().forEach(p -> System.out.println("Pet: " + p.name() + " (" + p.species() + ")"));
        int unnamed = shelter.getOccupancy() - shelter.getOccupants().size();
        if (unnamed > 0) System.out.println("Legacy occupants without saved names: " + unnamed);
        List<ReliefRequest> requests = app.getOpenRequestsByUrgency().stream()
                .filter(r -> shelter.getShelterId().equals(r.getDestinationShelterId())).toList();
        for (int i = 0; i < requests.size(); i++) System.out.println((i + 1) + ". " + requestSummary(requests.get(i)));
        int selected = number(prompt("Choose an arriving request, or Enter: "));
        if (selected < 1 || selected > requests.size()) return;
        ReliefRequest request = requests.get(selected - 1); showHousehold(request);
        String action = prompt("1. Check in household, 2. Complete request, or Enter: ");
        if ("1".equals(action)) System.out.println(app.checkIn(request.getRequestId(), shelter)
                ? "Household and pets checked in; human occupancy is " + shelter.getOccupancy() : "Check-in denied: verify assignment, services, capacity, or an existing admission.");
        else if ("2".equals(action)) app.completeRequest(request.getRequestId());
        if (!app.saveData()) System.out.println("Saving failed; do not exit until saved data has been checked.");
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


    private final HurricaneReliefApp app;

    /**
     * Creates the terminal interface for the supplied application facade.
     * @param app application facade used by the terminal menus
     */
    public UI(HurricaneReliefApp app) {
        this.app = app;
    }

    /**
     * Displays terminal menus until the user exits, saving changes after menu actions.
     */
    public void run() {
        scanner = new Scanner(System.in);
        System.out.println("Welcome to the Gamecock Relief Network");

        boolean running = true;
        while (running) {
            try {
                running = app.getCurrentUser() == null ? startMenu() : homeMenu();
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println(e.getMessage());
            }
            if (!app.saveData()) System.err.println("Changes could not be fully saved.");
        }

        if (app.getCurrentUser() != null) {
            app.logout();
        }
        if (!app.saveData()) System.err.println("Changes could not be fully saved.");
        System.out.println("Goodbye!");
    }



    /**
     * Loads the saved data and starts the interactive terminal menus.
     * @param args command-line arguments; unused by this entry point
     */
    public static void main(String[] args) {
        HurricaneReliefApp app = new HurricaneReliefApp();
        app.initialize();
        new UI(app).run();
    }
}
