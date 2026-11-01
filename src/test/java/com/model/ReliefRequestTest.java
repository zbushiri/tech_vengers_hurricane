package com.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

// Unit tests for ReliefRequest's priority rules, details and the workflow of the class.
public class ReliefRequestTest {
    private static final String HOME = "25 Calhoun Street, Charleston, SC 29401";
    private ReliefRequest foodRequest;
    private VolunteerProfile volunteer;
    private User coordinator;

    @Before
    public void setUp() {
        ShelterList.getInstance().getShelters().clear();
        foodRequest = new ReliefRequest(RequestType.FOOD, "Need water and canned food", HOME, 2);
        volunteer = volunteer("mlopez", BackgroundCheckStatus.VERIFIED, AvailabilityStatus.AVAILABLE);
        coordinator = user("jkim", AccountRole.COORDINATOR);
    }

    @After
    public void tearDown() {
        ShelterList.getInstance().getShelters().clear();
    }

    private static User user(String username, AccountRole role) {
        return new User("Test", "User", username + "@example.com", "803-555-0100", HOME,
                username, "password1", new ArrayList<>(List.of(role)), HOME);
    }

    private static VolunteerProfile volunteer(String username, BackgroundCheckStatus check,
            AvailabilityStatus availability) {
        return new VolunteerProfile(user(username, AccountRole.VOLUNTEER), new ArrayList<>(List.of("FOOD")),
                new ArrayList<>(List.of("Pickup truck")), check, true, availability);
    }

    private static Shelter shelterRunBy(User operator) {
        Shelter shelter = new Shelter(UUID.randomUUID(), "Dreher High School",
                "3319 Millwood Avenue, Columbia, SC 29205", 100, 0, ShelterStatus.OPEN, new ArrayList<>());
        shelter.getShelterOperators().add(operator);
        ShelterList.getInstance().addShelter(shelter);
        return shelter;
    }

    private ReliefRequest assignedHouseholdHeadingTo(Shelter destination) {
        ReliefRequest request = new ReliefRequest(RequestType.TRANSPORTATION, "Street is flooded", HOME, 2);
        request.setFamilyMembers(List.of(new Person("David", "Carter", 38, new ArrayList<>()),
                new Person("Elena", "Carter", 36, new ArrayList<>())));
        request.setDestinationShelterId(destination.getShelterId());
        request.claimRequest(volunteer);
        return request;
    }

    // constructor for tests

    @Test
    public void newRequestStartsAsSubmitted() {
        assertEquals(RequestStatus.SUBMITTED, foodRequest.getStatus());
    }

    // KNOWN BUG: expected to fail until the constructor validates the people count.
    @Test(expected = IllegalArgumentException.class)
    public void newRequestWithNegativePeopleCountIsRejected() {
        new ReliefRequest(RequestType.FOOD, "Need food", HOME, -3);
    }

    @Test
    public void copyKeepsStatusOfOriginal() {
        foodRequest.cancel();
        assertEquals(RequestStatus.CANCELLED, new ReliefRequest(foodRequest).getStatus());
    }

    @Test
    public void copyKeepsManuallySetPriority() {
        foodRequest.setPriority(PriorityLevel.CRITICAL);
        assertEquals(PriorityLevel.CRITICAL, new ReliefRequest(foodRequest).getPriority());
    }

    // calculating priority

    @Test
    public void foodRequestForSmallGroupIsMediumPriority() {
        assertEquals(PriorityLevel.MEDIUM, foodRequest.calculatePriority());
    }

    @Test
    public void transportationRequestForSmallGroupIsLowPriority() {
        ReliefRequest ride = new ReliefRequest(RequestType.TRANSPORTATION, "Need a ride", HOME, 1);
        assertEquals(PriorityLevel.LOW, ride.calculatePriority());
    }

    @Test
    public void medicalRequestIsHighPriority() {
        ReliefRequest medical = new ReliefRequest(RequestType.MEDICAL, "Out of insulin", HOME, 1);
        assertEquals(PriorityLevel.HIGH, medical.calculatePriority());
    }

    @Test
    public void medicalNeedsRaiseAnyRequestToHighPriority() {
        ReliefRequest ride = new ReliefRequest(RequestType.TRANSPORTATION, "Need a ride", HOME, 1);
        ride.setMedicalNeeds("Daily dialysis");
        assertEquals(PriorityLevel.HIGH, ride.getPriority());
    }

    @Test
    public void groupOfTenRaisesPriorityOneLevel() {
        ReliefRequest bigGroup = new ReliefRequest(RequestType.FOOD, "Church group", HOME, 10);
        assertEquals(PriorityLevel.HIGH, bigGroup.calculatePriority());
    }

    @Test
    public void groupOfNineDoesNotRaisePriority() {
        ReliefRequest group = new ReliefRequest(RequestType.FOOD, "Two families", HOME, 9);
        assertEquals(PriorityLevel.MEDIUM, group.calculatePriority());
    }

