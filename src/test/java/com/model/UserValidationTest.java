package com.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import model.AccountRole;
import model.User;
import model.UserList;

/**
 * Tests important username and email rules when an account is added.
 * @author Zaki Bushiri
 */
public class UserValidationTest {
    private UserList users;

    /** Clears the shared user list before every test. */
    @Before
    public void setUp() {
        users = UserList.getInstance();
        users.getUsers().clear();
    }

    /** Checks that an empty username cannot be added. */
    @Test
    public void emptyUsernameIsRejected() {
        User user = new User("", "password");

        assertFalse(users.addUser(user));
    }

    /** Checks the five-character boundary for the six-character rule. */
    @Test
    public void usernameShorterThanSixCharactersIsRejected() {
        User user = new User("short", "password");

        assertFalse(users.addUser(user));
    }

    /** Checks that the same username cannot be added twice. */
    @Test
    public void duplicateUsernameIsRejected() {
        User firstUser = new User("uniqueUser", "password");
        User secondUser = new User("uniqueUser", "differentPassword");

        assertTrue(users.addUser(firstUser));
        assertFalse(users.addUser(secondUser));
    }

    /** Checks that capitalization cannot bypass the duplicate-name rule. */
    @Test
    public void duplicateUsernameWithDifferentCaseIsRejected() {
        User firstUser = new User("uniqueUser", "password");
        User secondUser = new User("UNIQUEUSER", "differentPassword");

        assertTrue(users.addUser(firstUser));
        assertFalse(users.addUser(secondUser));
    }

    /** Checks that an email without an at sign cannot be added. */
    @Test
    public void emailWithoutAtSignIsRejected() {
        boolean added = addUser("validUser", "zakiexample.com");

        assertFalse(added);
    }

    /** Checks that an email without a domain cannot be added. */
    @Test
    public void emailWithoutDomainIsRejected() {
        boolean added = addUser("validUser", "zaki@");

        assertFalse(added);
    }

    /** Checks that correct account information can still be added. */
    @Test
    public void validAccountIsAccepted() {
        boolean added = addUser("validUser", "zaki@example.com");

        assertTrue(added);
    }

    /** Adds a resident with the username and email being tested. */
    private boolean addUser(String username, String email) {
        return users.addUser(
                "Zaki",
                "Bushiri",
                email,
                "803-555-0100",
                "123 Main Street, Columbia, SC 29201",
                username,
                "password",
                new ArrayList<>(List.of(AccountRole.RESIDENT)),
                "123 Main Street, Columbia, SC 29201");
    }
}
