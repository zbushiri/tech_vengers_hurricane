package location;

/**
 * A geographic location represented by latitude and longitude in decimal degrees.
 * @author Team Tech-vengers
 */
public class Coordinates {
    private double latitude;
    private double longitude;

    /**
     * Stores a latitude and longitude pair.
     * @param latitude latitude in decimal degrees
     * @param longitude longitude in decimal degrees
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
     * @return latitude and longitude separated by a comma
     */
    @Override
    public String toString() {
        return latitude + ", " + longitude;
    }
}
