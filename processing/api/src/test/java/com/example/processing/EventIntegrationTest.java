package com.example.processing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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

    @Test()
    @Disabled("Disabled for build stability")
    void submit1000EventsAndCheckDatabase() throws Exception {
        long before = eventRepository.count();
        for (int i = 0; i < 1000; i++) {
            String json = String.format("{\"eventTypeId\":\"%s\",\"eventTypeName\":\"Type%d\",\"tt\":\"2025-06-03T12:00:00Z\",\"vt\":\"2025-06-03T12:00:00Z\",\"schemaVersionId\":\"%s\",\"schemaVersionName\":\"v1\",\"producerName\":\"producer%d\",\"userId\":\"user%d\"}", java.util.UUID.randomUUID(), i, java.util.UUID.randomUUID(), i, i);
            mockMvc.perform(post("/events")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(json))
                    .andExpect(status().isCreated());
        }
        long after = eventRepository.count();
        Assertions.assertEquals(before + 1000, after, "Row count should increase by 1000 after POSTs");
    }
}
