package com.example.processing;

import com.example.processing.Event;
import com.example.processing.EventRepository;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {CucumberSpringConfiguration.class})
public class EventStepDefinitions {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EventRepository eventRepository;
    private MvcResult mvcResult;

    @Given("the event repository is empty")
    public void the_event_repository_is_empty() {
        eventRepository.deleteAll();
    }

    @When("I submit a POST request to /events with name {string} and description {string}")
    public void i_submit_a_post_request(String name, String description) throws Exception {
        String json = String.format("{\"name\":\"%s\",\"description\":\"%s\"}", name, description);
        mvcResult = mockMvc.perform(post("/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andReturn();
    }

    @Then("the response status should be {int}")
    public void the_response_status_should_be(int status) {
        Assertions.assertEquals(status, mvcResult.getResponse().getStatus());
    }

    @And("the event repository should contain {int} event with name {string} and description {string}")
    public void the_event_repository_should_contain_event(int count, String name, String description) {
        Assertions.assertEquals(count, eventRepository.count());
        Event event = eventRepository.findAll().get(0);
        Assertions.assertEquals(name, event.getName());
        Assertions.assertEquals(description, event.getDescription());
    }
}
