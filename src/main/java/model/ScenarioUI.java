package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import location.RoutingService;
import location.MapTools;

/**
 * Demonstrates the supplied Hurricane Scarlett scenario with checked outcomes.
 * Uses a separate demo-data folder by default; restarting with --verify reloads saved results.
 * @author Team Tech-vengers
 */
public class ScenarioUI {
    private final HurricaneReliefApp app;
    private final Path data;

    /**
     * Loads a prepared scenario data folder using the chosen routing provider.
     * @param routes real driving routes, or a deterministic provider in tests
     * @param data folder containing users, shelters, requests and hurricanes JSON
     */
    public ScenarioUI(RoutingService routes, Path data) {
        this.data = data;
        app = new HurricaneReliefApp(routes);
        app.initialize();
    }

    /**
     * Runs the complete scenario and saves before/after JSON snapshots.
     * @throws IOException if routing, export or snapshot creation fails
     */
    public void run() throws IOException {
        if (UserList.getInstance().getUser("dcarter") != null) {
            throw new IllegalStateException("This scenario already has a Carter account. Run with --verify to show saved results, or choose a fresh --data-dir folder.");
        }
        require(ReliefRequestCatalog.getInstance().getOpenRequestsByUrgency().size() == 6,
                "Start this demonstration with the six original open sample requests.");
        snapshot("before");
        System.out.println("1. David Carter creates an account and requests transportation.");
        String pickup = "25 Calhoun Street, Charleston, SC 29401";
        require(app.createAccount("David", "Carter", "dcarter@example.com", "803-555-0142", pickup,
                "dcarter", "carter2026", new ArrayList<>(List.of(AccountRole.RESIDENT)), pickup), "Create Carter account");
        require(app.login("dcarter", "carter2026"), "Log in David");
        List<Person> family = List.of(new Person("David", "Carter", 38, new ArrayList<>()),
                new Person("Elena", "Carter", 36, new ArrayList<>()),
                new Person("Sophie", "Carter", 9, new ArrayList<>(List.of("Asthma; medical assistance needed"))));
        ReliefRequest request = app.submitHouseholdRequest(RequestType.TRANSPORTATION,
                "Floodwater blocks our car. Three people and Buddy need transport; Sophie's asthma has worsened.",
                pickup, family, List.of(new Pet("Buddy", "dog")), "Sophie has worsening asthma and needs medical staff.");
        require(request != null, "Submit household request");
        snapshot("submitted"); app.logout();
        System.out.println("2. Rosa sees seven open requests and an explained recommendation.");
        require(app.login("rosadelgado", "rosa2026"), "Log in Rosa");
        List<RequestRecommendation> matches = app.getRecommendedRequests();
        require(matches.size() == 7 && matches.get(0).request() == request && matches.get(0).recommended(),
                "Carter request must be first because of nearby transportation specialty match");
        for (RequestRecommendation match : matches) System.out.println("  " + match.request().getRequestId()
                + " " + match.request().getType() + " " + match.request().getLocation() + " "
                + (match.recommended() ? "RECOMMENDED: " : "") + match.explanation());
        family.forEach(p -> System.out.println("  " + p.getFirstName() + " " + p.getLastName() + ", age " + p.getAge()));
        System.out.println("  Buddy (dog); " + request.getMedicalNeeds());
        System.out.println("3. Compare ten shelters by driving distance, with medical, dog and capacity filters.");
        require(ShelterList.getInstance().getShelters().size() == 10, "Ten sample shelters");
        Shelter shelter = app.findShelter(request);
        require(shelter != null, "Find a shelter with all required services and space");
        System.out.println("  Selected: " + shelter.getName() + ", " + shelter.getAddress());
        require(shelter.isShelterOperator(UserList.getInstance().getUser("felixgrant")), "Felix must manage the selected shelter");
        require(app.claimRequest(request.getRequestId()), "Assign request to Rosa");
        require(app.selectShelter(request.getRequestId(), shelter), "Select destination");
        snapshot("assigned");
        System.out.println("  Visible to other volunteers: " + request.getStatus() + ", assigned to "
                + ReliefRequestCatalog.getInstance().getRequest(request.getRequestId()).getAssignedVolunteer().getUsername());
        System.out.println("4. Export both route legs and complete household details.");
        System.out.println("  Saved: " + app.exportTripSheet(request.getRequestId(), data.resolve("trip-sheets")));
        System.out.println("5. Rosa attempts completion after transporting the family.");
        require(!app.completeRequest(request.getRequestId()), "Deny volunteer completion");
        require(request.getStatus() == RequestStatus.ASSIGNED, "Keep request assigned to Rosa");
        app.logout();
        System.out.println("6. Felix views his shelter and checks in three people and one dog.");
        require(app.login("felixgrant", "felix2026"), "Log in Felix");
        System.out.println("  " + shelter.getName() + ", " + shelter.getAddress() + ": occupancy "
                + shelter.getOccupancy() + "/" + shelter.getCapacity() + "; available " + shelter.getAvailableSpaces());
        int before = shelter.getOccupancy();
        require(app.checkIn(request.getRequestId(), shelter), "Check in household");
        require(shelter.getOccupancy() == before + 3, "Increase human occupancy by three");
        shelter.getOccupants().forEach(p -> System.out.println("  Occupant: " + p.getFirstName() + " " + p.getLastName() + ", age " + p.getAge()));
        shelter.getPets().forEach(p -> System.out.println("  Pet: " + p.name() + " (" + p.species() + ")"));
        snapshot("checked-in");
        System.out.println("7. Felix completes the request and saves all changes.");
        require(app.completeRequest(request.getRequestId()), "Manager completes request");
        snapshot("after"); app.logout();
        System.out.println("Restart Scenario with --verify --data-dir=" + data + " to prove persistence. JSON snapshots are in " + data.resolve("snapshots"));
    }

