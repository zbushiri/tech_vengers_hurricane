package com.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import location.RoutePlan;
import location.RoutingService;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//Unit tests for the HurricaneReliefApp facade
public class HurricaneReliefAppTest {
    private static final String HOME = "25 Calhoun Street, Charleston, SC 29401";
    private static final String NEIGHBOR = "80 Broad Street, Charleston, SC 29401";
    private static Path dataDir;

    private final RoutingService routes = (from, to) -> {
        if (from == null || to == null) throw new IOException("TEST ROUTE ONLY: invalid address");
        double meters = to.getStreet().contains("Dorchester") ? 5000
                : to.getStreet().contains("Lackawanna") ? 10000 : 1200;
        return new RoutePlan(meters, List.of("TEST ROUTE ONLY: drive to " + to));
    };

    private HurricaneReliefApp app;
    private User resident;
    private VolunteerProfile pendingVolunteer;
    private Shelter lackawanna;
    private Shelter dorchester;

    @BeforeClass
    public static void useTemporaryDataFolder() throws IOException {
        // The data folder is fixed once per JVM, so reuse one another test class already chose.
        String folder = System.getProperty("hurricane.dataDir");
        if (folder == null) {
            folder = Files.createTempDirectory("hurricane-app-test-").toString();
            System.setProperty("hurricane.dataDir", folder);
        }
        dataDir = Path.of(folder);
    }

    @Before
    public void setUp() {
        clearSharedLists();
        resident = new User("David", "Carter", "dcarter@example.com", "803-555-0142", HOME,
                "dcarter", "carter2026", new ArrayList<>(List.of(AccountRole.RESIDENT)), HOME);
        pendingVolunteer = volunteer("macywest", "macy2026", BackgroundCheckStatus.PENDING);
        User operator = user("felixgrant", "felix2026", AccountRole.SHELTER_OPERATOR);

        UserList users = UserList.getInstance();
        users.addUser(resident);
        users.addUser(user("eparker", "parker2026", AccountRole.RESIDENT));
        users.addUser(volunteer("rosadelgado", "rosa2026", BackgroundCheckStatus.VERIFIED));
        users.addUser(volunteer("tnguyen", "nguyen2026", BackgroundCheckStatus.VERIFIED));
        users.addUser(pendingVolunteer);
        users.addUser(operator);
        users.addUser(user("jkim", "kim2026", AccountRole.COORDINATOR));

        lackawanna = new Shelter(UUID.randomUUID(), "Lackawanna Community Shelter",
                "5200 Lackawanna Boulevard, North Charleston, SC 29405", 150, 90, ShelterStatus.OPEN,
                new ArrayList<>(List.of(Accommodation.PET_FRIENDLY, Accommodation.MEDICAL_SUPPORT)));
        lackawanna.setPetCapacity(20);
        lackawanna.getShelterOperators().add(operator);
        dorchester = new Shelter(UUID.randomUUID(), "Dorchester Road Shelter",
                "6401 Dorchester Road, North Charleston, SC 29418", 90, 30, ShelterStatus.OPEN,
                new ArrayList<>(List.of(Accommodation.WHEELCHAIR_ACCESSIBLE)));
        ShelterList.getInstance().addShelter(lackawanna);
        ShelterList.getInstance().addShelter(dorchester);

        app = new HurricaneReliefApp(routes);
    }

    @After
    public void tearDown() {
        clearSharedLists();
    }

    //helpers

    private static void clearSharedLists() {
        UserList.getInstance().getUsers().clear();
        ShelterList.getInstance().getShelters().clear();
        ReliefRequestCatalog.getInstance().getRequests().clear();
    }

