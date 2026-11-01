package com.model;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import model.*;

/**
 * Tests important user actions without testing simple getters and setters.
 * @author Zaki Bushiri
 */
public class UserBehaviorTest {
    private User user;

    /** Creates a fresh user before every test. */
    @Before public void setUp() {
        user = new User("validUser", "correctPassword");
        ReliefRequestCatalog.getInstance().getRequests().clear();
    }

    /** Correct credentials allow login. */
    @Test public void correctCredentialsAllowLogin() {
        assertTrue(user.login("validUser", "correctPassword"));
    }

    /** Username capitalization does not prevent login. */
    @Test public void usernameLoginIgnoresCase() {
        assertTrue(user.login("VALIDUSER", "correctPassword"));
    }

    /** An incorrect username prevents login. */
    @Test public void incorrectUsernamePreventsLogin() {
        assertFalse(user.login("wrongUser", "correctPassword"));
    }

    /** An incorrect password prevents login. */
    @Test public void incorrectPasswordPreventsLogin() {
        assertFalse(user.login("validUser", "wrongPassword"));
    }

    /** A missing username prevents login. */
    @Test public void missingUsernamePreventsLogin() {
        assertFalse(user.login(null, "correctPassword"));
    }

    /** A missing password prevents login. */
    @Test public void missingPasswordPreventsLogin() {
        assertFalse(user.login("validUser", null));
    }

    /** A missing verification method cannot verify a user. */
    @Test public void missingVerificationMethodIsRejected() {
        assertFalse(user.verifyIdentity(null));
        assertEquals(VerificationStatus.UNVERIFIED, user.getVerificationStatus());
    }

    /** A valid verification method verifies a user. */
    @Test public void validVerificationMethodVerifiesUser() {
        assertTrue(user.verifyIdentity(VerificationMethod.ONE_TIME_PASSWORD));
        assertEquals(VerificationStatus.VERIFIED, user.getVerificationStatus());
    }

    /** Marking safe changes the status and update time. */
    @Test public void markSafeUpdatesSafetyInformation() {
        LocalDateTime before = LocalDateTime.now();
        user.markSafe();
        assertEquals(SafetyStatusType.SAFE, user.getSafetyStatus());
        assertFalse(user.getSafetyStatusUpdatedAt().isBefore(before));
    }

    /** Submitting a request records its owner and adds it to the catalog. */
    @Test public void submitRequestLinksUserAndCatalog() {
        ReliefRequest request = new ReliefRequest(RequestType.FOOD, "Need food", "Columbia", 1);
        user.submitReliefRequest(request);
        assertSame(user, request.getSubmittedBy());
        assertSame(request, ReliefRequestCatalog.getInstance().getRequest(request.getRequestId()));
    }

    /** A missing request is ignored. */
    @Test public void missingRequestIsIgnored() {
        user.submitReliefRequest(null);
        assertTrue(ReliefRequestCatalog.getInstance().getRequests().isEmpty());
    }
}
