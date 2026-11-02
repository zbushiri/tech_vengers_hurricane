package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.Before;

public class RoutingServiceTest {

    private location.RoutingService routingService;

    @Before
    public void setUp() {
        routingService = new location.RoutingService() {
            @Override
            public location.RoutePlan route(
                    location.Address from,
                    location.Address to
            ) throws IOException {
                if (from == null || to == null) {
                    throw new java.io.IOException("Invalid address");
                }
                return new location.RoutePlan(10.0, Arrays.asList("Turn left", "Turn right"));
            }
        };
    }
    @Test
    public void testRouteValidAddresses() {
        location.Address from = new location.Address("123 Test St", "Test City", "TS", "12345");
        location.Address to = new location.Address("456 Test Ave", "Test City", "TS", "12345");
        try {
            location.RoutePlan routePlan = routingService.route(from, to);
            assertNotNull(routePlan);
            assertEquals(10.0, routePlan.distanceMeters(), 0.0001);
            assertEquals(Arrays.asList("Turn left", "Turn right"), routePlan.instructions());
        } catch (IOException e) {
            fail("IOException should not be thrown for valid addresses");
        }
    }

    @Test
    public void testRouteInvalidAddresses() {
        location.Address from = new location.Address("123 Test St", "Test City", "TS", "12345");
        location.Address to = null;
        try {
            routingService.route(from, to);
            fail("IOException should be thrown for invalid addresses");
        } catch (IOException e) {
            assertEquals("Invalid address", e.getMessage());
        }
    }

    @Test
    public void testRouteNullAddresses() {
        try {
            routingService.route(null, null);
            fail("IOException should be thrown for null addresses");
        } catch (IOException e) {
            assertEquals("Invalid address", e.getMessage());
        }
    }

}
