Feature: Event API
  Scenario: Create a new event
    Given the event repository is empty
    When I submit a POST request to /events with name "Cucumber Event" and description "Cucumber Description"
    Then the response status should be 201
    And the event repository should contain 1 event with name "Cucumber Event" and description "Cucumber Description"
