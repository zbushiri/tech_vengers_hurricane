package com.techvengershurricane.system;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExistingDataIntegrationTest {
    @Test
    void loadsEveryExistingJsonDataset() {
        /* Test: confirms the facade reads all sample files. */
        HurricaneReliefSystem system = new HurricaneReliefSystem(Path.of("..", "json"));

        assertEquals(2, system.getUsers().size());
        assertEquals(2, system.getVolunteers().size());
        assertEquals(2, system.getShelters().size());
        assertEquals(2, system.getRequests().size());
        assertEquals(3, system.getHurricanes().size());
    }
}
