package model;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import location.RoutingService;

/**
 * Presents the Carter family's Hurricane Scarlett walkthrough, one step at a time.
 * Application services handle permissions, recommendations, routing and check-in.
 *
 * @author Team Tech-vengers
 */
public class ScenarioUI {
    private static final String PICKUP = "25 Calhoun Street, Charleston, SC 29401";
    private static final String MEDICAL_NEEDS = "Sophie has worsening asthma and needs medical staff.";

    private final HurricaneReliefApp app;
    private final ScenarioData data;

    /**
     * Loads the prepared demonstration data.
     * @param routes driving-route provider; tests supply fictional routes
     * @param directory folder containing the demonstration's JSON files
     */
    public ScenarioUI(RoutingService routes, Path directory) {
        app = new HurricaneReliefApp(routes);
        data = new ScenarioData(directory);
        app.initialize();
    }

    /**
     * Presents the required scenario in order and prints the outcome of each action.
     * @throws IOException if routing, export or saving a snapshot fails
     */
    public void run() throws IOException {
        data.checkStartingData();
        data.snapshot(app, "before");

        ReliefRequest request = createCarterRequest();
        if (request == null) {
            System.out.println("The request could not be submitted.");
            return;
        }
        showRosasRecommendations(request);
        Shelter shelter = selectShelterAndAssign(request);
        if (shelter == null) {
            System.out.println("No suitable shelter was found.");
            return;
        }
        exportTripSheet(request);
        demonstrateVolunteerRestriction(request);
        checkInFamily(request, shelter);
        completeRequest(request);

        data.printRestartInstructions();
    }

    /** Creates the named household and records its transportation and medical needs. */
    private ReliefRequest createCarterRequest() throws IOException {
        System.out.println("\n1. David Carter creates an account and requests transportation.");
        ArrayList<AccountRole> roles = new ArrayList<>(List.of(AccountRole.RESIDENT));
        System.out.println("Carter account created: " + app.createAccount("David", "Carter", "dcarter@example.com", "803-555-0142",
                PICKUP, "dcarter", "carter2026", roles, PICKUP));
        System.out.println("David logged in: " + app.login("dcarter", "carter2026"));

        List<Person> family = List.of(
                new Person("David", "Carter", 38, new ArrayList<>()),
                new Person("Elena", "Carter", 36, new ArrayList<>()),
                new Person("Sophie", "Carter", 9, new ArrayList<>(List.of("Asthma; medical assistance needed"))));
        ReliefRequest request = app.submitHouseholdRequest(RequestType.TRANSPORTATION,
                "Floodwater blocks our car. Three people and Buddy need transport; Sophie's asthma has worsened.",
                PICKUP, family, List.of(new Pet("Buddy", "dog")), MEDICAL_NEEDS);
        System.out.println("Household request submitted: " + (request != null));

        data.snapshot(app, "submitted");
        app.logout();
        return request;
    }

    /** Shows why Carter's request is first among Rosa's seven open requests. */
    private void showRosasRecommendations(ReliefRequest request) {
        System.out.println("\n2. Rosa reviews seven open requests and the recommended household.");
        System.out.println("Rosa logged in: " + app.login("rosadelgado", "rosa2026"));
        List<RequestRecommendation> matches = app.getRecommendedRequests();
        System.out.println("  Open requests: " + matches.size());

        for (RequestRecommendation match : matches) {
            ReliefRequest open = match.request();
            String label = match.recommended() ? "RECOMMENDED: " : "";
            System.out.println("  " + open.getType() + " at " + open.getLocation());
            System.out.println("    " + label + match.explanation());
        }
        printHousehold(request.getFamilyMembers(), request.getPets());
        System.out.println("  Medical assistance: " + request.getMedicalNeeds());
    }

    /** Finds the closest eligible shelter by road and assigns the trip to Rosa. */
    private Shelter selectShelterAndAssign(ReliefRequest request) throws IOException {
        System.out.println("\n3. Rosa compares ten shelters and claims the request.");
        System.out.println("  Shelters available to search: " + ShelterList.getInstance().getShelters().size());
        Shelter shelter = app.findShelter(request);

        if (shelter == null) {
            return null;
        }
        System.out.println("Request assigned to Rosa: " + app.claimRequest(request.getRequestId()));
        System.out.println("Destination selected: " + app.selectShelter(request.getRequestId(), shelter));

        System.out.println("  Destination: " + shelter.getName() + ", " + shelter.getAddress());
        System.out.println("  Status: " + request.getStatus()
                + "; assigned to " + request.getAssignedVolunteer().getUsername());
        data.snapshot(app, "assigned");
        return shelter;
    }

    /** Exports the family details and directions for both parts of Rosa's trip. */
    private void exportTripSheet(ReliefRequest request) throws IOException {
        System.out.println("\n4. Rosa exports the household details and both route legs.");
        Path tripSheet = app.exportTripSheet(request.getRequestId(), data.tripSheetDirectory());
        System.out.println("  Saved: " + tripSheet);
    }

    /** Demonstrates that transporting the family does not grant completion permission. */
    private void demonstrateVolunteerRestriction(ReliefRequest request) {
        System.out.println("\n5. Rosa attempts completion after transporting the family.");
        System.out.println("Volunteer completion denied: " + !app.completeRequest(request.getRequestId()));
        System.out.println("  Request status: " + request.getStatus());
        app.logout();
    }

    /** Shows Felix's shelter and checks in the family without counting Buddy as a person. */
    private void checkInFamily(ReliefRequest request, Shelter shelter) throws IOException {
        System.out.println("\n6. Felix views his shelter and checks in the Carter household.");
        System.out.println("Felix logged in: " + app.login("felixgrant", "felix2026"));
        System.out.println("  " + shelter.getName() + ", " + shelter.getAddress());
        System.out.println("  Occupancy: " + shelter.getOccupancy() + "/" + shelter.getCapacity()
                + "; available spaces: " + shelter.getAvailableSpaces());

        int previousOccupancy = shelter.getOccupancy();
        System.out.println("Household checked in: " + app.checkIn(request.getRequestId(), shelter));
        System.out.println("  People checked in: " + (shelter.getOccupancy() - previousOccupancy));
        printHousehold(shelter.getOccupants(), shelter.getPets());
        System.out.println("  Human occupancy is now " + shelter.getOccupancy());
        data.snapshot(app, "checked-in");
    }

    /** Demonstrates authorized completion and saves the final JSON snapshot. */
    private void completeRequest(ReliefRequest request) throws IOException {
        System.out.println("\n7. Felix completes the request and saves all changes.");
        System.out.println("Request completed by Felix: " + app.completeRequest(request.getRequestId()));
        data.snapshot(app, "after");
        app.logout();
    }

    /** Prints a readable list of people and accompanying pets. */
    private void printHousehold(List<Person> people, List<Pet> pets) {
        for (Person person : people) {
            System.out.println("  " + person.getFirstName() + " " + person.getLastName()
                    + ", age " + person.getAge());
        }
        for (Pet pet : pets) {
            System.out.println("  " + pet.name() + " (" + pet.species() + ")");
        }
    }

    /**
     * Verifies the demonstration's persisted results after restarting the application.
     * @return true if the account, assignment, household and completion survived
     */
    public boolean verifySaved() {
        return data.verifySaved(app);
    }

    /**
     * Starts the demonstration or reloads its saved results.
     * @param args optional --verify and --data-dir=folder
     * @throws IOException if seed data, driving routes or exports are unavailable
     */
    public static void main(String[] args) throws IOException {
        ScenarioData.launch(args);
    }
}
