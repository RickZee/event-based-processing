package com.example.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EventIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private EventRepository eventRepository;

    @Test
    void submit1000EventsAndCheckDatabase() throws Exception {
        eventRepository.deleteAll();
        for (int i = 0; i < 1000; i++) {
            String json = String.format("{\"name\":\"Event %d\",\"description\":\"Description %d\"}", i, i);
            mockMvc.perform(post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }
        long count = eventRepository.count();
        Assertions.assertEquals(1000, count, "There should be 1000 events in the database");
    }
}
