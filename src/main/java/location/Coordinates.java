package location;

/**
 * coordinates in the relief application.
 * @author Team Tech-vengers
 */
public class Coordinates {
    private double latitude;
    private double longitude;

    /**
     * Creates coordinates.
     * @param latitude latitude for this operation
     * @param longitude longitude for this operation
     */
    public Coordinates(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Returns the latitude.
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude.
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Formats this object for display.
     * @return the result of to string
     */
    @Override
    public String toString() {
        return latitude + ", " + longitude;
    }
}
