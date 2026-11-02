package com.model;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.*;
import org.junit.Before;
import org.junit.Test;

public class ShelterAdmissionTest {
    private UUID requestId;
    private ArrayList<Person> people;
    private ArrayList<Pet> pets;

    @Before
    public void setUp() {
        requestId = UUID.randomUUID();
        people = new ArrayList<>();
        people.add(new Person("David", "Carter", 38, new ArrayList<>()));
        people.add(new Person("Elena", "Carter", 36, new ArrayList<>()));
        pets = new ArrayList<>();
        pets.add(new Pet("Buddy", "dog"));
    }

    @Test
    public void testCreate_TwoPeopleOnePet_ShouldKeepTwoPeople() {
        ShelterAdmission admission = new ShelterAdmission(requestId, people, pets);
        int expected = 2;
        int actual = admission.people().size();
        assertEquals(expected, actual);
    }

    @Test
    public void testCreate_NoPets_ShouldHaveZeroPets() {
        ShelterAdmission admission = new ShelterAdmission(requestId, people, new ArrayList<>());
        int expected = 0;
        int actual = admission.pets().size();
        assertEquals(expected, actual);
    }

    @Test
    public void testCreate_NullRequestId_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ShelterAdmission(null, people, pets));
    }

    @Test
    public void testCreate_NullPeople_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ShelterAdmission(requestId, null, pets));
    }

    @Test
    public void testCreate_EmptyPeople_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> new ShelterAdmission(requestId, new ArrayList<>(), pets));
    }

    @Test
    public void testCreate_NullPets_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ShelterAdmission(requestId, people, null));
    }

    @Test
    public void testCreate_OriginalListChangedAfter_ShouldShowThreePeople() {
        ShelterAdmission admission = new ShelterAdmission(requestId, people, pets);
        people.add(new Person("Sophie", "Carter", 9, new ArrayList<>()));
        int expected = 3;
        int actual = admission.people().size();
        assertEquals(expected, actual);
    }
}
