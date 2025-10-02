package com.acme.qa.runners;

import io.cucumber.junit.platform.engine.Cucumber;
import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME, value = "com.acme.qa.steps,com.acme.qa.hooks")
@Cucumber
public class CucumberTest {
    // empty by design; Cucumber runs via JUnit Platform engine
}