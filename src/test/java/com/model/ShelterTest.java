package com.model;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.*;
import org.junit.Before;
import org.junit.Test;

public class ShelterTest {
    private Shelter shelter;
    private User operator;
    private User coordinator;
    private User resident;

    @Before
    public void setUp() {
        ArrayList<Accommodation> accommodations = new ArrayList<>();
        accommodations.add(Accommodation.PET_FRIENDLY);
        shelter = new Shelter(UUID.randomUUID(), "Test Shelter",
                "100 Main Street, Columbia, SC 29201", 10, 5, ShelterStatus.OPEN, accommodations);
        shelter.setPetCapacity(5);

        operator = new User("operator1", "pw");
        operator.getRoles().add(AccountRole.SHELTER_OPERATOR);
        shelter.getShelterOperators().add(operator);

        coordinator = new User("coordinator1", "pw");
        coordinator.getRoles().add(AccountRole.COORDINATOR);

        resident = new User("resident1", "pw");
        resident.getRoles().add(AccountRole.RESIDENT);
    }

    /** Builds a family request headed to this shelter: the given number of people, optional dog and medical needs. */
    private ReliefRequest familyRequest(int people, boolean withDog, String medicalNeeds) {
        ReliefRequest request = new ReliefRequest(RequestType.TRANSPORTATION, "Flooding",
                "25 Calhoun Street, Charleston, SC 29401", people);
        ArrayList<Person> family = new ArrayList<>();
        for (int i = 0; i < people; i++) {
            family.add(new Person("Person" + i, "Carter", 30, new ArrayList<>()));
        }
        request.setFamilyMembers(family);
        if (withDog) {
            request.setPets(List.of(new Pet("Buddy", "dog")));
        }
        request.setMedicalNeeds(medicalNeeds);
        request.setDestinationShelterId(shelter.getShelterId());
        return request;
    }

    /** Has a verified, available volunteer claim the request so its status becomes ASSIGNED. */
    private void assignVolunteer(ReliefRequest request) {
        VolunteerProfile volunteer = new VolunteerProfile(new ArrayList<>(List.of("transportation")),
                new ArrayList<>(), BackgroundCheckStatus.VERIFIED, true, AvailabilityStatus.AVAILABLE);
        request.claimRequest(volunteer);
    }

    // hasSpace

    @Test
    public void testHasSpace_OpenWithRoom_ShouldReturnTrue() {
        boolean expected = true;
        boolean actual = shelter.hasSpace();
        assertEquals(expected, actual);
    }

    @Test
    public void testHasSpace_FullShelter_ShouldReturnFalse() {
        shelter.setOccupancy(10);
        boolean expected = false;
        boolean actual = shelter.hasSpace();
        assertEquals(expected, actual);
    }

    @Test
    public void testHasSpace_ClosedShelter_ShouldReturnFalse() {
        shelter.setStatus(ShelterStatus.CLOSED);
        boolean expected = false;
        boolean actual = shelter.hasSpace();
        assertEquals(expected, actual);
    }

    // getAvailableSpaces