    @Test
    public void largeMedicalGroupIsCriticalPriority() {
        ReliefRequest nursingHome = new ReliefRequest(RequestType.MEDICAL, "Nursing home lost power", HOME, 12);
        assertEquals(PriorityLevel.CRITICAL, nursingHome.calculatePriority());
    }

    @Test
    public void nullMedicalNeedsAreStoredAsEmptyText() {
        foodRequest.setMedicalNeeds(null);
        assertEquals("", foodRequest.getMedicalNeeds());
    }

    // ---------- isOpen / isForVolunteers ----------

    @Test
    public void cancelledRequestIsNotOpen() {
        foodRequest.cancel();
        assertFalse(foodRequest.isOpen());
    }

    @Test
    public void foodRequestIsForVolunteers() {
        assertTrue(foodRequest.isForVolunteers());
    }

    @Test
    public void medicalRequestIsNotForVolunteers() {
        ReliefRequest medical = new ReliefRequest(RequestType.MEDICAL, "Chest pain", HOME, 1);
        assertFalse(medical.isForVolunteers());
    }

    // add details and flag as suspicious

    @Test
    public void addDetailAppendsOnNewLine() {
        foodRequest.addDetail("Gate code is 1234");
        assertEquals("Need water and canned food\nGate code is 1234", foodRequest.getDescription());
    }

    @Test
    public void addDetailReplacesEmptyDescription() {
        ReliefRequest blank = new ReliefRequest(RequestType.FOOD, "", HOME, 1);
        blank.addDetail("Two adults, no stove");
        assertEquals("Two adults, no stove", blank.getDescription());
    }

    @Test
    public void addDetailIgnoresBlankText() {
        foodRequest.addDetail("   ");
        assertEquals("Need water and canned food", foodRequest.getDescription());
    }

    @Test
    public void addDetailIgnoresNull() {
        foodRequest.addDetail(null);
        assertEquals("Need water and canned food", foodRequest.getDescription());
    }

    @Test
    public void flagAsSuspiciousMarksRequestSuspicious() {
        foodRequest.flagAsSuspicious();
        assertTrue(foodRequest.isSuspicious());
    }

    // set pets and update animal notes

    @Test
    public void settingPetsUpdatesAnimalCount() {
        foodRequest.setPets(List.of(new Pet("Buddy", "dog"), new Pet("Mittens", "cat")));
        assertEquals(2, foodRequest.getNumberOfAnimals());
    }

    @Test
    public void settingPetsDescribesEachPetInAnimalNotes() {
        foodRequest.setPets(List.of(new Pet("Buddy", "dog"), new Pet("Mittens", "cat")));
        assertEquals("Buddy (dog), Mittens (cat)", foodRequest.getAnimalNotes());
    }

    // claim request workflow

    @Test
    public void verifiedAvailableVolunteerCanClaimRequest() {
        foodRequest.claimRequest(volunteer);
        assertEquals(RequestStatus.ASSIGNED, foodRequest.getStatus());
    }

    @Test
    public void claimingRequestAssignsTheVolunteer() {
        foodRequest.claimRequest(volunteer);
        assertSame(volunteer, foodRequest.getAssignedVolunteer());
    }

    @Test
    public void claimingRequestPutsVolunteerOnAssignment() {
        foodRequest.claimRequest(volunteer);
        assertEquals(AvailabilityStatus.ON_ASSIGNMENT, volunteer.getAvailabilityStatus());
    }

    @Test
    public void volunteerWithPendingBackgroundCheckCannotClaim() {
        foodRequest.claimRequest(volunteer("newbie", BackgroundCheckStatus.PENDING, AvailabilityStatus.AVAILABLE));
        assertEquals(RequestStatus.SUBMITTED, foodRequest.getStatus());
    }

    @Test
    public void unavailableVolunteerCannotClaim() {
        foodRequest.claimRequest(volunteer("busy", BackgroundCheckStatus.VERIFIED, AvailabilityStatus.UNAVAILABLE));
        assertEquals(RequestStatus.SUBMITTED, foodRequest.getStatus());
    }

    @Test
    public void nullVolunteerCannotClaim() {
        foodRequest.claimRequest(null);
        assertNull(foodRequest.getAssignedVolunteer());
    }

    @Test
    public void medicalRequestCannotBeClaimedByVolunteer() {
        ReliefRequest medical = new ReliefRequest(RequestType.MEDICAL, "Broken leg", HOME, 1);
        medical.claimRequest(volunteer);
        assertEquals(RequestStatus.SUBMITTED, medical.getStatus());
    }

    @Test
    public void duplicateOfOpenRequestCannotBeClaimed() {
        ReliefRequest duplicate = new ReliefRequest(RequestType.FOOD, "Same house, same need", HOME, 2);
        duplicate.setIsDuplicateOf(foodRequest);
        duplicate.claimRequest(volunteer);
        assertEquals(RequestStatus.SUBMITTED, duplicate.getStatus());
    }

