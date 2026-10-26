package location;


/**
 * A street address that can be parsed, displayed and geocoded.
 * @author Team Tech-vengers
 */
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;

    /**
     * Creates a street address from its postal components.
     * @param street street number and street name
     * @param city city or town name
     * @param state state abbreviation
     * @param zip postal ZIP code
     */
    public Address(String street, String city, String state, String zip){
        this.street = street;
        this. city = city;
        this.state = state;
        this.zip = zip;
    }

    /**
     * Returns the street.
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * Returns the city.
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Returns the state.
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Returns the zip.
     * @return the zip
     */
    public String getZip() {
        return zip;
    }

    /**
     * Geocodes this address, printing any lookup failure.
     * @return coordinates returned by the mapping service, or null if lookup fails
     */
    public Coordinates getCoordinates() {
        try {
            return MapTools.getInstance().getCoordinates(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Formats this object for display.
     * @return street address formatted as street, city, state and ZIP
     */
    public String toString() {
        return String.format("%s, %s, %s %s", street, city, state, zip);
    }

    /**
     * Builds an Address from text like "2950 
     * Carner Avenue, North Charleston, SC 29405",
     * the same format toString() prints
     * 
     * @param text the stored address 
     * @return the Address, or null if the
     * text isn't in "street, city, ST zip" form
     */
    public static Address fromString(String text) {
        if (text == null) {
            return null;
        }

        // "2950 Carner Avenue" | "North Charleston" | "SC 29405"
        String[] parts = text.trim().split("\\s*,\\s*");
        if (parts.length != 3 || parts[0].isBlank() || parts[1].isBlank()) {
            return null;
        }

        // "SC" | "29405"
        String[] stateZip = parts[2].trim().split("\\s+");
        if (stateZip.length != 2) {
            return null;
        }

        return new Address(parts[0], parts[1], stateZip[0], stateZip[1]);
    }
}