    @Test
    public void testGetAvailableSpaces_OpenWithRoom_ShouldReturnFive() {
        int expected = 5;
        int actual = shelter.getAvailableSpaces();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAvailableSpaces_FullShelter_ShouldReturnZero() {
        shelter.setOccupancy(10);
        int expected = 0;
        int actual = shelter.getAvailableSpaces();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetAvailableSpaces_ClosedShelter_ShouldReturnZero() {
        shelter.setStatus(ShelterStatus.CLOSED);
        int expected = 0;
        int actual = shelter.getAvailableSpaces();
        assertEquals(expected, actual);
    }

    // updateStatus

    @Test
    public void testUpdateStatus_OperatorOnList_ShouldReturnTrue() {
        boolean expected = true;
        boolean actual = shelter.updateStatus(ShelterStatus.CLOSED, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateStatus_Coordinator_ShouldReturnTrue() {
        boolean expected = true;
        boolean actual = shelter.updateStatus(ShelterStatus.CLOSED, coordinator);
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateStatus_Resident_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.updateStatus(ShelterStatus.CLOSED, resident);
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateStatus_OperatorNotOnList_ShouldReturnFalse() {
        User otherOperator = new User("operator2", "pw");
        otherOperator.getRoles().add(AccountRole.SHELTER_OPERATOR);
        boolean expected = false;
        boolean actual = shelter.updateStatus(ShelterStatus.CLOSED, otherOperator);
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateStatus_NullStatus_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.updateStatus(null, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testUpdateStatus_NullUser_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.updateStatus(ShelterStatus.CLOSED, null);
        assertEquals(expected, actual);
    }

    // addAccommodation

    @Test
    public void testAddAccommodation_OperatorAddsNew_ShouldReturnTrue() {
        boolean expected = true;
        boolean actual = shelter.addAccommodation(Accommodation.WHEELCHAIR_ACCESSIBLE, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testAddAccommodation_Duplicate_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.addAccommodation(Accommodation.PET_FRIENDLY, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testAddAccommodation_NullAccommodation_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.addAccommodation(null, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testAddAccommodation_Resident_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.addAccommodation(Accommodation.WHEELCHAIR_ACCESSIBLE, resident);
        assertEquals(expected, actual);
    }

    // isShelterOperator

    @Test
    public void testIsShelterOperator_OperatorOnList_ShouldReturnTrue() {
        boolean expected = true;
        boolean actual = shelter.isShelterOperator(operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsShelterOperator_DifferentCapitalization_ShouldReturnTrue() {
        User capitals = new User("OPERATOR1", "pw");
        boolean expected = true;
        boolean actual = shelter.isShelterOperator(capitals);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsShelterOperator_Resident_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.isShelterOperator(resident);
        assertEquals(expected, actual);
    }

    @Test
    public void testIsShelterOperator_NullUser_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.isShelterOperator(null);
        assertEquals(expected, actual);
    }

    // setPetCapacity, setCapacity, setOccupancy

    @Test
    public void testSetPetCapacity_Negative_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> shelter.setPetCapacity(-1));
    }

    @Test
    public void testSetCapacity_Negative_ShouldNeverBeNegative() {
        shelter.setCapacity(-5);
        assertTrue(shelter.getCapacity() >= 0);
    }

    @Test
    public void testSetOccupancy_AboveCapacity_ShouldNotBeAllowed() {
        shelter.setOccupancy(15);
        assertTrue(shelter.getOccupancy() <= shelter.getCapacity());
    }

    @Test
    public void testSetOccupancy_FullShelterDropsBelowCapacity_ShouldReopen() {
        shelter.setOccupancy(10);
        shelter.setOccupancy(9);
        ShelterStatus expected = ShelterStatus.OPEN;
        ShelterStatus actual = shelter.getStatus();
        assertEquals(expected, actual);
    }

    // toString

    @Test
    public void testToString_ShouldIncludeShelterName() {
        String text = shelter.toString();
        assertTrue(text.contains("Test Shelter"));
    }

    // canAccommodate

    @Test
    public void testCanAccommodate_FamilyOfThree_ShouldReturnTrue() {
        ReliefRequest request = familyRequest(3, false, "");
        boolean expected = true;
        boolean actual = shelter.canAccommodate(request);
        assertEquals(expected, actual);
    }

    @Test
    public void testCanAccommodate_FamilyLargerThanSpace_ShouldReturnFalse() {
        ReliefRequest request = familyRequest(6, false, "");
        boolean expected = false;
        boolean actual = shelter.canAccommodate(request);
        assertEquals(expected, actual);
    }

    @Test
    public void testCanAccommodate_MedicalNeedsWithoutMedicalSupport_ShouldReturnFalse() {
        ReliefRequest request = familyRequest(3, false, "Asthma");
        boolean expected = false;
        boolean actual = shelter.canAccommodate(request);
        assertEquals(expected, actual);
    }

    @Test
    public void testCanAccommodate_FamilyWithDog_ShouldReturnTrue() {
        ReliefRequest request = familyRequest(3, true, "");
        boolean expected = true;
        boolean actual = shelter.canAccommodate(request);
        assertEquals(expected, actual);
    }

    @Test
    public void testCanAccommodate_ClosedShelter_ShouldReturnFalse() {
        shelter.setStatus(ShelterStatus.CLOSED);
        ReliefRequest request = familyRequest(3, false, "");
        boolean expected = false;
        boolean actual = shelter.canAccommodate(request);
        assertEquals(expected, actual);
    }

    // checkIn

    @Test
    public void testCheckIn_OperatorAssignedFamily_ShouldReturnTrue() {
        ReliefRequest request = familyRequest(3, true, "");
        assignVolunteer(request);
        boolean expected = true;
        boolean actual = shelter.checkIn(request, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckIn_Resident_ShouldReturnFalse() {
        ReliefRequest request = familyRequest(3, true, "");
        assignVolunteer(request);
        boolean expected = false;
        boolean actual = shelter.checkIn(request, resident);
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckIn_DifferentDestinationShelter_ShouldReturnTrue() {
        ReliefRequest request = familyRequest(3, true, "");
        request.setDestinationShelterId(UUID.randomUUID());
        assignVolunteer(request);
        boolean expected = true;
        boolean actual = shelter.checkIn(request, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckIn_RequestStillSubmitted_ShouldReturnFalse() {
        ReliefRequest request = familyRequest(3, true, "");
        boolean expected = false;
        boolean actual = shelter.checkIn(request, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckIn_SameFamilyTwice_ShouldReturnFalse() {
        ReliefRequest request = familyRequest(3, true, "");
        assignVolunteer(request);
        shelter.checkIn(request, operator);
        boolean expected = false;
        boolean actual = shelter.checkIn(request, operator);
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckIn_FamilyOfThreeAndDog_OccupancyShouldBeEight() {
        ReliefRequest request = familyRequest(3, true, "");
        assignVolunteer(request);
        shelter.checkIn(request, operator);
        int expected = 8;
        int actual = shelter.getOccupancy();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckIn_FamilyOfThreeAndDog_OccupantsShouldBeThree() {
        ReliefRequest request = familyRequest(3, true, "");
        assignVolunteer(request);
        shelter.checkIn(request, operator);
        int expected = 3;
        int actual = shelter.getOccupants().size();
        assertEquals(expected, actual);
    }

    @Test
    public void testCheckIn_FamilyOfThreeAndDog_PetsShouldBeOne() {
        ReliefRequest request = familyRequest(3, true, "");
        assignVolunteer(request);
        shelter.checkIn(request, operator);
        int expected = 1;
        int actual = shelter.getPets().size();
        assertEquals(expected, actual);
    }

    @Test
    public void testCanAccommodate_NullRequest_ShouldReturnFalse() {
        boolean expected = false;
        boolean actual = shelter.canAccommodate(null);
        assertEquals(expected, actual);
    }
}