    private void loadSeedData() throws IOException {
        clearSharedLists();
        for (String name : List.of("users", "shelters", "requests", "hurricanes")) {
            Files.copy(Path.of("json", name + ".json"), dataDir.resolve(name + ".json"),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        app = new HurricaneReliefApp(routes);
        app.initialize();
    }

    private static User user(String username, String password, AccountRole role) {
        return new User("Test", "User", username + "@example.com", "803-555-0100", HOME,
                username, password, new ArrayList<>(List.of(role)), HOME);
    }

    private static VolunteerProfile volunteer(String username, String password, BackgroundCheckStatus check) {
        return new VolunteerProfile(user(username, password, AccountRole.VOLUNTEER),
                new ArrayList<>(List.of("TRANSPORTATION")), new ArrayList<>(List.of("Pickup truck")),
                check, true, AvailabilityStatus.AVAILABLE);
    }

    private static List<Person> family(int size) {
        List<Person> people = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            people.add(new Person("Member" + i, "Carter", 30, new ArrayList<>()));
        }
        return people;
    }

    private static Map<String, String> accountDetails(String username, String password) {
        return Map.of("username", username, "password", password,
                "firstName", "Sam", "lastName", "Brown", "address", HOME);
    }

    // David Carter submits a two-person transportation request, then signs out
    private ReliefRequest householdRequest(String medicalNeeds, List<Pet> pets) {
        app.login("dcarter", "carter2026");
        ReliefRequest request = app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded",
                HOME, family(2), pets, medicalNeeds);
        app.logout();
        return request;
    }

    private ReliefRequest householdRequest() {
        return householdRequest("", List.of());
    }

    // Rosa claims the request and stays signed in.
    private ReliefRequest claimedByRosa(ReliefRequest request) {
        app.login("rosadelgado", "rosa2026");
        app.claimRequest(request.getRequestId());
        return request;
    }

    // A household request Rosa has claimed and routed to the given shelter; nobody is signed in afterwards.
    private ReliefRequest headingTo(Shelter shelter) {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.selectShelter(request.getRequestId(), shelter);
        app.logout();
        return request;
    }

    //loadData, saveData, and getActiveHurricane

    @Test
    public void initializeLoadsSeedUsers() throws IOException {
        loadSeedData();
        assertEquals(6, UserList.getInstance().getUsers().size());
    }

    @Test
    public void initializeLoadsSeedShelters() throws IOException {
        loadSeedData();
        assertEquals(10, ShelterList.getInstance().getShelters().size());
    }

    @Test
    public void initializeLoadsSeedRequests() throws IOException {
        loadSeedData();
        assertEquals(6, ReliefRequestCatalog.getInstance().getRequests().size());
    }

    @Test
    public void initializeLoadsSeedHurricanes() throws IOException {
        loadSeedData();
        assertEquals(4, app.getHurricanes().size());
    }

    @Test
    public void activeHurricaneIsFoundAfterLoading() throws IOException {
        loadSeedData();
        assertEquals("Hurricane Scarlett", app.getActiveHurricane().getName());
    }

    @Test
    public void noActiveHurricaneBeforeDataIsLoaded() {
        assertNull(app.getActiveHurricane());
    }

    @Test
    public void noActiveHurricaneWhenAllAreOnlyMonitored() throws IOException {
        loadSeedData();
        app.getHurricanes().forEach(hurricane -> hurricane.setStatus(EventStatus.MONITORING));
        assertNull(app.getActiveHurricane());
    }

    @Test
    public void submittedRequestIsLinkedToActiveHurricane() throws IOException {
        loadSeedData();
        app.login("johndoe", "password123");
        app.submitReliefRequest(RequestType.FOOD, "Need water", HOME, 2);
        assertEquals("Hurricane Scarlett", app.getMyRequests().get(0).getForHurricane().getName());
    }

    @Test
    public void saveDataReportsSuccess() throws IOException {
        loadSeedData();
        assertTrue(app.saveData());
    }

    @Test
    public void savedAccountSurvivesReload() throws IOException {
        loadSeedData();
        app.createAccount(AccountRole.RESIDENT, accountDetails("sbrown", "brown2026"));
        app.saveData();
        clearSharedLists();
        HurricaneReliefApp reloaded = new HurricaneReliefApp(routes);
        reloaded.initialize();
        assertTrue(reloaded.login("sbrown", "brown2026"));
    }

    //login and logout

    @Test
    public void correctPasswordLogsIn() {
        assertTrue(app.login("dcarter", "carter2026"));
    }

    @Test
    public void loginMakesUserTheCurrentUser() {
        app.login("dcarter", "carter2026");
        assertSame(resident, app.getCurrentUser());
    }

