package com.example.processing.inttest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventStepDefinitions {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestRestTemplate restTemplate;
    private int initialRowCount;
    private ResponseEntity<String> response;
    private static final String EVENT_TABLE = "events";

    @Given("the event table is empty")
    public void the_event_table_is_empty() {
        jdbcTemplate.execute("DELETE FROM " + EVENT_TABLE);
        initialRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + EVENT_TABLE, Integer.class);
        assertEquals(0, initialRowCount);
    }

    @When("I submit 1000 events")
    public void i_submit_1000_events() {
        for (int i = 0; i < 1000; i++) {
            String json = String.format(
                    "{\"eventTypeId\":\"%s\",\"eventTypeName\":\"Type%d\",\"tt\":\"2025-06-03T12:00:00Z\",\"vt\":\"2025-06-03T12:00:00Z\",\"schemaVersionId\":\"%s\",\"schemaVersionName\":\"v1\",\"producerName\":\"producer%d\",\"userId\":\"user%d\"}",
                    java.util.UUID.randomUUID(), i, java.util.UUID.randomUUID(), i, i);
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(json, headers);
            restTemplate.postForEntity("/events", entity, String.class);
        }
    }

    @Then("the event table row count increases by 1000")
    public void the_event_table_row_count_increases_by_1000() {
        int newRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + EVENT_TABLE, Integer.class);
        assertEquals(initialRowCount + 1000, newRowCount);
    }

    @Given("I remember the event table row count")
    public void i_remember_the_event_table_row_count() {
        initialRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + EVENT_TABLE, Integer.class);
    }

    @When("I GET \\/events-count")
    public void i_get_events_count() {
        response = restTemplate.getForEntity("/events-count", String.class);
    }

    @Then("the response code should be 200")
    public void the_response_code_should_be_200() {
        assertEquals(200, response.getStatusCode().value());
    }

    @Then("the response body should contain the remembered row count")
    public void the_response_body_should_contain_the_remembered_row_count() {
        assertTrue(response.getBody().contains(String.valueOf(initialRowCount)));
    }
}
