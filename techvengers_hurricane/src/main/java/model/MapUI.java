package model;

import location.Address;
import location.MapTools;

/** Tests the map tools with example shelter addresses. */
public class MapUI {

    /** Finds the closest shelter and displays directions. */
    public void run() {
        Address userAddress = new Address(
                "1 Vendue Range",
                "Charleston",
                "SC",
                "29401"
        );

        Address[] shelters = {
                new Address("2950 Carner Avenue", "North Charleston", "SC", "29405"),
                new Address("5200 Lackawanna Boulevard", "North Charleston", "SC", "29405"),
                new Address("3841 Leeds Avenue", "North Charleston", "SC", "29405"),
                new Address("3625 Ashley Phosphate Road", "North Charleston", "SC", "29418"),
                new Address("6401 Dorchester Road", "North Charleston", "SC", "29418"),
                new Address("1624 State Road", "Summerville", "SC", "29483"),
                new Address("1053 Russellville Road", "St. Stephen", "SC", "29479"),
                new Address("200 Macedonia Foxes Circle", "St. Stephen", "SC", "29479"),
                new Address("101 Westview Boulevard", "Goose Creek", "SC", "29445"),
                new Address("1101 Boone Hill Road", "Summerville", "SC", "29483")
        };

        try {
            MapTools mapTools = MapTools.getInstance();
            Address closestShelter = mapTools.findClosestAddress(userAddress, shelters);

            System.out.println("User address: " + userAddress);
            System.out.println("Closest shelter: " + closestShelter);

            String[] directions = mapTools.getDrivingDirections(userAddress, closestShelter);
            for (String direction : directions) {
                System.out.println(direction);
            }
        } catch (Exception e) {
            System.out.println("There was a problem finding a shelter.");
            e.printStackTrace();
        }
    }

    /** Starts the map example. */
    public static void main(String[] args) {
        new MapUI().run();
    }
}
