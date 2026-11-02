package com.model;

import static org.junit.Assert.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import location.Address;
import location.RoutePlan;
import location.RoutingService;
import model.*;
import org.junit.Before;
import org.junit.Test;

public class ShelterListTest {
    private ShelterList list;
    private Shelter shelterA;
    private Shelter shelterB;
    private Address from;

    /** Fake map: Alpha Road is 2 miles away, Bravo Road is 5 miles away. No real API calls. */
    private final RoutingService routes = (start, end) -> {
        if (start == null || end == null) throw new IOException("Invalid test address");
        double miles = end.getStreet().contains("Alpha") ? 2 : 5;
        return new RoutePlan(miles * ShelterList.METERS_PER_MILE, List.of("TEST ROUTE ONLY"));
    };

    /** Fake map that always fails, like the real one with no internet. */
    private final RoutingService brokenRoutes = (start, end) -> {
        throw new IOException("Map service down");
    };

    @Before
    public void setUp() {
        list = ShelterList.getInstance();
        list.getShelters().clear();

        ArrayList<Accommodation> petFriendly = new ArrayList<>();
        petFriendly.add(Accommodation.PET_FRIENDLY);
        shelterA = new Shelter(UUID.randomUUID(), "Shelter A",
                "1 Alpha Road, Columbia, SC 29201", 10, 5, ShelterStatus.OPEN, petFriendly);
        shelterA.setPetCapacity(5);

        ArrayList<Accommodation> petFriendly2 = new ArrayList<>();
        petFriendly2.add(Accommodation.PET_FRIENDLY);
        shelterB = new Shelter(UUID.randomUUID(), "Shelter B",
                "2 Bravo Road, Columbia, SC 29201", 10, 5, ShelterStatus.OPEN, petFriendly2);
        shelterB.setPetCapacity(5);

        from = Address.fromString("25 Calhoun Street, Charleston, SC 29401");
    }

    /** Adds both shelters to the list. */
    private void addBoth() {
        list.addShelter(shelterA);
        list.addShelter(shelterB);
    }

    // ---------- addShelter ----------

    @Test
    public void testAddShelter_EmptyList_ShouldHaveOne() {
        list.addShelter(shelterA);
        int expected = 1;
        int actual = list.getShelters().size();
        assertEquals(expected, actual);
    }

    @Test
    public void testAddShelter_SameShelterTwice_ShouldHaveOne() {
        list.addShelter(shelterA);
        list.addShelter(shelterA);
        int expected = 1;
        int actual = list.getShelters().size();
        assertEquals(expected, actual);
    }

    @Test
    public void testAddShelter_Null_ShouldNotChangeList() {
        list.addShelter(null);
        int expected = 0;
        int actual = list.getShelters().size();
        assertEquals(expected, actual);
    }

    // ---------- findClosestShelter ----------

