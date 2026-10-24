package location;


public class Address {
    private String street;
    private String city;
    private String state;
    private String zip;

    public Address(String street, String city, String state, String zip){
        this.street = street;
        this. city = city;
        this.state = state;
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public Coordinates getCoordinates() {
        try {
            return MapTools.getInstance().getCoordinates(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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
        String[] parts = text.split(", ");
        if (parts.length != 3) {
            return null;
        }

        // "SC" | "29405"
        String[] stateZip = parts[2].split(" ");
        if (stateZip.length != 2) {
            return null;
        }

        return new Address(parts[0], parts[1], stateZip[0], stateZip[1]);
    }
}
