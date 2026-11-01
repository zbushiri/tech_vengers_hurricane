package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import model.AccountRole;
import model.AvailabilityStatus;
import model.BackgroundCheckStatus;
import model.User;
import model.VolunteerProfile;

public class VolunteerProfileTest {
    private VolunteerProfile volunteer;
    
    @Before
    public void setup() {
        volunteer = new VolunteerProfile(
            new ArrayList<>(),
            new ArrayList<>(),
            BackgroundCheckStatus.VERIFIED,
            true,
            AvailabilityStatus.AVAILABLE
        );

    }

    @Test
    public void verifiedAvailableVolunteerCanClaim() {
        assertTrue("Verified and available volunteer should be able to claim", 
        canClaim());
    }

    @Test
   public void verifiedUnavailableVolunteerCannotClaim() {
        volunteer.setAvailability(AvailabilityStatus.UNAVAILABLE);
        assertFalse("Verified unavailable should not be able to claim",
        canClaim());
    }

    @Test
    public void unverifiedAvailableVolunteerCannotClaim() {
        volunteer.setBackgroundCheckStatus(BackgroundCheckStatus.PENDING);
        assertFalse("unverified Available should not be able to claim",
         canClaim());
    }

    @Test
    public void verifiedButOnAssignmentCannotClaim() {
        volunteer.setAvailabilityStatus(AvailabilityStatus.ON_ASSIGNMENT);
        assertFalse("verified on assignment should not be able to claim",
         canClaim());
    }

    @Test
    public void rejectedBackgroundCheckAvailableCannotClaim() {
        volunteer.setBackgroundCheckStatus(BackgroundCheckStatus.REJECTED);
        assertFalse("Rejected background check cannot claim request",
         canClaim());
    }

    @Test
    public void nullAvailabilityCannotClaim() {
        volunteer.setAvailability(null);
        assertFalse("null availability cannot claim", 
        canClaim());
    }

    @Test
    public void nullBackgroundCheckCannotClaim() {
        volunteer.setBackgroundCheckStatus(null);
        assertFalse("null background check cannot claim", 
            canClaim());
    }

    @Test
    public void creatingVolunteerAddsVolunteerRole() {
        assertTrue("New volunteer should have the Volunteer role",
         volunteer.hasRole(AccountRole.VOLUNTEER));
    }

    @Test
    public void nullSkillsBecomesEmptyList() {
        volunteer = new VolunteerProfile(
            null, 
            new ArrayList<>(), 
            BackgroundCheckStatus.VERIFIED,
            true,
            AvailabilityStatus.AVAILABLE);
        
            assertNotNull("Skills should not be null", 
                volunteer.getSkills());
            assertTrue("Skills should be empty",
                volunteer.getSkills().isEmpty());
    }

    @Test
    public void nullEquipmentBecomesEmptyList() {
        volunteer = new VolunteerProfile(
            new ArrayList<>(), 
            null, 
            BackgroundCheckStatus.VERIFIED,
            true,
            AvailabilityStatus.AVAILABLE);
        
            assertNotNull("Equipment should not be null", 
                volunteer.getEquipment());
            assertTrue("Equipment should be empty",
                volunteer.getEquipment().isEmpty());
    }

    @Test
    public void convertingUserPreservesAccountInformation() {
        User user = new User("charles", "password");

        VolunteerProfile converted = new VolunteerProfile(
            user,
            new ArrayList<>(),
            new ArrayList<>(),
            BackgroundCheckStatus.VERIFIED,
            true,
            AvailabilityStatus.AVAILABLE);

        assertEquals("Conversion should preserve the user ID",
            user.getUserId(), converted.getUserId());
        assertEquals("Conversion should preserve the username",
            user.getUsername(), converted.getUsername());
        assertEquals("Conversion should preserve the password",
            user.getPasswordHash(), converted.getPasswordHash());
    }

    @Test
    public void convertingUserDoesNotDuplicateVolunteerRole() {
        User user = new User("charles", "password");
        user.getRoles().add(AccountRole.VOLUNTEER);

        VolunteerProfile converted = new VolunteerProfile(
            user,
            new ArrayList<>(),
            new ArrayList<>(),
            BackgroundCheckStatus.VERIFIED,
            true,
            AvailabilityStatus.AVAILABLE);

        assertEquals("VOLUNTEER role should appear only once",
            1, java.util.Collections.frequency(
                converted.getRoles(), AccountRole.VOLUNTEER));
    }

    @Test
    public void convertingUserAddsVolunteerRole() {
        User user = new User("charles", "password");

        VolunteerProfile converted = new VolunteerProfile(
            user, new ArrayList<>(), new ArrayList<>(),
            BackgroundCheckStatus.VERIFIED, true,
            AvailabilityStatus.AVAILABLE
        );

        assertTrue("Converted user should have the VOLUNTEER role",
            converted.hasRole(AccountRole.VOLUNTEER));
    }

    @Test
    public void convertingUserPreservesExistingRoles() {
        User user = new User("charles", "password");
        user.getRoles().add(AccountRole.RESIDENT);

        VolunteerProfile converted = new VolunteerProfile(
            user, new ArrayList<>(), new ArrayList<>(),
            BackgroundCheckStatus.VERIFIED, true,
            AvailabilityStatus.AVAILABLE
        );

        assertTrue("Conversion should preserve the RESIDENT role",
            converted.hasRole(AccountRole.RESIDENT));
    }

    @Test
    public void convertingUserPreservesProfileDetails() {
        User user = new User("charles", "password");
        user.setFirstName("Charles");
        user.setLastName("Newhouse");
        user.setEmail("charles@example.com");
        user.setPhoneNumber("803-555-0100");
        user.setAddress("123 Main Street");

        VolunteerProfile converted = new VolunteerProfile(
            user, new ArrayList<>(), new ArrayList<>(),
            BackgroundCheckStatus.VERIFIED, true,
            AvailabilityStatus.AVAILABLE
        );

        assertEquals("Conversion should preserve first name",
            "Charles", converted.getFirstName());
        assertEquals("Conversion should preserve last name",
            "Newhouse", converted.getLastName());
        assertEquals("Conversion should preserve email",
            "charles@example.com", converted.getEmail());
        assertEquals("Conversion should preserve phone number",
            "803-555-0100", converted.getPhoneNumber());
        assertEquals("Conversion should preserve address",
            "123 Main Street", converted.getAddress());
    }

    @Test
    public void constructorPreservesSkills() {
        ArrayList<String> skills = new ArrayList<>();
        skills.add("transportation");

        volunteer = new VolunteerProfile(
            skills, new ArrayList<>(),
            BackgroundCheckStatus.VERIFIED, true,
            AvailabilityStatus.AVAILABLE
        );

        assertEquals("Constructor should preserve supplied skills",
            skills, volunteer.getSkills());
    }

    @Test
    public void constructorPreservesEquipment() {
        ArrayList<String> equipment = new ArrayList<>();
        equipment.add("truck");

        volunteer = new VolunteerProfile(
            new ArrayList<>(), equipment,
            BackgroundCheckStatus.VERIFIED, true,
            AvailabilityStatus.AVAILABLE
        );

        assertEquals("Constructor should preserve supplied equipment",
            equipment, volunteer.getEquipment());
    }

    private boolean canClaim() {
        return volunteer.canClaimRequests();
    }
}
