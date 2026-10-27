package com.model;

import static org.junit.Assert.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import location.Address;
import location.RoutePlan;
import location.RoutingService;
import model.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/** Exercises the actual scenario services with deterministic, explicitly fictional test routes. */
public class ScenarioWorkflowTest {
    private static Path data;
    private HurricaneReliefApp app;
    private final RoutingService routes = (from, to) -> {
        if (from == null || to == null) throw new IOException("Invalid test address");
        double meters = to.getStreet().contains("Lackawanna") ? 10000
                : to.getStreet().contains("State Road") ? 20000
                : to.getStreet().contains("Russellville") ? 30000 : 1200;
        return new RoutePlan(meters, List.of("TEST ROUTE ONLY: depart " + from, "TEST ROUTE ONLY: arrive " + to));
    };

    @BeforeClass public static void isolateData() throws IOException {
        data = Files.createTempDirectory("hurricane-workflow-test-");
        System.setProperty("hurricane.dataDir", data.toString());
    }

    @Before public void loadFreshSeeds() throws IOException {
        UserList.getInstance().getUsers().clear();
        ShelterList.getInstance().getShelters().clear();
        ReliefRequestCatalog.getInstance().getRequests().clear();
        for (String name : List.of("users", "shelters", "requests", "hurricanes"))
            Files.copy(Path.of("json", name + ".json"), data.resolve(name + ".json"), StandardCopyOption.REPLACE_EXISTING);
        app = new HurricaneReliefApp(routes); app.initialize();
    }

