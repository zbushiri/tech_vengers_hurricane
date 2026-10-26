package location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * MapTools Provides map api tools
 * @author Team Tech-vengers
 */
public class MapTools implements RoutingService {
    private static final String GEOCODING_URL =
        "https://api.heigit.org/pelias/v1/search";

    private static final String DIRECTIONS_URL =
        "https://api.heigit.org/openrouteservice/v2/directions/driving-car";

    private static final String ROUTE_GEOJSON_URL =
        "https://api.heigit.org/openrouteservice/v2/directions/driving-car/geojson";
    private static MapTools mapTools;
    private final String apiKey = System.getenv("HURRICANE_API");
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final HashMap<String, Coordinates> savedCoordinates = new HashMap<>();
    private final java.util.Map<String, RoutePlan> savedRoutes = new java.util.HashMap<>();

    private MapTools() {
        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        objectMapper = new ObjectMapper();
    }

    /**
     * Returns the instance.
     * @return the instance
     */
    public static MapTools getInstance() {
        if(mapTools == null){
            mapTools = new MapTools();
        }
        return mapTools;
    }

    /**
     * Converts a street address into latitude and longitude.
     * @param address Street address
     * @return Coordinates in latitude and longitude
     * @throws IOException if the mapping service or route response is unavailable
     * @throws InterruptedException if the network operation is interrupted
     */
    public Coordinates getCoordinates(Address address)
            throws IOException, InterruptedException {

        checkForApiKey();

        String addressText = address.toString();
        if (savedCoordinates.containsKey(addressText)) {
            return savedCoordinates.get(addressText);
        }

        String encodedAddress = URLEncoder.encode(
                addressText,
                StandardCharsets.UTF_8
        );

        URI uri = URI.create(
                GEOCODING_URL
                        + "?api_key=" + apiKey
                        + "&text=" + encodedAddress
                        + "&boundary.country=USA"
                        + "&size=10"
        );

        HttpRequest request = HttpRequest.newBuilder(uri)
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(15))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException(
                    "Could not convert: " + address
                            + " (HTTP " + response.statusCode() + ")"
                            + "\n" + response.body()
            );
        }

        JsonNode features = objectMapper
                .readTree(response.body())
                .path("features");

        JsonNode bestResult = findBestResult(features);

        Coordinates coordinates = readCoordinates(bestResult);
        savedCoordinates.put(addressText, coordinates);
        return coordinates;
    }

    /**
     * Finds the shelter closest to the starting address.
     * @param startingAddress The address we are driving from
     * @param shelters The list of shelters we are choosing from
     * @return The address of the closest shelter
     * @throws IOException if the mapping service or route response is unavailable
     * @throws InterruptedException if the network operation is interrupted
     */
    public Address findClosestAddress(Address startingAddress, Address[] shelters)
            throws IOException, InterruptedException {

        Address closestShelter = null;
        double shortestDistance = Double.POSITIVE_INFINITY;
        for (Address shelter : shelters) {
            double distance = route(startingAddress, shelter).distanceMeters();
            if (distance < shortestDistance) {
                shortestDistance = distance;
                closestShelter = shelter;
            }
        }

        return closestShelter;
    }

    /**
     * Collects the driving directions to the shelter
     * @param startingAddress The address the route starts at
     * @param endingAddress The address the route ends at
     * @return A string array where each item represents a direction on the route
     * @throws IOException if the mapping service or route response is unavailable
     * @throws InterruptedException if the network operation is interrupted
     */
    public String[] getDrivingDirections(
            Address startingAddress,
            Address endingAddress
    ) throws IOException, InterruptedException {

        return route(startingAddress, endingAddress).instructions().toArray(new String[0]);
    }

    /**
     * Gets a cached driving distance and directions from the route service.
     * @param from departure address
     * @param to destination address
     * @return road distance and instructions
     * @throws IOException if routing fails or returns incomplete data
     */
    @Override
    public synchronized RoutePlan route(Address from, Address to) throws IOException {
        if (from == null || to == null) throw new IOException("A valid street, city, state and ZIP are required.");
        String key = from + " -> " + to;
        if (savedRoutes.containsKey(key)) return savedRoutes.get(key);
        try {
            JsonNode data = getRoute(getCoordinates(from), getCoordinates(to));
            double meters = data.path("summary").path("distance").asDouble(-1);
            ArrayList<String> instructions = new ArrayList<>();
            for (JsonNode segment : data.path("segments")) {
                for (JsonNode step : segment.path("steps")) {
                    String instruction = step.path("instruction").asText("");
                    if (!instruction.isBlank()) instructions.add(instruction);
                }
            }
            if (meters < 0 || instructions.isEmpty()) throw new IOException("Route response is missing distance or directions.");
            RoutePlan plan = new RoutePlan(meters, instructions);
            savedRoutes.put(key, plan);
            return plan;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Routing interrupted.", e);
        } catch (IllegalStateException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    /**
     * Finds the best version of a coordinate. Based on the address we might not 
     * be able to find the exact house, but maybe the city
     * @param features The information about the street address
     * @return The node representation of the closest address
     * @throws IOException if the mapping service or route response is unavailable
     */
    private JsonNode findBestResult(JsonNode features) throws IOException {
        if (!features.isArray() || features.isEmpty()) {
            throw new IOException("No location was found.");
        }

        for (JsonNode result : features) {
            String layer = result.path("properties").path("layer").asText();

            if (layer.equals("address")) {
                return result;
            }
        }

        return features.get(0);
    }

    /**
     * Covert a node into a coordinate object
     * @param result The node representing the location
     * @return The coordinates of the node in latitude and longitude
     * @throws IOException if the mapping service or route response is unavailable
     */
    private Coordinates readCoordinates(JsonNode result) throws IOException {
        JsonNode coordinates = result.path("geometry").path("coordinates");

        if (!coordinates.isArray() || coordinates.size() < 2) {
            throw new IOException("Coordinates were missing.");
        }

        double longitude = coordinates.get(0).asDouble();
        double latitude = coordinates.get(1).asDouble();

        return new Coordinates(latitude, longitude);
    }

    /**
     * Calculates the route information from one location to the next
     * @param start Starting location
     * @param end Ending location
     * @return A json node representing the route.
     * @throws IOException if the mapping service or route response is unavailable
     * @throws InterruptedException if the network operation is interrupted
     */
    private JsonNode getRoute(Coordinates start, Coordinates end)
            throws IOException, InterruptedException {

        String requestBody =
                "{ \"coordinates\": [[" +
                        start.getLongitude() + "," + start.getLatitude() +
                        "],[" +
                        end.getLongitude() + "," + end.getLatitude() +
                        "]], \"instructions\": true, \"language\": \"en\" }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DIRECTIONS_URL))
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException("Could not get driving directions.");
        }

        return objectMapper
                .readTree(response.body())
                .path("routes")
                .path(0);
    }

    /**
     * Determines the route from one address to another and returns it to show on a map.
     * @param startingAddress   The address the route begins at
     * @param endingAddress The address the route ends on
     * @return The Driving route to be plotted on a map
     * @throws IOException if the mapping service or route response is unavailable
     * @throws InterruptedException if the network operation is interrupted
     */
    public DrivingRoute getDrivingRoute(Address startingAddress, Address endingAddress) 
        throws IOException, InterruptedException {

        Coordinates startCoordinates = getCoordinates(startingAddress);
        Coordinates endCoordinates = getCoordinates(endingAddress);

        ArrayList<Coordinates> routePoints = getRoutePoints(
                startCoordinates,
                endCoordinates
        );

        MapLocation startingLocation = new MapLocation(
                startingAddress,
                startCoordinates
        );

        MapLocation endingLocation = new MapLocation(
                endingAddress,
                endCoordinates
        );

        return new DrivingRoute(
                startingLocation,
                endingLocation,
                routePoints
        );
    }

    /**
     * Determines all the points to show on the route to be plotted on a map
     * @param start The address to begin on as a coordinate
     * @param end The address to end at as a coordinate
     * @return A list of all points to be put on the route
     * @throws IOException if the mapping service or route response is unavailable
     * @throws InterruptedException if the network operation is interrupted
     */
    private ArrayList<Coordinates> getRoutePoints(Coordinates start,Coordinates end
    ) throws IOException, InterruptedException {

        String requestBody =
                "{ \"coordinates\": [[" +
                        start.getLongitude() + "," + start.getLatitude() +
                        "],[" +
                        end.getLongitude() + "," + end.getLatitude() +
                        "]]}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ROUTE_GEOJSON_URL))
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(15))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() != 200) {
            throw new IOException("Could not get the driving route.");
        }

        JsonNode points = objectMapper
                .readTree(response.body())
                .path("features")
                .path(0)
                .path("geometry")
                .path("coordinates");

        ArrayList<Coordinates> routePoints = new ArrayList<>();

        for (JsonNode point : points) {
            double longitude = point.get(0).asDouble();
            double latitude = point.get(1).asDouble();

            routePoints.add(new Coordinates(latitude, longitude));
        }

        return routePoints;
    }

    /**
     * Determine if we have a valid API key in our environment variable.
     */
    private void checkForApiKey() {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "HURRICANE_API environment variable has not been set."
            );
        }
    }
}
