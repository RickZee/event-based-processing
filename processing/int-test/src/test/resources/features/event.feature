Feature: Event processing
  Scenario: Submitting 1000 events increases row count by 1000
    Given I remember the event table row count
    When I submit 1000 events
    Then the event table row count increases by 1000

  Scenario: Get the count of events
    Given I remember the event table row count
    When I GET /events-count
    Then the response code should be 200
    And the response body should contain the remembered row count
