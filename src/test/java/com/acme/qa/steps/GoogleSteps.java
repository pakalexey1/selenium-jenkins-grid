package com.acme.qa.steps;

import com.acme.qa.core.TestContext;
import com.acme.qa.pages.GooglePage;
import io.cucumber.java.en.*;
import static org.assertj.core.api.Assertions.assertThat;

public class GoogleSteps {
    private final TestContext ctx;
    private GooglePage google;

    public GoogleSteps(TestContext ctx) { this.ctx = ctx; }

    @Given("I open Google")
    public void iOpenGoogle() {
        google = new GooglePage(ctx.driver());
        google.open(ctx.baseUrl());
    }

    @When("I search for {string}")
    public void iSearchFor(String term) {
        google.search(term);
    }

    @Then("I should see results containing {string}")
    public void iShouldSee(String expected) {
        assertThat(google.resultsContain(expected)).isTrue();
    }
}