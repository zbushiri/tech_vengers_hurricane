package location;

/*
 * Represents one address after it has been converted
 * into latitude and longitude coordinates.
 */
/**
 * map location in the relief application.
 * @author Team Tech-vengers
 */
public class MapLocation {

    private final Address address;
    private final Coordinates coordinates;

    /**
     * Creates map location.
     * @param address address for this operation
     * @param coordinates coordinates for this operation
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
     * @return the result of to string
     */
    @Override
    public String toString() {
        return address.toString();
    }
}
