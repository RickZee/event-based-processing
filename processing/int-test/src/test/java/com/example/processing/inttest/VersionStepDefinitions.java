package com.example.processing.inttest;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VersionStepDefinitions {
    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<String> response;

    @When("I GET /version")
    public void i_get_version() {
        response = restTemplate.getForEntity("/version", String.class);
    }

    @Then("the response status should be 200")
    public void the_response_status_should_be_200() {
        assertEquals(200, response.getStatusCodeValue());
    }

    @Then("the response body should be \"1.0.0\"")
    public void the_response_body_should_be_version() {
        assertEquals("1.0.0", response.getBody());
    }
}
