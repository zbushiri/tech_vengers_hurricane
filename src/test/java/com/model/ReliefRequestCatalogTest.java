package com.model;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import model.*;

/**
 * Tests request storage, duplicate detection, searching, and ordering.
 * @author Zaki Bushiri
 */
public class ReliefRequestCatalogTest {
    private ReliefRequestCatalog catalog;

    /** Clears the shared catalog before each test. */
    @Before public void setUp() {
        catalog = ReliefRequestCatalog.getInstance();
        catalog.getRequests().clear();
    }

    /** A missing request is not stored. */
    @Test public void missingRequestIsIgnored() {
        catalog.submitRequest(null);
        assertTrue(catalog.getRequests().isEmpty());
    }

    /** A request can be found by its identifier. */
    @Test public void requestCanBeFoundById() {
        ReliefRequest request = request(RequestType.FOOD, "Columbia", 1);
        catalog.submitRequest(request);
        assertSame(request, catalog.getRequest(request.getRequestId()));
    }

    /** An unknown identifier returns no request. */
    @Test public void unknownIdReturnsNull() {
        assertNull(catalog.getRequest(UUID.randomUUID()));
    }

    /** The same request cannot be stored twice. */
    @Test public void sameRequestCannotBeSubmittedTwice() {
        ReliefRequest request = request(RequestType.FOOD, "Columbia", 1);
        catalog.submitRequest(request);
        catalog.submitRequest(request);
        assertEquals(1, catalog.getRequests().size());
    }

    /** Type searches return only the requested type. */
    @Test public void searchReturnsRequestedType() {
        catalog.submitRequest(request(RequestType.FOOD, "A", 1));
        catalog.submitRequest(request(RequestType.SHELTER, "B", 1));
        assertEquals(1, catalog.searchByType(RequestType.FOOD).size());
        assertEquals(RequestType.FOOD, catalog.searchByType(RequestType.FOOD).get(0).getType());
    }

    /** Duplicate-location searches ignore case and outside spaces. */
    @Test public void duplicateLocationIgnoresCaseAndSpaces() {
        catalog.submitRequest(request(RequestType.FOOD, "  Columbia  ", 1));
        assertEquals(1, catalog.findDuplicates("columbia").size());
    }

    /** Same-type requests at the same location are linked as duplicates. */
    @Test public void sameTypeAndLocationBecomesDuplicate() {
        ReliefRequest first = request(RequestType.FOOD, "Columbia", 1);
        ReliefRequest second = request(RequestType.FOOD, "columbia", 1);
        catalog.submitRequest(first);
        catalog.submitRequest(second);
        assertSame(first, second.getIsDuplicateOf());
    }

    /** Different request types at one location are not duplicates. */
    @Test public void differentTypesAreNotDuplicates() {
        catalog.submitRequest(request(RequestType.FOOD, "Columbia", 1));
        ReliefRequest shelter = request(RequestType.SHELTER, "Columbia", 1);
        catalog.submitRequest(shelter);
        assertNull(shelter.getIsDuplicateOf());
    }

    /** Critical requests are returned before low-priority requests. */
    @Test public void openRequestsAreOrderedByPriority() {
        ReliefRequest low = request(RequestType.TRANSPORTATION, "A", 1);
        ReliefRequest critical = request(RequestType.MEDICAL, "B", ReliefRequest.LARGE_GROUP_SIZE);
        catalog.submitRequest(low);
        catalog.submitRequest(critical);
        assertSame(critical, catalog.getOpenRequestsByUrgency().get(0));
    }

    /** Cancelled requests are not returned as open. */
    @Test public void cancelledRequestsAreNotOpen() {
        ReliefRequest request = request(RequestType.FOOD, "Columbia", 1);
        catalog.submitRequest(request);
        request.cancel();
        assertTrue(catalog.getOpenRequestsByUrgency().isEmpty());
    }

    /** Shelter requests are excluded from volunteer work. */
    @Test public void shelterRequestIsNotVolunteerWork() {
        catalog.submitRequest(request(RequestType.SHELTER, "Columbia", 1));
        assertTrue(catalog.getOpenRequestsForVolunteers().isEmpty());
    }

    /** Claiming an unknown request fails safely. */
    @Test public void unknownRequestCannotBeClaimed() {
        assertFalse(catalog.claimRequest(UUID.randomUUID(), volunteer()));
    }

    /** Creates a request for one test. */
    private ReliefRequest request(RequestType type, String location, int people) {
        return new ReliefRequest(type, "Help needed", location, people);
    }

    /** Creates a verified, available volunteer. */
    private VolunteerProfile volunteer() {
        return new VolunteerProfile(new User("volunteer", "password"), List.of(), List.of(),
                BackgroundCheckStatus.VERIFIED, true, AvailabilityStatus.AVAILABLE);
    }
}
