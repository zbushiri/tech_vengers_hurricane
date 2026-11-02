package com.model;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MapToolsTest {
    public static final String ADDRESS = "123 Test St";
    public static final String CITY = "Test City";
    public static final String STATE = "TS";
    public static final String ZIP = "12345";
    public static final Double LATITUDE = 29.951065;
    public static final Double LONGITUDE = -90.071533;
    private location.Address testAddress;
    private location.Coordinates testCoordinates;
    @Before
    public void setup() {
        testAddress = new location.Address(ADDRESS, CITY, STATE, ZIP);
        testCoordinates = new location.Coordinates(LATITUDE, LONGITUDE);
    }

    @Test
    @Ignore("Requires a live HURRICANE_API key and an external geocoding service.")
    public void testGetCoordinates() {
        location.Coordinates coordinates = testAddress.getCoordinates();
        assertEquals(LATITUDE, coordinates.getLatitude(), 0.0001);
        assertEquals(LONGITUDE, coordinates.getLongitude(), 0.0001);
    }

    @Test
    public void testAddressFormatting() {
        assertEquals("123 Test St, Test City, TS 12345", testAddress.toString());
    }

    @Test
    public void testCoordinatesFormatting() {
        assertEquals("29.951065, -90.071533", testCoordinates.toString());
    }
    @Test
    public void testMapLocationFormatting() {
        location.MapLocation mapLocation = new location.MapLocation(testAddress, testCoordinates);
        assertEquals("123 Test St, Test City, TS 12345", mapLocation.toString());
    }
    @Test
    public void testMapLocationWithNullCoordinates() {
        location.MapLocation mapLocation = new location.MapLocation(testAddress, null);
        assertEquals("123 Test St, Test City, TS 12345", mapLocation.toString());
    }
    @Test
    public void testMapLocationWithNullAddress() {
        location.MapLocation mapLocation = new location.MapLocation(null, testCoordinates);
        assertEquals("29.951065, -90.071533", mapLocation.toString());
    }
    @Test
    public void testMapLocationWithNullAddressAndCoordinates() {
        location.MapLocation mapLocation = new location.MapLocation(null, null);
        assertEquals("Unknown Location", mapLocation.toString());
    }
    @Test
    public void testCoordinatesWithNegativeValues() {
        location.Coordinates negativeCoordinates = new location.Coordinates(-29.951065, -90.071533);
        assertEquals("-29.951065, -90.071533", negativeCoordinates.toString());
    }
    @Test
    public void testCoordinatesWithZeroValues() {
        location.Coordinates zeroCoordinates = new location.Coordinates(0.0, 0.0);
        assertEquals("0.0, 0.0", zeroCoordinates.toString());
    }
    @Test
    public void testCoordinatesWithLargeValues() {
        location.Coordinates largeCoordinates = new location.Coordinates(90.0, 180.0);
        assertEquals("90.0, 180.0", largeCoordinates.toString());
    }
    @Test
    public void testCoordinatesWithSmallValues() {
        location.Coordinates smallCoordinates = new location.Coordinates(-90.0, -180.0);
        assertEquals("-90.0, -180.0", smallCoordinates.toString());
    }

}
