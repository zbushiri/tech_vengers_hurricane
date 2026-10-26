package location;

import java.util.ArrayList;

/**
 * Departure and destination locations with coordinates used to draw a driving route.
 * @author Team Tech-vengers
 */
public class DrivingRoute {

    private final MapLocation startingLocation;
    private final MapLocation endingLocation;
    private final ArrayList<Coordinates> routePoints;

    /**
     * Creates a drawable route with departure, destination and route coordinates.
     * @param startingLocation departure address and its coordinates
     * @param endingLocation destination address and its coordinates
     * @param routePoints ordered coordinates forming the driving route
     */
    public DrivingRoute(
            MapLocation startingLocation,
            MapLocation endingLocation,
            ArrayList<Coordinates> routePoints
    ) {
        this.startingLocation = startingLocation;
        this.endingLocation = endingLocation;
        this.routePoints = routePoints;
    }

    /**
     * Returns the starting location.
     * @return the starting location
     */
    public MapLocation getStartingLocation() {
        return startingLocation;
    }

    /**
     * Returns the ending location.
     * @return the ending location
     */
    public MapLocation getEndingLocation() {
        return endingLocation;
    }

    /**
     * Returns the route points.
     * @return the route points
     */
    public ArrayList<Coordinates> getRoutePoints() {
        return routePoints;
    }
}
