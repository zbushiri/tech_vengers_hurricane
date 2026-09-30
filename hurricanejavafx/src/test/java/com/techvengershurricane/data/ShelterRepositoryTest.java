package com.techvengershurricane.data;

import com.techvengershurricane.model.Shelter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShelterRepositoryTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void addEditAndDeleteShelter() {
        /* Test: proves the shared JSON CRUD path works. */
        ShelterRepository repository = new ShelterRepository(temporaryDirectory);
        Shelter shelter = new Shelter(10, "Test Shelter", "1 Main St", 25);

        repository.add(shelter);
        assertEquals(1, repository.getAll().size());

        shelter.setOccupancy(8);
        assertTrue(repository.edit(shelter));
        assertEquals(8, repository.findById("10").orElseThrow().getOccupancy());

        assertTrue(repository.delete("10"));
        assertFalse(repository.findById("10").isPresent());
    }
}
