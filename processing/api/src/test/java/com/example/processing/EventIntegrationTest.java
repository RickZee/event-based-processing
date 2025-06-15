package com.example.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test()
    void post1000EventsToApi_databaseCountIncrementedBy1000() throws Exception {
        long before = eventRepository.count();
        for (int i = 0; i < 1000; i++) {
            String json = String.format(
                    "{\"eventTypeId\":\"%s\",\"eventTypeName\":\"Type%d\",\"tt\":\"2025-06-03T12:00:00Z\",\"vt\":\"2025-06-03T12:00:00Z\",\"schemaVersionId\":\"%s\",\"schemaVersionName\":\"v1\",\"producerName\":\"producer%d\",\"userId\":\"user%d\"}",
                    java.util.UUID.randomUUID(), i, java.util.UUID.randomUUID(), i, i);
            mockMvc.perform(post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }
        long after = eventRepository.count();
        Assertions.assertEquals(before + 1000, after, "Row count should increase by 1000 after POSTs");
    }

    @Test
    void save2EventsThenCheckEventsCount_getEventsCountIncrementedBy2() throws Exception {
        // Verify the count in the repository
        long before = eventRepository.count();
        
        for (int i = 0; i < 2; i++) {
            String json = String.format(
                    "{\"eventTypeId\":\"%s\",\"eventTypeName\":\"Type%d\",\"tt\":\"2025-06-03T12:00:00Z\",\"vt\":\"2025-06-03T12:00:00Z\",\"schemaVersionId\":\"%s\",\"schemaVersionName\":\"v1\",\"producerName\":\"producer%d\",\"userId\":\"user%d\"}",
                    java.util.UUID.randomUUID(), i, java.util.UUID.randomUUID(), i, i);
            mockMvc.perform(post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }

        String responseContent = mockMvc.perform(get("/events/events-count"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertNotNull(responseContent, "Response should not be null");
        long eventsCount = Long.parseLong(responseContent);

        Assertions.assertEquals(before + 2, eventsCount, "Events count should increase 2 after saving two events");
    }
}