package model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Saves project data to JSON files.
 *
 * DataWriter takes Java objects and converts them into JSON
 * so changes can be saved after the program closes.
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
     * Saves all shelters to shelters.json.
     *
     * @param shelters the shelters that will be saved
     */
    @SuppressWarnings("unchecked")
    public static void saveShelters(ArrayList<Shelter> shelters) {
        JSONArray shelterArray = new JSONArray();

        // Convert each Shelter object into JSON.
        for (Shelter shelter : shelters) {
            shelterArray.add(shelterToJSON(shelter));
        }

        // Write the finished JSON array to the file.
        try (FileWriter file = new FileWriter(SHELTER_FILE)) {
            file.write(shelterArray.toJSONString());
            System.out.println("Shelters saved successfully.");
        } catch (IOException exception) {
            System.out.println("Could not save shelters.");
        }
    }

    /**
     * Converts one Shelter object into a JSON object.
     *
     * @param shelter the shelter being converted
     * @return the shelter as JSON
     */
    @SuppressWarnings("unchecked")
    private static JSONObject shelterToJSON(Shelter shelter) {
        JSONObject shelterJSON = new JSONObject();

        shelterJSON.put("shelterId", shelter.getShelterId().toString());
        shelterJSON.put("name", shelter.getName());
        shelterJSON.put("address", shelter.getAddress());
        shelterJSON.put("capacity", shelter.getCapacity());
        shelterJSON.put("occupancy", shelter.getOccupancy());
        shelterJSON.put("status", shelter.getStatus().toString().toLowerCase());

        // Save the shelter accommodations.
        JSONArray accommodations = new JSONArray();
        for (Accommodation accommodation : shelter.getAccommodations()) {
            accommodations.add(accommodation.toString().toLowerCase());
        }
        shelterJSON.put("accommodations", accommodations);

        shelterJSON.put("lastUpdated", shelter.getLastUpdated().toString());

        // Save shelter operators by username.
        JSONArray operators = new JSONArray();
        for (User operator : shelter.getShelterOperators()) {
            operators.add(operator.getUsername());
        }
        shelterJSON.put("shelterOperators", operators);

        return shelterJSON;
    }
}
