package com.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import location.Address;
import location.RoutePlan;
import location.RoutingService;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the RequestRecommendation list that HurricaneReliefApp builds for a
 * signed-in volunteer. This person will get recommendations, which requests are recommended,
 * why it is and distance, and the order they are listed in.
 * Driving distances come from a fixed test route table.
 */
public class RequestRecommendationTest {
    private static final double METERS_PER_MILE = 1609.344;
    private static final double RADIUS_METERS = 25 * METERS_PER_MILE;

    private static final String HOME = "1200 Main Street, Columbia, SC 29201";
    private static final String CLOSE = "200 Close Avenue, Columbia, SC 29201";
    private static final String NEAR = "100 Near Street, Columbia, SC 29201";
    private static final String TWIN = "300 Twin Street, Columbia, SC 29201";
    private static final String EDGE = "25 Edge Lane, Lexington, SC 29072";
    private static final String PAST_EDGE = "26 Edge Lane, Lexington, SC 29072";
    private static final String FAR = "900 Far Road, Charleston, SC 29401";
    private static final String OFFLINE = "1 Offline Court, Sumter, SC 29150";

    private static final Map<String, Double> TEST_DISTANCES = Map.of(
            "200 Close Avenue", 3 * METERS_PER_MILE,
            "100 Near Street", 5 * METERS_PER_MILE,
            "300 Twin Street", 5 * METERS_PER_MILE,
            "25 Edge Lane", RADIUS_METERS,
            "26 Edge Lane", RADIUS_METERS + 1,
            "900 Far Road", 100 * METERS_PER_MILE);

    private final List<Address> departures = new ArrayList<>();
    private final RoutingService routes = (from, to) -> {
        if (from == null || to == null || !TEST_DISTANCES.containsKey(to.getStreet())) {
            throw new IOException("TEST ROUTE ONLY: no route");
        }
        departures.add(from);
        return new RoutePlan(TEST_DISTANCES.get(to.getStreet()), List.of("TEST ROUTE ONLY: drive to " + to));
    };

    private HurricaneReliefApp app;
    private VolunteerProfile volunteer;

    @Before
    public void setUp() {
        UserList.getInstance().getUsers().clear();
        ReliefRequestCatalog.getInstance().getRequests().clear();
        volunteer = new VolunteerProfile(user("mlopez", AccountRole.VOLUNTEER),
                new ArrayList<>(List.of("FOOD", "SUPPLIES")), new ArrayList<>(List.of("Pickup truck")),
                BackgroundCheckStatus.VERIFIED, true, AvailabilityStatus.AVAILABLE);
        UserList.getInstance().addUser(volunteer);
        UserList.getInstance().addUser(user("dcarter", AccountRole.RESIDENT));
        app = new HurricaneReliefApp(routes);
    }

    @After
    public void tearDown() {
        UserList.getInstance().getUsers().clear();
        ReliefRequestCatalog.getInstance().getRequests().clear();
    }

    private static User user(String username, AccountRole role) {
        return new User("Test", "User", username + "@example.com", "803-555-0100", HOME,
                username, "password1", new ArrayList<>(List.of(role)), HOME);
    }

    private static ReliefRequest openRequest(RequestType type, String location, int people) {
        ReliefRequest request = new ReliefRequest(type, "Test request", location, people);
        ReliefRequestCatalog.getInstance().addLoadedRequest(request);
        return request;
    }

    private List<RequestRecommendation> recommendationsForVolunteer() {
        app.login("mlopez", "password1");
        return app.getRecommendedRequests();
    }

    private RequestRecommendation onlyRecommendationFor(RequestType type, String location) {
        openRequest(type, location, 2);
        return recommendationsForVolunteer().get(0);
    }

    // who gets recommendations

    @Test
    public void signedOutUserGetsNoRecommendations() {
        openRequest(RequestType.FOOD, NEAR, 2);
        assertTrue(app.getRecommendedRequests().isEmpty());
    }

    @Test
    public void residentGetsNoRecommendations() {
        openRequest(RequestType.FOOD, NEAR, 2);
        app.login("dcarter", "password1");
        assertTrue(app.getRecommendedRequests().isEmpty());
    }

    @Test
    public void volunteerWithNoOpenRequestsGetsEmptyList() {
        assertTrue(recommendationsForVolunteer().isEmpty());
    }

    // which requests are listed

    @Test
    public void everyOpenRequestIsListed() {
        openRequest(RequestType.FOOD, NEAR, 2);
        openRequest(RequestType.TRANSPORTATION, CLOSE, 1);
        openRequest(RequestType.MEDICAL, FAR, 1);
        assertEquals(3, recommendationsForVolunteer().size());
    }

    @Test
    public void cancelledRequestIsNotListed() {
        openRequest(RequestType.FOOD, NEAR, 2).cancel();
        assertTrue(recommendationsForVolunteer().isEmpty());
    }

    @Test
    public void assignedRequestStaysListed() {
        ReliefRequest taken = openRequest(RequestType.FOOD, NEAR, 2);
        taken.claimRequest(new VolunteerProfile(user("other", AccountRole.VOLUNTEER), new ArrayList<>(),
                new ArrayList<>(), BackgroundCheckStatus.VERIFIED, true, AvailabilityStatus.AVAILABLE));
        assertSame(taken, recommendationsForVolunteer().get(0).request());
    }

    // which requests are recommended

    @Test
    public void nearbySpecialtyMatchIsRecommended() {
        assertTrue(onlyRecommendationFor(RequestType.FOOD, NEAR).recommended());
    }

