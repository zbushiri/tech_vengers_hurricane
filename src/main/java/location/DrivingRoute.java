package location;

import java.util.ArrayList;

/**
 * driving route in the relief application.
 * @author Team Tech-vengers
 */
public class DrivingRoute {

    private final MapLocation startingLocation;
    private final MapLocation endingLocation;
    private final ArrayList<Coordinates> routePoints;

    /**
     * Creates driving route.
     * @param startingLocation starting location for this operation
     * @param endingLocation ending location for this operation
     * @param routePoints route points for this operation
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
