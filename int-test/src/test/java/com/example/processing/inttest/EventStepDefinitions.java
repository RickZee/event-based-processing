package com.example.processing.inttest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EventStepDefinitions {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TestRestTemplate restTemplate;
    private int initialRowCount;

    @Given("the event table is empty")
    public void the_event_table_is_empty() {
        jdbcTemplate.execute("DELETE FROM event");
        initialRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM event", Integer.class);
        assertEquals(0, initialRowCount);
    }

    @When("I submit 1000 events")
    public void i_submit_1000_events() {
        for (int i = 0; i < 1000; i++) {
            restTemplate.postForEntity("/events", "{\"data\":\"test\"}", String.class);
        }
    }

    @Then("the event table row count increases by 1000")
    public void the_event_table_row_count_increases_by_1000() {
        int newRowCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM event", Integer.class);
        assertEquals(initialRowCount + 1000, newRowCount);
    }
}
