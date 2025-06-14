Feature: Version endpoint
  Scenario: Get API version
    When I GET version
    Then the response status should be 200
    And the response body should be "1.0.0"