    @Test
    public void usernameMatchIgnoresCase() {
        assertTrue(app.login("DCarter", "carter2026"));
    }

    @Test
    public void wrongPasswordIsRejected() {
        assertFalse(app.login("dcarter", "wrongpass"));
    }

    @Test
    public void unknownUsernameIsRejected() {
        assertFalse(app.login("nobody", "carter2026"));
    }

    @Test
    public void failedLoginLeavesNobodySignedIn() {
        app.login("dcarter", "wrongpass");
        assertFalse(app.isLoggedIn());
    }

    @Test
    public void logoutClearsCurrentUser() {
        app.login("dcarter", "carter2026");
        app.logout();
        assertNull(app.getCurrentUser());
    }

    @Test
    public void logoutWhenNobodyIsSignedInKeepsNobodySignedIn() {
        app.logout();
        assertFalse(app.isLoggedIn());
    }

    //createAccount testing

    @Test
    public void createAccountFromDetailsSucceeds() {
        assertTrue(app.createAccount(AccountRole.RESIDENT, accountDetails("sbrown", "brown2026")));
    }

    @Test
    public void newAccountCanLogIn() {
        app.createAccount(AccountRole.RESIDENT, accountDetails("sbrown", "brown2026"));
        assertTrue(app.login("sbrown", "brown2026"));
    }

    @Test
    public void newVolunteerAccountStartsWithPendingBackgroundCheck() {
        app.createAccount(AccountRole.VOLUNTEER, accountDetails("sbrown", "brown2026"));
        VolunteerProfile created = UserList.getInstance().getUser("sbrown").getVolunteerProfile();
        assertEquals(BackgroundCheckStatus.PENDING, created.getBackgroundCheckStatus());
    }

    @Test
    public void createAccountWithoutPasswordFails() {
        assertFalse(app.createAccount(AccountRole.RESIDENT, Map.of("username", "sbrown")));
    }

    @Test
    public void createAccountWithoutRoleFails() {
        assertFalse(app.createAccount(null, accountDetails("sbrown", "brown2026")));
    }

    @Test
    public void createAccountWithTakenUsernameFails() {
        assertFalse(app.createAccount(AccountRole.RESIDENT, accountDetails("dcarter", "other2026")));
    }

    @Test
    public void createAccountFromFieldsSucceeds() {
        assertTrue(app.createAccount("Sam", "Brown", "sbrown@example.com", "803-555-0199", HOME,
                "sbrown", "brown2026", new ArrayList<>(List.of(AccountRole.RESIDENT)), HOME));
    }

    @Test
    public void createAccountFromFieldsWithTakenUsernameFails() {
        assertFalse(app.createAccount("Sam", "Brown", "sbrown@example.com", "803-555-0199", HOME,
                "DCARTER", "brown2026", new ArrayList<>(List.of(AccountRole.RESIDENT)), HOME));
    }

    //submitReliefRequest testing

    @Test
    public void signedInUserCanSubmitRequest() {
        app.login("dcarter", "carter2026");
        assertTrue(app.submitReliefRequest(RequestType.FOOD, "Need water", HOME, 2));
    }

    @Test
    public void submittedRequestIsAddedToCatalog() {
        app.login("dcarter", "carter2026");
        app.submitReliefRequest(RequestType.FOOD, "Need water", HOME, 2);
        assertEquals(1, ReliefRequestCatalog.getInstance().getRequests().size());
    }

    @Test
    public void submittedRequestRecordsWhoSubmittedIt() {
        app.login("dcarter", "carter2026");
        app.submitReliefRequest(RequestType.FOOD, "Need water", HOME, 2);
        assertSame(resident, ReliefRequestCatalog.getInstance().getRequests().get(0).getSubmittedBy());
    }

    @Test
    public void submittedLocationIsTrimmed() {
        app.login("dcarter", "carter2026");
        app.submitReliefRequest(RequestType.FOOD, "Need water", "  " + HOME + "  ", 2);
        assertEquals(HOME, ReliefRequestCatalog.getInstance().getRequests().get(0).getLocation());
    }

