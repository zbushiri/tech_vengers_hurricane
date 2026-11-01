package com.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** Runs all of the project's useful test classes together. */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    AccountFactoryTest.class,
    UserBehaviorTest.class,
    UserValidationTest.class,
    ReliefRequestTest.class,
    ReliefRequestCatalogTest.class,
    ScenarioWorkflowTest.class
})
public class AllTests {
}
