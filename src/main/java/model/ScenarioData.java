package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import location.MapTools;

/**
 * Handles the walkthrough's data folder, JSON snapshots and restart verification.
 * Keeps demonstration setup separate from the story presented by {@link ScenarioUI}.
 *
 * @author Team Tech-vengers
 */
final class ScenarioData {
    private static final List<String> JSON_FILES = List.of("users", "shelters", "requests", "hurricanes");
    private final Path directory;

    /**
     * Records the folder used for this demonstration's saved files.
     * @param directory folder containing the demonstration's JSON files
     */
    ScenarioData(Path directory) {
        this.directory = directory;
    }

    /** Rejects previously run or incomplete seed data before the presentation starts. */
    void checkStartingData() {
        if (UserList.getInstance().getUser("dcarter") != null) {
            throw new IllegalStateException("Carter already exists. Use --verify or choose a fresh --data-dir folder.");
        }
        System.out.println("Starting with " + ReliefRequestCatalog.getInstance().getOpenRequestsByUrgency().size()
                + " open sample requests.");
    }

    /**
     * Saves the application and copies each JSON file into the named snapshot folder.
     * @param app application whose current data should be saved
     * @param phase snapshot label, such as before, assigned or after
     * @throws IOException if saving or copying a JSON file fails
     */
    void snapshot(HurricaneReliefApp app, String phase) throws IOException {
        if (!app.saveData()) {
            throw new IOException("Could not save the JSON files.");
        }
        System.out.println("JSON files saved.");
        Path snapshot = directory.resolve("snapshots").resolve(phase);
        Files.createDirectories(snapshot);
        for (String name : JSON_FILES) {
            Files.copy(directory.resolve(name + ".json"), snapshot.resolve(name + ".json"),
                    StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Returns the folder for this demonstration's printable trip sheets.
     * @return trip-sheets folder within the demonstration data directory
     */
    Path tripSheetDirectory() {
        return directory.resolve("trip-sheets");
    }

    /** Explains how to demonstrate persistence in a fresh application process. */
    void printRestartInstructions() {
        System.out.println("\nRestart Scenario with --verify --data-dir=" + directory);
        System.out.println("JSON snapshots: " + directory.resolve("snapshots"));
    }

    /**
     * Shows the reloaded household and reports whether the scenario results were saved.
     * @param app application initialized from the saved demonstration files
     * @return true if completion, assignment, household and shelter admission were restored
     */
    boolean verifySaved(HurricaneReliefApp app) {
        System.out.println("Carter account reloaded: " + app.login("dcarter", "carter2026"));
        ReliefRequest request = app.getMyRequests().stream()
                .filter(saved -> saved.getDestinationShelterId() != null).findFirst().orElse(null);
        if (request == null) {
            System.out.println("No saved Carter request was found.");
            app.logout();
            return false;
        }
        Shelter shelter = ShelterList.getInstance().getShelters().stream()
                .filter(saved -> saved.getShelterId().equals(request.getDestinationShelterId()))
                .findFirst().orElse(null);

        boolean completed = request.getStatus() == RequestStatus.FULFILLED;
        boolean assigned = request.getAssignedVolunteer() != null
                && "rosadelgado".equals(request.getAssignedVolunteer().getUsername());
        boolean householdSaved = request.getFamilyMembers().size() == 3
                && request.getPets().equals(List.of(new Pet("Buddy", "dog")));
        boolean checkedIn = shelter != null && shelter.hasAdmission(request.getRequestId())
                && shelter.getPets().contains(new Pet("Buddy", "dog"))
                && shelter.getOccupants().stream().anyMatch(person -> "Sophie".equals(person.getFirstName()));

        System.out.println("Completed status saved: " + completed);
        System.out.println("Rosa's assignment saved: " + assigned);
        System.out.println("Family and Buddy saved: " + householdSaved);
        System.out.println("Shelter check-in saved: " + checkedIn);
        boolean saved = completed && assigned && householdSaved && checkedIn;
        System.out.println(saved ? "Restart verified: all scenario results were saved."
                : "Some scenario results were not saved.");
        app.logout();
        return saved;
    }

    /**
     * Parses launch options, prepares isolated seed files and selects the demonstration mode.
     * @param args optional --verify and --data-dir=folder launch arguments
     * @throws IOException if seed files, snapshots, trip exports or driving routes are unavailable
     */
    static void launch(String[] args) throws IOException {
        Path directory = Path.of("demo-data").toAbsolutePath();
        boolean verify = false;
        for (String argument : args) {
            if (argument.startsWith("--data-dir=")) {
                directory = Path.of(argument.substring("--data-dir=".length())).toAbsolutePath();
            } else if (argument.equals("--verify")) {
                verify = true;
            } else {
                throw new IllegalArgumentException("Unknown option: " + argument);
            }
        }

        if (!verify) {
            String apiKey = System.getenv("HURRICANE_API");
            if (apiKey == null || apiKey.isBlank()) {
                System.out.println("Set HURRICANE_API in the root .env file for VS Code, or export it in your shell.");
                return;
            }
        }
        System.setProperty("hurricane.dataDir", directory.toString());
        prepareDirectory(directory, verify);

        ScenarioUI scenario = new ScenarioUI(MapTools.getInstance(), directory);
        if (verify) {
            scenario.verifySaved();
        } else {
            scenario.run();
        }
    }

    /** Copies the seed files only when creating a new demonstration folder. */
    private static void prepareDirectory(Path directory, boolean verify) throws IOException {
        if (Files.exists(directory)) {
            return;
        }
        if (verify) {
            throw new IOException("No saved demonstration at " + directory);
        }
        Files.createDirectories(directory);
        for (String name : JSON_FILES) {
            Files.copy(Path.of("json", name + ".json"), directory.resolve(name + ".json"));
        }
    }
}
