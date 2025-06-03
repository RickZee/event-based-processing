Feature: Event processing
  Scenario: Submitting 1000 events increases row count by 1000
    Given the event table is empty
    When I submit 1000 events
    Then the event table row count increases by 1000
