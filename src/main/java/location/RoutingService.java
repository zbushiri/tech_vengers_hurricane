package location;

import java.io.IOException;

/**
 * Supplies actual driving routes; callers must report unavailable routes honestly.
 * @author Team Tech-vengers
 */
@FunctionalInterface
public interface RoutingService {
    /**
     * Finds a driving route between two valid addresses.
     * @param from departure address
     * @param to destination address
     * @return driving distance and turn instructions
     * @throws IOException if a route cannot be obtained
     */
    RoutePlan route(Address from, Address to) throws IOException;
}
