package com.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import model.RequestRecommendation;

/** Runs all of the project's useful test classes together. */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AccountFactoryTest.class,
    UserBehaviorTest.class,
    UserValidationTest.class,
    ReliefRequestCatalogTest.class,
    ScenarioWorkflowTest.class,
    HurricaneReliefAppTest.class,
    RequestRecommendationTest.class,
    VolunteerProfileTest.class
})
public class AllTests {
}
