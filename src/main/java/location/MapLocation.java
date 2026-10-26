package location;

/*
 * Represents one address after it has been converted
 * into latitude and longitude coordinates.
 */
/**
 * A street address paired with its geographic coordinates.
 * @author Team Tech-vengers
 */
public class MapLocation {

    private final Address address;
    private final Coordinates coordinates;

    /**
     * Pairs a street address with the coordinates found for that address.
     * @param address street address represented by this map location
     * @param coordinates latitude and longitude associated with the address
     */
    public MapLocation(Address address, Coordinates coordinates) {
        this.address = address;
        this.coordinates = coordinates;
    }

    /**
     * Returns the address.
     * @return the address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Returns the coordinates.
     * @return the coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Formats this object for display.
     * @return formatted street address of this location
     */
    @Override
    public String toString() {
        return address.toString();
    }
}
