package location;

import java.util.List;

/**
 * An immutable driving route with distance in meters and printable instructions.
 * @param distanceMeters total distance along roads, not straight-line distance
 * @param instructions ordered turn instructions
 * @author Team Tech-vengers
 */
public record RoutePlan(double distanceMeters, List<String> instructions) {
    /**
     * Validates and defensively copies a route response.
     * @param distanceMeters finite, nonnegative driving distance
     * @param instructions nonempty turn instructions
     */
    public RoutePlan {
        if (!Double.isFinite(distanceMeters) || distanceMeters < 0
                || instructions == null || instructions.isEmpty()) {
            throw new IllegalArgumentException("A route needs distance and directions.");
        }
        instructions = List.copyOf(instructions);
    }
}
