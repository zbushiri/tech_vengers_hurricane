package com.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.*;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.io.PrintStream;
import java.util.TimeZone;

public class HurricaneEventTest {
    private HurricaneEvent hurricane;
    private PrintStream originalOut;
    private TimeZone originalTimeZone;
    private ByteArrayOutputStream output;
    private PrintStream capturedOut;

    @Before 
    public void setup() {
        originalOut = System.out;
        originalTimeZone = TimeZone.getDefault();

        output = new ByteArrayOutputStream();
        capturedOut = new PrintStream(output);
        System.setOut(capturedOut);

        hurricane = new HurricaneEvent(
            "Helene",
            Date.from(Instant.parse("2026-10-21T00:00:00Z")),
            Date.from(Instant.parse("2026-10-28T00:00:00Z")),
            List.of("Charleston", "Columbia"),
            EventStatus.ACTIVE
        );
    }
    @After
    public void tearDown() {
        System.setOut(originalOut);
        capturedOut.close();
        TimeZone.setDefault(originalTimeZone);
    }

    @Test
    public void toStringIncludesHurricaneName() {
        assertTrue("Display should include the hurricane name",
            hurricane.toString().contains("name='Helene"));
    }

    @Test
    public void toStringIncludesStartDate() {
        assertTrue("Display should include the start date",
            hurricane.toString().contains("startDate=2026-10-21"));
    }

    @Test
    public void toStringIncludesEndDate() {
        assertTrue("Display should include the end date",
            hurricane.toString().contains("endDate=2026-10-28"));
    }

    @Test
    public void toStringIncludesAffectedAreas() {
        assertTrue("Display should include both affected areas",
            hurricane.toString().contains(
                "affectedAreas=[Charleston, Columbia]"));
    }

    @Test
    public void toStringIncludesStatus() {
        assertTrue("Display should include the event status",
            hurricane.toString().contains("status=ACTIVE"));
    }

    @Test
    public void nullStartDateDisplaysUnknown() {
        hurricane.setStartDate(null);

        assertTrue("Missing start date should display unknown",
            hurricane.toString().contains("startDate=unknown"));
    }

    @Test
    public void nullEndDateDisplaysUnknown() {
        hurricane.setEndDate(null);

        assertTrue("Missing end date should display unknown",
            hurricane.toString().contains("endDate=unknown"));
    }

    @Test
    public void datesDoNotShiftInEasternTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        String display = hurricane.toString();

        assertTrue("Start date should remain October 21 in Eastern time",
            display.contains("startDate=2026-10-21"));
        assertTrue("End date should remain October 28 in Eastern time",
            display.contains("endDate=2026-10-28"));
    }

    @Test
    public void issueAdvisoryPrintsMessage() {
        HurricaneEvent.issueAdvisory("Seek shelter");

        assertEquals("Advisory should print the supplied message",
            "Advisory issued: Seek shelter" + System.lineSeparator(),
            output.toString());
    }

    @Test
    public void refreshStormDataPrintsConfirmation() {
        HurricaneEvent.refreshStormData();

        assertEquals("Refreshing should print a confirmation",
            "Storm data refreshed." + System.lineSeparator(),
            output.toString());
    }
    
}
