package location;


/**
 * address in the relief application.
 * @author Team Tech-vengers
 */
public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;

    /**
     * Creates address.
     * @param street street for this operation
     * @param city city for this operation
     * @param state state for this operation
     * @param zip zip for this operation
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
     * Returns the coordinates.
     * @return the coordinates
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
     * @return the result of to string
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
