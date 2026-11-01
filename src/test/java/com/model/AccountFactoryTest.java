package com.model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import model.*;

/**
 * Tests account creation without using the UI.
 * @author Zaki Bushiri
 */
public class AccountFactoryTest {

    /** A missing role cannot create an account. */
    @Test public void missingRoleIsRejected() {
        assertNull(AccountFactory.createAccount(null, validDetails()));
    }

    /** Missing account details cannot create an account. */
    @Test public void missingDetailsAreRejected() {
        assertNull(AccountFactory.createAccount(AccountRole.RESIDENT, null));
    }

    /** An empty username cannot create an account. */
    @Test public void emptyUsernameIsRejected() {
        Map<String, Object> details = validDetails();
        details.put("username", " ");
        assertNull(AccountFactory.createAccount(AccountRole.RESIDENT, details));
    }

    /** An empty password cannot create an account. */
    @Test public void emptyPasswordIsRejected() {
        Map<String, Object> details = validDetails();
        details.put("password", " ");
        assertNull(AccountFactory.createAccount(AccountRole.RESIDENT, details));
    }

    /** A resident account is created as a normal user. */
    @Test public void residentCreatesNormalUser() {
        User user = AccountFactory.createAccount(AccountRole.RESIDENT, validDetails());
        assertNotNull(user);
        assertFalse(user instanceof VolunteerProfile);
        assertTrue(user.hasRole(AccountRole.RESIDENT));
    }

    /** A volunteer account is created as a volunteer profile. */
    @Test public void volunteerCreatesVolunteerProfile() {
        User user = AccountFactory.createAccount(AccountRole.VOLUNTEER, validDetails());
        assertTrue(user instanceof VolunteerProfile);
        assertTrue(user.hasRole(AccountRole.VOLUNTEER));
    }

    /** A new volunteer starts pending and available. */
    @Test public void volunteerStartsPendingAndAvailable() {
        VolunteerProfile volunteer = (VolunteerProfile) AccountFactory.createAccount(
                AccountRole.VOLUNTEER, validDetails());
        assertEquals(BackgroundCheckStatus.PENDING, volunteer.getBackgroundCheckStatus());
        assertEquals(AvailabilityStatus.AVAILABLE, volunteer.getAvailabilityStatus());
        assertFalse(volunteer.canClaimRequests());
    }

    /** Comma-separated volunteer skills are trimmed and stored. */
    @Test public void commaSeparatedSkillsAreStored() {
        Map<String, Object> details = validDetails();
        details.put("skills", " food, transportation,  ");
        VolunteerProfile volunteer = (VolunteerProfile) AccountFactory.createAccount(
                AccountRole.VOLUNTEER, details);
        assertEquals(List.of("food", "transportation"), volunteer.getSkills());
    }

    /** Volunteer transportation access is read from the details. */
    @Test public void transportationAccessIsStored() {
        Map<String, Object> details = validDetails();
        details.put("transportationAccess", "true");
        VolunteerProfile volunteer = (VolunteerProfile) AccountFactory.createAccount(
                AccountRole.VOLUNTEER, details);
        assertTrue(volunteer.hasTransportationAccess());
    }

    /** Returns simple valid account information for each test. */
    private Map<String, Object> validDetails() {
        Map<String, Object> details = new HashMap<>();
        details.put("username", "validUser");
        details.put("password", "password");
        details.put("firstName", "Zaki");
        details.put("lastName", "Bushiri");
        details.put("email", "zaki@example.com");
        return details;
    }
}