    @Test
    public void secondRequestAtSameLocationIsMarkedDuplicate() {
        app.login("dcarter", "carter2026");
        app.submitReliefRequest(RequestType.FOOD, "Need water", HOME, 2);
        app.submitReliefRequest(RequestType.FOOD, "Still need water", HOME, 2);
        assertNotNull(app.getMyRequests().get(1).getIsDuplicateOf());
    }

    @Test
    public void signedOutUserCannotSubmitRequest() {
        assertFalse(app.submitReliefRequest(RequestType.FOOD, "Need water", HOME, 2));
    }

    @Test
    public void requestWithoutTypeIsRejected() {
        app.login("dcarter", "carter2026");
        assertFalse(app.submitReliefRequest(null, "Need water", HOME, 2));
    }

    @Test
    public void requestWithBlankDescriptionIsRejected() {
        app.login("dcarter", "carter2026");
        assertFalse(app.submitReliefRequest(RequestType.FOOD, "   ", HOME, 2));
    }

    @Test
    public void requestWithBlankLocationIsRejected() {
        app.login("dcarter", "carter2026");
        assertFalse(app.submitReliefRequest(RequestType.FOOD, "Need water", "  ", 2));
    }

    @Test
    public void requestForZeroPeopleIsRejected() {
        app.login("dcarter", "carter2026");
        assertFalse(app.submitReliefRequest(RequestType.FOOD, "Need water", HOME, 0));
    }

    //submitHouseholdRequest testing

