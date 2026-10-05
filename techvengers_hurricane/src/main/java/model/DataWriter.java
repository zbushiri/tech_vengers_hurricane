package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;

/**
 * Saves project data to JSON files.
 *
 * @author Zaki
 */
public class DataWriter {
    private static final String SHELTER_FILE = "../json/shelters.json";

    /**
     * Prevents a DataWriter object from being created.
     */
    private DataWriter() {
    }

    /**
     * Saves a list of shelters to the shelters JSON file. Each item must be
     * converted to a JSON object before this method writes the list.
     *
     * @param shelters the shelters that will be saved
     */
    @SuppressWarnings("unchecked")
    public static void saveShelters(ArrayList<?> shelters) {
        JSONArray shelterArray = new JSONArray();

        // ? Replace the wildcard with Shelter after the Shelter class is added.
        // TODO: Convert each shelter into a JSON object.
        shelterArray.addAll(shelters);

        // ! Do not overwrite the file until the entire JSON array is ready.
        try (FileWriter file = new FileWriter(SHELTER_FILE)) {
            shelterArray.writeJSONString(file);
        } catch (IOException exception) {
            System.out.println("Could not save shelters.");
        }
    }
}
