package com.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;


/** Runs all of the project's useful test classes together. */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AccountFactoryTest.class,
    UserBehaviorTest.class,
    UserValidationTest.class,
    ReliefRequestCatalogTest.class,
    ScenarioWorkflowTest.class,
    VolunteerProfileTest.class,
    HurricaneEventTest.class,
    HurricaneReliefAppTest.class,
    RequestRecommendationTest.class,
    VolunteerProfileTest.class,
    SupplyItemTest.class,
    ShelterTest.class
})
public class AllTests {
}