    @Test
    public void householdRequestCountsEveryPerson() {
        app.login("dcarter", "carter2026");
        ReliefRequest request = app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded",
                HOME, family(3), List.of(), "");
        assertEquals(3, request.getNumberOfPeopleNeeded());
    }

    @Test
    public void householdIsSavedAsUsersAssociatedPeople() {
        app.login("dcarter", "carter2026");
        app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded", HOME, family(3), List.of(), "");
        assertEquals(3, resident.getAssociatedPersons().size());
    }

    @Test
    public void householdMedicalNeedsMakeRequestHighPriority() {
        assertEquals(PriorityLevel.HIGH, householdRequest("Needs oxygen", List.of()).getPriority());
    }

    @Test
    public void householdRequestRequiresSignIn() {
        assertNull(app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded",
                HOME, family(2), List.of(), ""));
    }

    @Test
    public void householdRequestRejectsIncompleteAddress() {
        app.login("dcarter", "carter2026");
        assertNull(app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded",
                "my house", family(2), List.of(), ""));
    }

    @Test
    public void householdRequestRejectsEmptyHousehold() {
        app.login("dcarter", "carter2026");
        assertNull(app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded",
                HOME, List.of(), List.of(), ""));
    }

    @Test
    public void householdRequestRejectsImpossibleAge() {
        app.login("dcarter", "carter2026");
        List<Person> people = List.of(new Person("David", "Carter", 130, new ArrayList<>()));
        assertNull(app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded",
                HOME, people, List.of(), ""));
    }

    @Test
    public void householdRequestRejectsBlankName() {
        app.login("dcarter", "carter2026");
        List<Person> people = List.of(new Person(" ", "Carter", 38, new ArrayList<>()));
        assertNull(app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded",
                HOME, people, List.of(), ""));
    }

    @Test
    public void householdRequestRejectsMissingPetList() {
        app.login("dcarter", "carter2026");
        assertNull(app.submitHouseholdRequest(RequestType.TRANSPORTATION, "Street is flooded",
                HOME, family(2), null, ""));
    }

    //findNearestShelters testing

    @Test
    public void nearestSheltersAreOrderedByDrivingDistance() {
        assertEquals(List.of(dorchester, lackawanna), app.findNearestShelters(HOME, 25));
    }

    @Test
    public void sheltersBeyondRadiusAreLeftOut() {
        assertEquals(List.of(dorchester), app.findNearestShelters(HOME, 4));
    }

    @Test
    public void fullSheltersAreLeftOut() {
        dorchester.setOccupancy(90);
        assertEquals(List.of(lackawanna), app.findNearestShelters(HOME, 25));
    }

    @Test
    public void blankLocationSearchesFromSignedInUsersAddress() {
        app.login("dcarter", "carter2026");
        assertEquals(2, app.findNearestShelters("", 0).size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankLocationWhileSignedOutIsRejected() {
        app.findNearestShelters("", 25);
    }

    @Test(expected = IllegalArgumentException.class)
    public void incompleteAddressIsRejected() {
        app.findNearestShelters("Charleston", 25);
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeRadiusIsRejected() {
        app.findNearestShelters(HOME, -1);
    }

    @Test(expected = IllegalStateException.class)
    public void unavailableRoutesAreReportedInsteadOfGuessing() {
        HurricaneReliefApp offline = new HurricaneReliefApp((from, to) -> {
            throw new IOException("TEST ROUTE ONLY: offline");
        });
        offline.findNearestShelters(HOME, 25);
    }

    //markSafe testing

    @Test
    public void markSafeMarksSignedInUserSafe() {
        app.login("dcarter", "carter2026");
        app.markSafe();
        assertEquals(SafetyStatusType.SAFE, resident.getSafetyStatus());
    }

    @Test
    public void markSafeWhileSignedOutChangesNobody() {
        app.markSafe();
        assertEquals(SafetyStatusType.UNKNOWN, resident.getSafetyStatus());
    }

    //open request lists testing

    @Test
    public void openRequestsAreListedMostUrgentFirst() {
        app.login("dcarter", "carter2026");
        app.submitReliefRequest(RequestType.TRANSPORTATION, "Need a ride", HOME, 1);
        app.submitReliefRequest(RequestType.MEDICAL, "Out of insulin", NEIGHBOR, 1);
        assertEquals(RequestType.MEDICAL, app.getOpenRequestsByUrgency().get(0).getType());
    }

    @Test
    public void medicalRequestsAreNotOfferedToVolunteers() {
        app.login("dcarter", "carter2026");
        app.submitReliefRequest(RequestType.TRANSPORTATION, "Need a ride", HOME, 1);
        app.submitReliefRequest(RequestType.MEDICAL, "Out of insulin", NEIGHBOR, 1);
        assertTrue(app.getOpenRequestsForVolunteers().stream().noneMatch(r -> r.getType() == RequestType.MEDICAL));
    }

    //claimRequest testing

    @Test
    public void verifiedVolunteerCanClaimRequest() {
        ReliefRequest request = householdRequest();
        app.login("rosadelgado", "rosa2026");
        assertTrue(app.claimRequest(request.getRequestId()));
    }

    @Test
    public void claimedRequestIsAssignedToTheVolunteer() {
        ReliefRequest request = claimedByRosa(householdRequest());
        assertEquals("rosadelgado", request.getAssignedVolunteer().getUsername());
    }

    @Test
    public void residentCannotClaimRequest() {
        ReliefRequest request = householdRequest();
        app.login("dcarter", "carter2026");
        assertFalse(app.claimRequest(request.getRequestId()));
    }

    @Test
    public void signedOutUserCannotClaimRequest() {
        assertFalse(app.claimRequest(householdRequest().getRequestId()));
    }

    @Test
    public void volunteerWithPendingBackgroundCheckCannotClaim() {
        ReliefRequest request = householdRequest();
        app.login("macywest", "macy2026");
        assertFalse(app.claimRequest(request.getRequestId()));
    }

    @Test
    public void claimedRequestCannotBeClaimedBySecondVolunteer() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        app.login("tnguyen", "nguyen2026");
        assertFalse(app.claimRequest(request.getRequestId()));
    }

    @Test
    public void unknownRequestCannotBeClaimed() {
        app.login("rosadelgado", "rosa2026");
        assertFalse(app.claimRequest(UUID.randomUUID()));
    }

    //verifyVolunteer and canVerifyVolunteers testing

    @Test
    public void coordinatorCanVerifyVolunteer() {
        app.login("jkim", "kim2026");
        assertTrue(app.verifyVolunteer("macywest"));
    }

    @Test
    public void verifyingVolunteerMarksBackgroundCheckVerified() {
        app.login("jkim", "kim2026");
        app.verifyVolunteer("macywest");
        assertEquals(BackgroundCheckStatus.VERIFIED, pendingVolunteer.getBackgroundCheckStatus());
    }

    @Test
    public void shelterOperatorCanVerifyVolunteer() {
        app.login("felixgrant", "felix2026");
        assertTrue(app.verifyVolunteer("macywest"));
    }

    @Test
    public void residentCannotVerifyVolunteer() {
        app.login("dcarter", "carter2026");
        assertFalse(app.verifyVolunteer("macywest"));
    }

    @Test
    public void signedOutUserCannotVerifyVolunteer() {
        assertFalse(app.verifyVolunteer("macywest"));
    }

    @Test
    public void verifyingNonVolunteerFails() {
        app.login("jkim", "kim2026");
        assertFalse(app.verifyVolunteer("dcarter"));
    }

    @Test
    public void verifyingUnknownUserFails() {
        app.login("jkim", "kim2026");
        assertFalse(app.verifyVolunteer("nobody"));
    }

    @Test
    public void signedOutUserIsNotAllowedToVerifyVolunteers() {
        assertFalse(app.canVerifyVolunteers());
    }

    //completeRequest testing

    @Test
    public void coordinatorCanCompleteAssignedRequest() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        app.login("jkim", "kim2026");
        assertTrue(app.completeRequest(request.getRequestId()));
    }

    @Test
    public void completedRequestIsFulfilled() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        app.login("jkim", "kim2026");
        app.completeRequest(request.getRequestId());
        assertEquals(RequestStatus.FULFILLED, request.getStatus());
    }

    @Test
    public void operatorCanCompleteRequestAfterCheckIn() {
        ReliefRequest request = headingTo(lackawanna);
        app.login("felixgrant", "felix2026");
        app.checkIn(request.getRequestId(), lackawanna);
        assertTrue(app.completeRequest(request.getRequestId()));
    }

    @Test
    public void operatorCannotCompleteRequestBeforeCheckIn() {
        ReliefRequest request = headingTo(lackawanna);
        app.login("felixgrant", "felix2026");
        assertFalse(app.completeRequest(request.getRequestId()));
    }

    @Test
    public void volunteerCannotCompleteRequest() {
        ReliefRequest request = claimedByRosa(householdRequest());
        assertFalse(app.completeRequest(request.getRequestId()));
    }

    @Test
    public void signedOutUserCannotCompleteRequest() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        assertFalse(app.completeRequest(request.getRequestId()));
    }

    @Test
    public void unknownRequestCannotBeCompleted() {
        app.login("jkim", "kim2026");
        assertFalse(app.completeRequest(UUID.randomUUID()));
    }

    //getMyAssignments and getMyRequests testing

    @Test
    public void claimedRequestAppearsInVolunteersAssignments() {
        claimedByRosa(householdRequest());
        assertEquals(1, app.getMyAssignments().size());
    }

    @Test
    public void completedRequestLeavesVolunteersAssignments() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        app.login("jkim", "kim2026");
        app.completeRequest(request.getRequestId());
        app.logout();
        app.login("rosadelgado", "rosa2026");
        assertTrue(app.getMyAssignments().isEmpty());
    }

    @Test
    public void residentHasNoAssignments() {
        claimedByRosa(householdRequest());
        app.logout();
        app.login("dcarter", "carter2026");
        assertTrue(app.getMyAssignments().isEmpty());
    }

    @Test
    public void usersOwnRequestsAreListed() {
        ReliefRequest request = householdRequest();
        app.login("dcarter", "carter2026");
        assertEquals(List.of(request), app.getMyRequests());
    }

    @Test
    public void otherUsersRequestsAreNotListed() {
        householdRequest();
        app.login("eparker", "parker2026");
        assertTrue(app.getMyRequests().isEmpty());
    }

    @Test
    public void signedOutUserHasNoRequests() {
        householdRequest();
        assertTrue(app.getMyRequests().isEmpty());
    }

    //cancelRequest testing

    @Test
    public void ownerCanCancelOpenRequest() {
        ReliefRequest request = householdRequest();
        app.login("dcarter", "carter2026");
        assertTrue(app.cancelRequest(request.getRequestId()));
    }

    @Test
    public void cancelledRequestIsMarkedCancelled() {
        ReliefRequest request = householdRequest();
        app.login("dcarter", "carter2026");
        app.cancelRequest(request.getRequestId());
        assertEquals(RequestStatus.CANCELLED, request.getStatus());
    }

    @Test
    public void otherUserCannotCancelRequest() {
        ReliefRequest request = householdRequest();
        app.login("eparker", "parker2026");
        assertFalse(app.cancelRequest(request.getRequestId()));
    }

    @Test
    public void cancelledRequestCannotBeCancelledAgain() {
        ReliefRequest request = householdRequest();
        app.login("dcarter", "carter2026");
        app.cancelRequest(request.getRequestId());
        assertFalse(app.cancelRequest(request.getRequestId()));
    }

    @Test
    public void signedOutUserCannotCancelRequest() {
        assertFalse(app.cancelRequest(householdRequest().getRequestId()));
    }

    @Test
    public void unknownRequestCannotBeCancelled() {
        app.login("dcarter", "carter2026");
        assertFalse(app.cancelRequest(UUID.randomUUID()));
    }

    //startRequest testing

    @Test
    public void assignedVolunteerCanStartRequest() {
        ReliefRequest request = claimedByRosa(householdRequest());
        assertTrue(app.startRequest(request.getRequestId()));
    }

    @Test
    public void startedRequestIsInProgress() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.startRequest(request.getRequestId());
        assertEquals(RequestStatus.IN_PROGRESS, request.getStatus());
    }

    @Test
    public void otherVolunteerCannotStartRequest() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        app.login("tnguyen", "nguyen2026");
        assertFalse(app.startRequest(request.getRequestId()));
    }

    @Test
    public void residentCannotStartRequest() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        app.login("dcarter", "carter2026");
        assertFalse(app.startRequest(request.getRequestId()));
    }

    @Test
    public void signedOutUserCannotStartRequest() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        assertFalse(app.startRequest(request.getRequestId()));
    }

    @Test
    public void unknownRequestCannotBeStarted() {
        app.login("rosadelgado", "rosa2026");
        assertFalse(app.startRequest(UUID.randomUUID()));
    }

    //findShelter testing

    @Test
    public void findShelterPicksClosestShelterWithRoom() throws IOException {
        assertSame(dorchester, app.findShelter(householdRequest()));
    }

    @Test
    public void findShelterRequiresMedicalSupportForMedicalNeeds() throws IOException {
        assertSame(lackawanna, app.findShelter(householdRequest("Needs oxygen", List.of())));
    }

    @Test
    public void findShelterRequiresPetFriendlyShelterForPets() throws IOException {
        assertSame(lackawanna, app.findShelter(householdRequest("", List.of(new Pet("Buddy", "dog")))));
    }

    @Test
    public void findShelterReturnsNullWhenNoShelterIsOpen() throws IOException {
        ReliefRequest request = householdRequest();
        lackawanna.setStatus(ShelterStatus.CLOSED);
        dorchester.setStatus(ShelterStatus.CLOSED);
        assertNull(app.findShelter(request));
    }

    @Test(expected = IllegalArgumentException.class)
    public void findShelterRejectsIncompleteAddress() throws IOException {
        app.findShelter(new ReliefRequest(RequestType.TRANSPORTATION, "Street is flooded", "somewhere", 2));
    }

    //selectShelter testing

    @Test
    public void assignedVolunteerCanSelectShelter() {
        ReliefRequest request = claimedByRosa(householdRequest());
        assertTrue(app.selectShelter(request.getRequestId(), lackawanna));
    }

    @Test
    public void selectingShelterSetsRequestDestination() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.selectShelter(request.getRequestId(), lackawanna);
        assertEquals(lackawanna.getShelterId(), request.getDestinationShelterId());
    }

    @Test
    public void otherVolunteerCannotSelectShelter() {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.logout();
        app.login("tnguyen", "nguyen2026");
        assertFalse(app.selectShelter(request.getRequestId(), lackawanna));
    }

    @Test
    public void unclaimedRequestCannotSelectShelter() {
        ReliefRequest request = householdRequest();
        app.login("rosadelgado", "rosa2026");
        assertFalse(app.selectShelter(request.getRequestId(), lackawanna));
    }

    @Test
    public void shelterMissingNeededServiceCannotBeSelected() {
        ReliefRequest request = claimedByRosa(householdRequest("Needs oxygen", List.of()));
        assertFalse(app.selectShelter(request.getRequestId(), dorchester));
    }

    @Test
    public void unlistedShelterCannotBeSelected() {
        ReliefRequest request = claimedByRosa(householdRequest());
        Shelter unlisted = new Shelter(UUID.randomUUID(), "Pop-up Shelter", NEIGHBOR, 50, 0,
                ShelterStatus.OPEN, new ArrayList<>());
        assertFalse(app.selectShelter(request.getRequestId(), unlisted));
    }

    @Test
    public void nullShelterCannotBeSelected() {
        ReliefRequest request = claimedByRosa(householdRequest());
        assertFalse(app.selectShelter(request.getRequestId(), null));
    }

    @Test
    public void checkedInRequestCannotSwitchShelters() {
        ReliefRequest request = headingTo(lackawanna);
        app.login("felixgrant", "felix2026");
        app.checkIn(request.getRequestId(), lackawanna);
        app.logout();
        app.login("rosadelgado", "rosa2026");
        assertFalse(app.selectShelter(request.getRequestId(), dorchester));
    }

    @Test
    public void unknownRequestCannotSelectShelter() {
        app.login("rosadelgado", "rosa2026");
        assertFalse(app.selectShelter(UUID.randomUUID(), lackawanna));
    }

    //exportTripSheet testing (error cases only since the .txt contents are out of scope)

    @Test(expected = IllegalStateException.class)
    public void onlyAssignedVolunteerCanExportTripSheet() throws IOException {
        ReliefRequest request = headingTo(lackawanna);
        app.login("tnguyen", "nguyen2026");
        app.exportTripSheet(request.getRequestId(), Path.of("unused"));
    }

    @Test(expected = IllegalStateException.class)
    public void tripSheetNeedsSelectedShelter() throws IOException {
        ReliefRequest request = claimedByRosa(householdRequest());
        app.exportTripSheet(request.getRequestId(), Path.of("unused"));
    }

    @Test(expected = IllegalStateException.class)
    public void signedOutUserCannotExportTripSheet() throws IOException {
        app.exportTripSheet(headingTo(lackawanna).getRequestId(), Path.of("unused"));
    }

    //getManagedShelters testing

    @Test
    public void coordinatorManagesEveryShelter() {
        app.login("jkim", "kim2026");
        assertEquals(2, app.getManagedShelters().size());
    }

    @Test
    public void operatorManagesOnlyTheirOwnShelter() {
        app.login("felixgrant", "felix2026");
        assertEquals(List.of(lackawanna), app.getManagedShelters());
    }

    @Test
    public void residentManagesNoShelters() {
        app.login("dcarter", "carter2026");
        assertTrue(app.getManagedShelters().isEmpty());
    }

    @Test
    public void signedOutUserManagesNoShelters() {
        assertTrue(app.getManagedShelters().isEmpty());
    }

    //checkIn testing

    @Test
    public void operatorCanCheckInAtTheirShelter() {
        ReliefRequest request = headingTo(lackawanna);
        app.login("felixgrant", "felix2026");
        assertTrue(app.checkIn(request.getRequestId(), lackawanna));
    }

    @Test
    public void operatorCannotCheckInAtShelterTheyDoNotRun() {
        ReliefRequest request = headingTo(dorchester);
        app.login("felixgrant", "felix2026");
        assertFalse(app.checkIn(request.getRequestId(), dorchester));
    }

    @Test
    public void coordinatorCanCheckInAtAnyShelter() {
        ReliefRequest request = headingTo(dorchester);
        app.login("jkim", "kim2026");
        assertTrue(app.checkIn(request.getRequestId(), dorchester));
    }

    @Test
    public void checkInAtNullShelterFails() {
        ReliefRequest request = headingTo(lackawanna);
        app.login("felixgrant", "felix2026");
        assertFalse(app.checkIn(request.getRequestId(), null));
    }

    @Test
    public void signedOutUserCannotCheckIn() {
        assertFalse(app.checkIn(headingTo(lackawanna).getRequestId(), lackawanna));
    }
}
