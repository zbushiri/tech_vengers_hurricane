package com.techvengershurricane;

import com.techvengershurricane.system.HurricaneReliefSystem;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Driver {
    private Driver() {
        /* Driver: this class should not be instantiated. */
    }

    public static void main(String[] args) {
        /* Driver: loads sample data without opening JavaFX. */
        Path jsonDirectory = args.length > 0 ? Path.of(args[0]) : findJsonDirectory();
        HurricaneReliefSystem system = new HurricaneReliefSystem(jsonDirectory);

        /* Log: quick proof that each JSON file is connected. */
        System.out.println("Gamecock Relief Network data check");
        System.out.println("Users: " + system.getUsers().size());
        System.out.println("Volunteers: " + system.getVolunteers().size());
        System.out.println("Shelters: " + system.getShelters().size());
        System.out.println("Requests: " + system.getRequests().size());
        System.out.println("Hurricanes: " + system.getHurricanes().size());
    }

    private static Path findJsonDirectory() {
        /* Driver: supports running from the repo or module folder. */
        Path fromModule = Path.of("..", "json");
        return Files.isDirectory(fromModule) ? fromModule : Path.of("json");
    }
}
