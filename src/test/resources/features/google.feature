@smoke
Feature: Google search

Scenario: Basic search
  Given I open Google
  When I search for "Selenium WebDriver"
  Then I should see results containing "selenium"