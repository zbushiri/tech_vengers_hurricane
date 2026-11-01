package com.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import model.*;

/**
 * Tests request priority, state changes, and volunteer assignment.
 * @author Zaki Bushiri
 */
public class ReliefRequestTest {

    /** Transportation requests begin with low priority. */
    @Test public void transportationStartsLowPriority() {
        assertEquals(PriorityLevel.LOW, request(RequestType.TRANSPORTATION, 1).getPriority());
    }

    /** Food requests begin with medium priority. */
    @Test public void foodStartsMediumPriority() {
        assertEquals(PriorityLevel.MEDIUM, request(RequestType.FOOD, 1).getPriority());
    }

    /** Medical requests begin with high priority. */
    @Test public void medicalStartsHighPriority() {
        assertEquals(PriorityLevel.HIGH, request(RequestType.MEDICAL, 1).getPriority());
    }

    /** A large food request moves from medium to high. */
    @Test public void largeGroupRaisesPriority() {
        assertEquals(PriorityLevel.HIGH,
                request(RequestType.FOOD, ReliefRequest.LARGE_GROUP_SIZE).getPriority());
    }

    /** A large medical request moves from high to critical. */
    @Test public void largeMedicalGroupIsCritical() {
        assertEquals(PriorityLevel.CRITICAL,
                request(RequestType.MEDICAL, ReliefRequest.LARGE_GROUP_SIZE).getPriority());
    }

    /** Medical needs raise a normal request to high priority. */
    @Test public void medicalNeedsRaisePriority() {
        ReliefRequest request = request(RequestType.TRANSPORTATION, 1);
        request.setMedicalNeeds("Needs oxygen");
        assertEquals(PriorityLevel.HIGH, request.getPriority());
    }

    /** Blank details do not change the description. */
    @Test public void blankDetailIsIgnored() {
        ReliefRequest request = request(RequestType.FOOD, 1);
        request.addDetail("   ");
        assertEquals("Help needed", request.getDescription());
    }

    /** A useful detail is added on a new line. */
    @Test public void usefulDetailIsAdded() {
        ReliefRequest request = request(RequestType.FOOD, 1);
        request.addDetail("No transportation");
        assertEquals("Help needed\nNo transportation", request.getDescription());
    }

    /** Cancelling an open request closes it. */
    @Test public void cancelClosesOpenRequest() {
        ReliefRequest request = request(RequestType.FOOD, 1);
        request.cancel();
        assertEquals(RequestStatus.CANCELLED, request.getStatus());
        assertFalse(request.isOpen());
    }

    /** An unverified volunteer cannot claim a request. */
    @Test public void unverifiedVolunteerCannotClaim() {
        ReliefRequest request = request(RequestType.TRANSPORTATION, 1);
        request.claimRequest(volunteer(BackgroundCheckStatus.PENDING));
        assertEquals(RequestStatus.SUBMITTED, request.getStatus());
    }

    /** A verified and available volunteer can claim a request. */
    @Test public void verifiedVolunteerCanClaim() {
        ReliefRequest request = request(RequestType.TRANSPORTATION, 1);
        VolunteerProfile volunteer = volunteer(BackgroundCheckStatus.VERIFIED);
        request.claimRequest(volunteer);
        assertEquals(RequestStatus.ASSIGNED, request.getStatus());
        assertEquals(AvailabilityStatus.ON_ASSIGNMENT, volunteer.getAvailabilityStatus());
    }

    /** Only the assigned volunteer can start a request. */
    @Test public void differentVolunteerCannotStartRequest() {
        ReliefRequest request = request(RequestType.TRANSPORTATION, 1);
        request.claimRequest(volunteer(BackgroundCheckStatus.VERIFIED));
        assertFalse(request.startRequest(volunteer(BackgroundCheckStatus.VERIFIED)));
        assertEquals(RequestStatus.ASSIGNED, request.getStatus());
    }

    /** The assigned volunteer can move a request into progress. */
    @Test public void assignedVolunteerCanStartRequest() {
        ReliefRequest request = request(RequestType.TRANSPORTATION, 1);
        VolunteerProfile volunteer = volunteer(BackgroundCheckStatus.VERIFIED);
        request.claimRequest(volunteer);
        assertTrue(request.startRequest(volunteer));
        assertEquals(RequestStatus.IN_PROGRESS, request.getStatus());
    }

    /** A duplicate remains blocked while its original is open. */
    @Test public void openOriginalBlocksDuplicate() {
        ReliefRequest original = request(RequestType.FOOD, 1);
        ReliefRequest duplicate = request(RequestType.FOOD, 1);
        duplicate.setIsDuplicateOf(original);
        assertTrue(duplicate.hasOpenOriginal());
        assertFalse(duplicate.isClaimable());
    }

    /** Pet details update both the count and notes. */
    @Test public void petsUpdateCountAndNotes() {
        ReliefRequest request = request(RequestType.TRANSPORTATION, 1);
        request.setPets(List.of(new Pet("Buddy", "dog")));
        assertEquals(1, request.getNumberOfAnimals());
        assertEquals("Buddy (dog)", request.getAnimalNotes());
    }

    /** Creates a simple request for one test. */
    private ReliefRequest request(RequestType type, int people) {
        return new ReliefRequest(type, "Help needed", "123 Main Street", people);
    }

    /** Creates an available volunteer with the requested background status. */
    private VolunteerProfile volunteer(BackgroundCheckStatus status) {
        return new VolunteerProfile(new User("volunteer" + System.nanoTime(), "password"),
                List.of("transportation"), List.of(), status, true,
                AvailabilityStatus.AVAILABLE);
    }
}