    /**
     * Checks the persisted Carter household after starting a fresh JVM.
     * @return true when account, assignment, admission, pets and completion survived
     */
    public boolean verifySaved() {
        require(app.login("dcarter", "carter2026"), "Reload Carter account");
        ReliefRequest request = app.getMyRequests().stream().filter(r -> r.getDestinationShelterId() != null).findFirst()
                .orElseThrow(() -> new IllegalStateException("No saved Carter destination"));
        Shelter shelter = ShelterList.getInstance().getShelters().stream()
                .filter(s -> s.getShelterId().equals(request.getDestinationShelterId())).findFirst().orElseThrow();
        require(request.getStatus() == RequestStatus.FULFILLED && request.getAssignedVolunteer() != null
                && "rosadelgado".equals(request.getAssignedVolunteer().getUsername())
                && request.getFamilyMembers().size() == 3 && request.getPets().equals(List.of(new Pet("Buddy", "dog")))
                && shelter.hasAdmission(request.getRequestId()) && shelter.getPets().contains(new Pet("Buddy", "dog"))
                && shelter.getOccupants().stream().anyMatch(p -> "Sophie".equals(p.getFirstName())), "Verify saved household and completion");
        System.out.println("Restart verified: Carter account, three family members, Buddy, Rosa assignment, shelter admission, and Complete status survived.");
        app.logout(); return true;
    }

    /** Saves current data and copies it into a labeled demonstration snapshot. */
    private void snapshot(String phase) throws IOException {
        require(app.saveData(), "Save all JSON files");
        Path folder = data.resolve("snapshots").resolve(phase); Files.createDirectories(folder);
        for (String name : List.of("users", "shelters", "requests", "hurricanes"))
            Files.copy(data.resolve(name + ".json"), folder.resolve(name + ".json"), StandardCopyOption.REPLACE_EXISTING);
    }

    /** Stops the walkthrough immediately when an expected operation fails. */
    private static void require(boolean condition, String operation) {
        if (!condition) throw new IllegalStateException("Scenario stopped: " + operation);
    }

    /**
     * Starts an isolated demonstration or verifies an existing saved demonstration.
     * @param args optional --verify and --data-dir=folder; set HURRICANE_API for live routes
     * @throws IOException if seed data or route services are unavailable
     */
    public static void main(String[] args) throws IOException {
        Path data = Path.of("demo-data").toAbsolutePath();
        boolean verify = false;
        for (String arg : args) {
            if (arg.startsWith("--data-dir=")) data = Path.of(arg.substring(11)).toAbsolutePath();
            else if (arg.equals("--verify")) verify = true;
            else throw new IllegalArgumentException("Unknown option: " + arg);
        }
        if (!verify && (System.getenv("HURRICANE_API") == null || System.getenv("HURRICANE_API").isBlank())) {
            throw new IllegalStateException("Set HURRICANE_API in the root .env file for VS Code (or export it in your shell) before running live Scenario routes.");
        }
        System.setProperty("hurricane.dataDir", data.toString());
        if (!Files.exists(data)) {
            if (verify) throw new IllegalStateException("No saved demo at " + data);
            Files.createDirectories(data);
            for (String name : List.of("users", "shelters", "requests", "hurricanes"))
                Files.copy(Path.of("json", name + ".json"), data.resolve(name + ".json"));
        }
        ScenarioUI scenario = new ScenarioUI(MapTools.getInstance(), data);
        if (verify) scenario.verifySaved(); else scenario.run();
    }
}