    private ReliefRequest createHousehold() {
        String home = "25 Calhoun Street, Charleston, SC 29401";
        assertTrue(app.createAccount("David", "Carter", "dcarter@example.com", "803-555-0142", home,
                "dcarter", "carter2026", new ArrayList<>(List.of(AccountRole.RESIDENT)), home));
        assertTrue(app.login("dcarter", "carter2026"));
        ReliefRequest request = app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Flooding; Sophie needs help", home,
                List.of(new Person("David", "Carter", 38, new ArrayList<>()),
                        new Person("Elena", "Carter", 36, new ArrayList<>()),
                        new Person("Sophie", "Carter", 9, new ArrayList<>(List.of("Asthma")))),
                List.of(new Pet("Buddy", "dog")), "Sophie needs asthma care");
        assertNotNull(request); app.logout(); return request;
    }

    private Shelter assign(ReliefRequest request) throws IOException {
        assertTrue(app.login("rosadelgado", "rosa2026"));
        Shelter shelter = app.findShelter(request);
        assertNotNull(shelter); assertTrue(app.claimRequest(request.getRequestId()));
        assertTrue(app.selectShelter(request.getRequestId(), shelter)); return shelter;
    }

    @Test public void medicalTransportHasHighPriorityAndStructuredPet() {
        ReliefRequest r = createHousehold(); assertEquals(PriorityLevel.HIGH, r.getPriority());
        assertEquals(3, r.getFamilyMembers().size()); assertEquals(1, r.getNumberOfAnimals());
        assertEquals("Buddy", r.getPets().get(0).name());
    }

    @Test public void sevenRequestsRecommendNearbyTransportationWithExplanation() {
        ReliefRequest r = createHousehold(); assertTrue(app.login("rosadelgado", "rosa2026"));
        List<RequestRecommendation> recommendations = app.getRecommendedRequests();
        assertEquals(7, recommendations.size()); assertSame(r, recommendations.get(0).request());
        assertTrue(recommendations.get(0).recommended());
        assertTrue(recommendations.get(0).explanation().contains("transportation"));
        assertTrue(recommendations.get(0).explanation().contains("driving miles"));
    }

    @Test public void unavailableRoutingNeverClaimsProximityIsKnown() {
        createHousehold(); HurricaneReliefApp offline = new HurricaneReliefApp((a,b) -> { throw new IOException("offline"); });
        assertTrue(offline.login("rosadelgado", "rosa2026"));
        assertTrue(offline.getRecommendedRequests().stream().noneMatch(RequestRecommendation::recommended));
        assertTrue(offline.getRecommendedRequests().get(0).explanation().contains("unavailable"));
    }

    @Test public void shelterSelectionUsesDrivingDistanceInsteadOfListOrder() throws IOException {
        ReliefRequest r = createHousehold();
        Shelter state = ShelterList.getInstance().getShelters().stream().filter(s -> s.getName().equals("State Road Shelter")).findFirst().orElseThrow();
        HurricaneReliefApp alternateRoads = new HurricaneReliefApp((a,b) -> new RoutePlan(
                b.getStreet().equals("1624 State Road") ? 100 : 10000, List.of("TEST directions")));
        assertSame(state, alternateRoads.findShelter(r));
    }

    @Test public void excludesFullSheltersMissingServicesAndInsufficientPetSpace() throws IOException {
        ReliefRequest r = createHousehold();
        Shelter selected = app.findShelter(r); assertEquals("Lackawanna Community Shelter", selected.getName());
        assertTrue(selected.getAccommodations().containsAll(List.of(Accommodation.PET_FRIENDLY, Accommodation.MEDICAL_SUPPORT)));
        selected.setPetCapacity(0); assertNotSame(selected, app.findShelter(r));
        for (Shelter s : ShelterList.getInstance().getShelters()) s.setPetCapacity(0);
        assertNull(app.findShelter(r));
    }

    @Test public void unavailableShelterRoutesFailInsteadOfReturningFirstMatch() {
        ReliefRequest r = createHousehold();
        HurricaneReliefApp offline = new HurricaneReliefApp((a,b) -> { throw new IOException("offline"); });
        assertThrows(IOException.class, () -> offline.findShelter(r));
    }

    @Test public void volunteerCannotCompleteThroughFacadeOrModel() throws IOException {
        ReliefRequest r = createHousehold(); assign(r);
        assertFalse(app.completeRequest(r.getRequestId())); assertFalse(r.completeRequest(app.getCurrentUser()));
        assertEquals(RequestStatus.ASSIGNED, r.getStatus()); assertEquals("rosadelgado", r.getAssignedVolunteer().getUsername());
    }

    @Test public void managerCannotCompleteBeforeCheckInOrAtAnotherShelter() throws IOException {
        ReliefRequest r = createHousehold(); Shelter shelter = assign(r); app.logout();
        assertTrue(app.login("felixgrant", "felix2026")); assertFalse(app.completeRequest(r.getRequestId()));
        User other = new User("other-manager", "pw"); other.getRoles().add(AccountRole.SHELTER_OPERATOR);
        assertFalse(shelter.checkIn(r, other)); assertFalse(r.completeRequest(other));
    }

    @Test public void checkInAddsThreeHumansOnePetAndCannotBeRepeated() throws IOException {
        ReliefRequest r = createHousehold(); Shelter s = assign(r); int occupancy = s.getOccupancy();
        assertFalse(app.checkIn(r.getRequestId(), s)); app.logout(); assertTrue(app.login("felixgrant", "felix2026"));
        assertTrue(app.checkIn(r.getRequestId(), s)); assertEquals(occupancy + 3, s.getOccupancy());
        assertEquals(3, s.getOccupants().size()); assertEquals(List.of(new Pet("Buddy", "dog")), s.getPets());
        assertFalse(app.checkIn(r.getRequestId(), s)); assertEquals(occupancy + 3, s.getOccupancy());
        assertTrue(app.completeRequest(r.getRequestId())); assertEquals(RequestStatus.FULFILLED, r.getStatus());
        assertEquals(AvailabilityStatus.AVAILABLE, r.getAssignedVolunteer().getAvailabilityStatus());
    }

    @Test public void checkInRechecksCapacityAtArrival() throws IOException {
        ReliefRequest r = createHousehold(); Shelter s = assign(r); app.logout(); app.login("felixgrant", "felix2026");
        s.setOccupancy(s.getCapacity() - 2); assertFalse(app.checkIn(r.getRequestId(), s)); assertFalse(s.hasAdmission(r.getRequestId()));
    }

    @Test public void assignmentRemainsVisibleAndCannotBeClaimedTwice() throws IOException {
        ReliefRequest r = createHousehold(); assign(r);
        assertTrue(app.getRecommendedRequests().stream().anyMatch(m -> m.request() == r && !m.recommended()));
        assertFalse(app.claimRequest(r.getRequestId()));
    }

    @Test public void tripSheetContainsBothLegsNamesAgesAndPetAndDoesNotOverwrite() throws IOException {
        ReliefRequest r = createHousehold(); assign(r);
        Path first = app.exportTripSheet(r.getRequestId(), data.resolve("trip-sheets"));
        Path second = app.exportTripSheet(r.getRequestId(), data.resolve("trip-sheets"));
        assertNotEquals(first, second); String text = Files.readString(first);
        for (String expected : List.of("David Carter, age 38", "Elena Carter, age 36", "Sophie Carter, age 9", "Buddy (dog)",
                "803-555-0142", "asthma", "Volunteer to pickup", "Pickup to shelter", "Lackawanna Community Shelter", "1 Vendue Range"))
            assertTrue("Missing " + expected, text.contains(expected));
    }

    @Test public void datesDoNotShiftToPreviousDayInEasternTime() {
        java.util.TimeZone old = java.util.TimeZone.getDefault();
        try { java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("America/New_York"));
            assertTrue(app.getActiveHurricane().toString().contains("startDate=2026-10-21"));
            assertTrue(app.getActiveHurricane().toString().contains("endDate=2026-10-28"));
        } finally { java.util.TimeZone.setDefault(old); }
    }

    @Test public void rejectsInvalidHouseholdAndToleratesAddressWhitespace() {
        app.login("rosadelgado", "rosa2026");
        assertNull(app.submitHouseholdRequest(RequestType.TRANSPORTATION, "help", "address", List.of(), List.of(), ""));
        assertNotNull(Address.fromString("25 Calhoun Street,Charleston, SC   29401"));
        assertNull(Address.fromString("Charleston"));
    }

    @Test public void distantSpecialtyMatchIsNotRecommended() {
        createHousehold();
        HurricaneReliefApp far = new HurricaneReliefApp((a,b) -> new RoutePlan(100000, List.of("TEST long route")));
        assertTrue(far.login("rosadelgado", "rosa2026"));
        assertTrue(far.getRecommendedRequests().stream().noneMatch(RequestRecommendation::recommended));
    }

    @Test public void anotherVolunteerCanSeeRosasAssignment() throws IOException {
        ReliefRequest request = createHousehold(); assign(request); app.logout();
        User account = new User("observer", "pw"); account.setAddress("80 Broad Street, Charleston, SC 29401");
        VolunteerProfile other = new VolunteerProfile(account, List.of("transportation"), List.of(),
                BackgroundCheckStatus.VERIFIED, true, AvailabilityStatus.AVAILABLE);
        assertTrue(UserList.getInstance().addUser(other)); assertTrue(app.login("observer", "pw"));
        RequestRecommendation visible = app.getRecommendedRequests().stream().filter(m -> m.request() == request).findFirst().orElseThrow();
        assertEquals("rosadelgado", visible.request().getAssignedVolunteer().getUsername());
        assertFalse(visible.recommended()); assertFalse(app.claimRequest(request.getRequestId()));
    }

    @Test public void checkInRechecksPetCapacityAtArrival() throws IOException {
        ReliefRequest request = createHousehold(); Shelter shelter = assign(request); app.logout();
        assertTrue(app.login("felixgrant", "felix2026")); shelter.setPetCapacity(0);
        assertFalse(app.checkIn(request.getRequestId(), shelter)); assertEquals(90, shelter.getOccupancy());
    }

    @Test public void fullScenarioAndFreshJvmReloadPreserveAllRecords() throws Exception {
        new ScenarioUI(routes, data).run();
        assertTrue(Files.exists(data.resolve("snapshots/before/users.json")));
        assertTrue(Files.exists(data.resolve("snapshots/assigned/requests.json")));
        assertTrue(Files.exists(data.resolve("snapshots/checked-in/shelters.json")));
        Path output = data.resolve("restart.log");
        // Maven uses the classpath; VS Code may put the application and its dependencies on the module path.
        String classpath = String.join(java.io.File.pathSeparator,
                Path.of(ScenarioUI.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toString(),
                System.getProperty("surefire.test.class.path", System.getProperty("java.class.path", "")),
                System.getProperty("jdk.module.path", ""));
        Process process = new ProcessBuilder(Path.of(System.getProperty("java.home"), "bin", "java").toString(),
                "-cp", classpath, "model.ScenarioUI", "--verify", "--data-dir=" + data)
                .redirectErrorStream(true).redirectOutput(output.toFile()).start();
        if (!process.waitFor(20, TimeUnit.SECONDS)) { process.destroyForcibly(); fail("Restart verification timed out"); }
        String log = Files.readString(output); assertEquals(log, 0, process.exitValue());
        assertTrue(log, log.contains("Restart verified"));
        // Reload into empty shared stores to verify the persisted occupancy was not replayed.
        UserList.getInstance().getUsers().clear(); ShelterList.getInstance().getShelters().clear();
        ReliefRequestCatalog.getInstance().getRequests().clear(); app = new HurricaneReliefApp(routes); app.initialize();
        Shelter shelter = ShelterList.getInstance().getShelters().stream().filter(s -> s.getName().equals("Lackawanna Community Shelter")).findFirst().orElseThrow();
        assertEquals(93, shelter.getOccupancy()); assertEquals(3, shelter.getOccupants().size()); assertEquals(1, shelter.getPetOccupancy());
        assertTrue(app.login("felixgrant", "felix2026"));
        UUID requestId = shelter.getAdmissions().get(0).requestId(); assertFalse(app.checkIn(requestId, shelter));
    }
}