    @Test
    public void testFindClosestShelter_TwoMatch_ShouldReturnCloser() throws IOException {
        addBoth();
        Shelter expected = shelterA;
        Shelter actual = list.findClosestShelter(from, new ArrayList<>(), 3, 0, routes);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindClosestShelter_CloserIsFull_ShouldReturnOther() throws IOException {
        addBoth();
        shelterA.setOccupancy(10);
        Shelter expected = shelterB;
        Shelter actual = list.findClosestShelter(from, new ArrayList<>(), 3, 0, routes);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindClosestShelter_CloserIsClosed_ShouldReturnOther() throws IOException {
        addBoth();
        shelterA.setStatus(ShelterStatus.CLOSED);
        Shelter expected = shelterB;
        Shelter actual = list.findClosestShelter(from, new ArrayList<>(), 3, 0, routes);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindClosestShelter_OnlyFartherHasMedical_ShouldReturnFarther() throws IOException {
        addBoth();
        shelterB.getAccommodations().add(Accommodation.MEDICAL_SUPPORT);
        Shelter expected = shelterB;
        Shelter actual = list.findClosestShelter(from, List.of(Accommodation.MEDICAL_SUPPORT), 3, 0, routes);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindClosestShelter_PetAndCloserNotPetFriendly_ShouldReturnOther() throws IOException {
        addBoth();
        shelterA.getAccommodations().remove(Accommodation.PET_FRIENDLY);
        Shelter expected = shelterB;
        Shelter actual = list.findClosestShelter(from, new ArrayList<>(), 3, 1, routes);
        assertEquals(expected, actual);
    }

    @Test
    public void testFindClosestShelter_BothFull_ShouldReturnNull() throws IOException {
        addBoth();
        shelterA.setOccupancy(10);
        shelterB.setOccupancy(10);
        Shelter actual = list.findClosestShelter(from, new ArrayList<>(), 3, 0, routes);
        assertNull(actual);
    }

    @Test
    public void testFindClosestShelter_NullAddress_ShouldThrowException() {
        addBoth();
        assertThrows(IllegalArgumentException.class,
                () -> list.findClosestShelter(null, new ArrayList<>(), 3, 0, routes));
    }

    @Test
    public void testFindClosestShelter_ZeroPeople_ShouldThrowException() {
        addBoth();
        assertThrows(IllegalArgumentException.class,
                () -> list.findClosestShelter(from, new ArrayList<>(), 0, 0, routes));
    }

    @Test
    public void testFindClosestShelter_NegativePets_ShouldThrowException() {
        addBoth();
        assertThrows(IllegalArgumentException.class,
                () -> list.findClosestShelter(from, new ArrayList<>(), 3, -1, routes));
    }

    @Test
    public void testFindClosestShelter_NullNeeds_ShouldStillWork() throws IOException {
        addBoth();
        Shelter expected = shelterA;
        Shelter actual = list.findClosestShelter(from, null, 3, 0, routes);
        assertEquals(expected, actual);
    }

    // ---------- searchByProximity ----------

    @Test
    public void testSearchByProximity_BothInRadius_ShouldBeNearestFirst() {
        addBoth();
        List<Shelter> expected = List.of(shelterA, shelterB);
        List<Shelter> actual = list.searchByProximity("25 Calhoun Street, Charleston, SC 29401", 10, routes);
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByProximity_SmallRadius_ShouldLeaveOutFarther() {
        addBoth();
        List<Shelter> expected = List.of(shelterA);
        List<Shelter> actual = list.searchByProximity("25 Calhoun Street, Charleston, SC 29401", 3, routes);
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByProximity_ExactlyOnRadius_ShouldBeIncluded() {
        addBoth();
        boolean expected = true;
        boolean actual = list.searchByProximity("25 Calhoun Street, Charleston, SC 29401", 5, routes).contains(shelterB);
        assertEquals(expected, actual);
    }

    @Test
    public void testSearchByProximity_BadAddress_ShouldThrowException() {
        addBoth();
        assertThrows(IllegalArgumentException.class,
                () -> list.searchByProximity("nowhere", 10, routes));
    }

    @Test
    public void testSearchByProximity_NegativeRadius_ShouldThrowException() {
        addBoth();
        assertThrows(IllegalArgumentException.class,
                () -> list.searchByProximity("25 Calhoun Street, Charleston, SC 29401", -1, routes));
    }

    @Test
    public void testSearchByProximity_MapServiceFails_ShouldThrowException() {
        addBoth();
        assertThrows(IllegalStateException.class,
                () -> list.searchByProximity("25 Calhoun Street, Charleston, SC 29401", 10, brokenRoutes));
    }

    // ---------- filterByAccommodation ----------

    @Test
    public void testFilterByAccommodation_OnlyOneHasIt_ShouldReturnThatOne() {
        addBoth();
        shelterA.getAccommodations().add(Accommodation.MEDICAL_SUPPORT);
        List<Shelter> expected = List.of(shelterA);
        List<Shelter> actual = list.filterByAccommodation(Accommodation.MEDICAL_SUPPORT);
        assertEquals(expected, actual);
    }

    @Test
    public void testFilterByAccommodation_NoneHaveIt_ShouldReturnEmptyList() {
        addBoth();
        boolean expected = true;
        boolean actual = list.filterByAccommodation(Accommodation.MEDICAL_SUPPORT).isEmpty();
        assertEquals(expected, actual);
    }

    // ---------- verifyStatusReport ----------

    @Test
    public void testVerifyStatusReport_SameStatus_ShouldReturnTrue() {
        boolean expected = true;
        boolean actual = list.verifyStatusReport(shelterA, ShelterStatus.OPEN);
        assertEquals(expected, actual);
    }

    @Test
    public void testVerifyStatusReport_DifferentStatus_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = list.verifyStatusReport(shelterA, ShelterStatus.CLOSED);
        assertEquals(expected, actual);
    }

    @Test
    public void testVerifyStatusReport_NullShelter_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = list.verifyStatusReport(null, ShelterStatus.OPEN);
        assertEquals(expected, actual);
    }
}