    @Test
    public void duplicateOfCancelledRequestCanBeClaimed() {
        ReliefRequest duplicate = new ReliefRequest(RequestType.FOOD, "Same house, same need", HOME, 2);
        duplicate.setIsDuplicateOf(foodRequest);
        foodRequest.cancel();
        duplicate.claimRequest(volunteer);
        assertEquals(RequestStatus.ASSIGNED, duplicate.getStatus());
    }

    // start request workflow

    @Test
    public void assignedVolunteerCanStartRequest() {
        foodRequest.claimRequest(volunteer);
        assertTrue(foodRequest.startRequest(volunteer));
    }

    @Test
    public void startingRequestMarksItInProgress() {
        foodRequest.claimRequest(volunteer);
        foodRequest.startRequest(volunteer);
        assertEquals(RequestStatus.IN_PROGRESS, foodRequest.getStatus());
    }

    @Test
    public void differentVolunteerCannotStartRequest() {
        foodRequest.claimRequest(volunteer);
        assertFalse(foodRequest.startRequest(volunteer("other", BackgroundCheckStatus.VERIFIED,
                AvailabilityStatus.AVAILABLE)));
    }

    @Test
    public void unclaimedRequestCannotBeStarted() {
        assertFalse(foodRequest.startRequest(volunteer));
    }

    @Test
    public void nullVolunteerCannotStartRequest() {
        foodRequest.claimRequest(volunteer);
        assertFalse(foodRequest.startRequest(null));
    }

    // complete request workflow

    @Test
    public void coordinatorCanCompleteAssignedRequest() {
        foodRequest.claimRequest(volunteer);
        assertTrue(foodRequest.completeRequest(coordinator));
    }

    @Test
    public void completingRequestMarksItFulfilled() {
        foodRequest.claimRequest(volunteer);
        foodRequest.completeRequest(coordinator);
        assertEquals(RequestStatus.FULFILLED, foodRequest.getStatus());
    }

    @Test
    public void completingRequestFreesTheVolunteer() {
        foodRequest.claimRequest(volunteer);
        foodRequest.completeRequest(coordinator);
        assertEquals(AvailabilityStatus.AVAILABLE, volunteer.getAvailabilityStatus());
    }

    @Test
    public void residentCannotCompleteRequest() {
        foodRequest.claimRequest(volunteer);
        assertFalse(foodRequest.completeRequest(user("dcarter", AccountRole.RESIDENT)));
    }

    @Test
    public void nullManagerCannotCompleteRequest() {
        foodRequest.claimRequest(volunteer);
        assertFalse(foodRequest.completeRequest(null));
    }

    @Test
    public void unclaimedRequestCannotBeCompleted() {
        assertFalse(foodRequest.completeRequest(coordinator));
    }

    @Test
    public void requestWithUnknownDestinationShelterCannotBeCompleted() {
        foodRequest.setDestinationShelterId(UUID.randomUUID());
        foodRequest.claimRequest(volunteer);
        assertFalse(foodRequest.completeRequest(coordinator));
    }

    @Test
    public void requestCannotBeCompletedBeforeHouseholdChecksIn() {
        Shelter shelter = shelterRunBy(user("rdelgado", AccountRole.SHELTER_OPERATOR));
        ReliefRequest request = assignedHouseholdHeadingTo(shelter);
        assertFalse(request.completeRequest(coordinator));
    }

    @Test
    public void operatorCanCompleteRequestCheckedIntoTheirShelter() {
        User operator = user("rdelgado", AccountRole.SHELTER_OPERATOR);
        Shelter shelter = shelterRunBy(operator);
        ReliefRequest request = assignedHouseholdHeadingTo(shelter);
        shelter.checkIn(request, operator);
        assertTrue(request.completeRequest(operator));
    }

    @Test
    public void operatorOfAnotherShelterCannotCompleteRequest() {
        User operator = user("rdelgado", AccountRole.SHELTER_OPERATOR);
        User otherOperator = user("tnguyen", AccountRole.SHELTER_OPERATOR);
        Shelter shelter = shelterRunBy(operator);
        shelterRunBy(otherOperator);
        ReliefRequest request = assignedHouseholdHeadingTo(shelter);
        shelter.checkIn(request, operator);
        assertFalse(request.completeRequest(otherOperator));
    }

    // cancel request workflow

    @Test
    public void cancellingSubmittedRequestMarksItCancelled() {
        foodRequest.cancel();
        assertEquals(RequestStatus.CANCELLED, foodRequest.getStatus());
    }

    @Test
    public void cancellingAssignedRequestFreesTheVolunteer() {
        foodRequest.claimRequest(volunteer);
        foodRequest.cancel();
        assertEquals(AvailabilityStatus.AVAILABLE, volunteer.getAvailabilityStatus());
    }

    @Test
    public void cancellingFulfilledRequestHasNoEffect() {
        foodRequest.claimRequest(volunteer);
        foodRequest.completeRequest(coordinator);
        foodRequest.cancel();
        assertEquals(RequestStatus.FULFILLED, foodRequest.getStatus());
    }
}