    @Test
    public void specialtyMatchIgnoresSkillCase() {
        volunteer.setSkills(new ArrayList<>(List.of("food")));
        assertTrue(onlyRecommendationFor(RequestType.FOOD, NEAR).recommended());
    }

    @Test
    public void nearbyRequestOutsideSpecialtyIsNotRecommended() {
        assertFalse(onlyRecommendationFor(RequestType.TRANSPORTATION, NEAR).recommended());
    }

    @Test
    public void farSpecialtyMatchIsNotRecommended() {
        assertFalse(onlyRecommendationFor(RequestType.FOOD, FAR).recommended());
    }

    @Test
    public void requestExactlyAtRadiusIsRecommended() {
        assertTrue(onlyRecommendationFor(RequestType.FOOD, EDGE).recommended());
    }

    @Test
    public void requestJustPastRadiusIsNotRecommended() {
        assertFalse(onlyRecommendationFor(RequestType.FOOD, PAST_EDGE).recommended());
    }

    @Test
    public void unreachableRequestIsNotRecommended() {
        assertFalse(onlyRecommendationFor(RequestType.FOOD, OFFLINE).recommended());
    }

    @Test
    public void requestAssignedToSomeoneElseIsNotRecommended() {
        ReliefRequest taken = openRequest(RequestType.FOOD, NEAR, 2);
        taken.claimRequest(new VolunteerProfile(user("other", AccountRole.VOLUNTEER), new ArrayList<>(),
                new ArrayList<>(), BackgroundCheckStatus.VERIFIED, true, AvailabilityStatus.AVAILABLE));
        assertFalse(recommendationsForVolunteer().get(0).recommended());
    }

    @Test
    public void duplicateOfOpenRequestIsNotRecommended() {
        ReliefRequest original = openRequest(RequestType.FOOD, NEAR, 2);
        ReliefRequest duplicate = openRequest(RequestType.FOOD, TWIN, 2);
        duplicate.setIsDuplicateOf(original);
        RequestRecommendation forDuplicate = recommendationsForVolunteer().stream()
                .filter(r -> r.request() == duplicate).findFirst().orElseThrow();
        assertFalse(forDuplicate.recommended());
    }

    // why and distances

    @Test
    public void nearbySpecialtyMatchExplainsSpecialtyAndDistance() {
        assertEquals("Matches your food specialty; nearby at 5.0 driving miles.",
                onlyRecommendationFor(RequestType.FOOD, NEAR).explanation());
    }

    @Test
    public void farRequestExplainsThereIsNoNearbyMatch() {
        assertEquals("No nearby specialty match.", onlyRecommendationFor(RequestType.FOOD, FAR).explanation());
    }

    @Test
    public void requestOutsideSpecialtyExplainsThereIsNoNearbyMatch() {
        assertEquals("No nearby specialty match.",
                onlyRecommendationFor(RequestType.TRANSPORTATION, NEAR).explanation());
    }

    @Test
    public void unreachableRequestExplainsDistanceIsUnavailable() {
        assertEquals("Driving distance unavailable; proximity cannot be verified.",
                onlyRecommendationFor(RequestType.FOOD, OFFLINE).explanation());
    }

    @Test
    public void recommendationReportsDrivingDistance() {
        assertEquals(5 * METERS_PER_MILE, onlyRecommendationFor(RequestType.FOOD, NEAR).distanceMeters(), 0.001);
    }

    @Test
    public void unreachableRequestHasInfiniteDistance() {
        assertEquals(Double.POSITIVE_INFINITY,
                onlyRecommendationFor(RequestType.FOOD, OFFLINE).distanceMeters(), 0);
    }

    // departure addresses

    @Test
    public void distanceIsMeasuredFromLastKnownLocation() {
        volunteer.setLastKnownLocation(CLOSE);
        onlyRecommendationFor(RequestType.FOOD, NEAR);
        assertEquals("200 Close Avenue", departures.get(0).getStreet());
    }

    @Test
    public void coordinateLocationFallsBackToAccountAddress() {
        volunteer.setLastKnownLocation("34.0007,-81.0348");
        onlyRecommendationFor(RequestType.FOOD, NEAR);
        assertEquals("1200 Main Street", departures.get(0).getStreet());
    }

    // ordering

    @Test
    public void recommendedRequestsAreListedFirst() {
        openRequest(RequestType.MEDICAL, NEAR, 1);
        ReliefRequest match = openRequest(RequestType.FOOD, CLOSE, 2);
        assertSame(match, recommendationsForVolunteer().get(0).request());
    }

    @Test
    public void higherPriorityIsListedFirstAmongRecommended() {
        openRequest(RequestType.FOOD, CLOSE, 2);
        ReliefRequest bigGroup = openRequest(RequestType.FOOD, NEAR, 10);
        assertSame(bigGroup, recommendationsForVolunteer().get(0).request());
    }

    @Test
    public void closerRequestIsListedFirstWhenPriorityTies() {
        openRequest(RequestType.FOOD, NEAR, 2);
        ReliefRequest closer = openRequest(RequestType.FOOD, CLOSE, 2);
        assertSame(closer, recommendationsForVolunteer().get(0).request());
    }

    @Test
    public void olderRequestIsListedFirstWhenPriorityAndDistanceTie() {
        ReliefRequest newer = openRequest(RequestType.FOOD, NEAR, 2);
        ReliefRequest older = openRequest(RequestType.FOOD, TWIN, 2);
        newer.setDateSubmitted(new Date(2_000_000_000_000L));
        older.setDateSubmitted(new Date(1_000_000_000_000L));
        assertSame(older, recommendationsForVolunteer().get(0).request());
    }
}
