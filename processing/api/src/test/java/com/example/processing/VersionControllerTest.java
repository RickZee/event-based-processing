package com.example.processing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @SpringBootTest
// @AutoConfigureMockMvc
// @WebMvcTest(VersionController.class)
// public class VersionControllerTest {
//     @Autowired
//     private MockMvc mockMvc;

//     @Test
//     void versionEndpointReturnsVersion() throws Exception {
//         mockMvc.perform(get("/version"))
//                 .andExpect(status().isOk())
//                 .andExpect(content().string("1.0.0"));
//     }
// }


@WebMvcTest(VersionController.class)
public class VersionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataSource dataSource; // Mock the DataSource to avoid real DB setup

    @Test
    void versionEndpointReturnsVersion() throws Exception {
        mockMvc.perform(get("/version"))
               .andExpect(status().isOk())
               .andExpect(content().string("1.0.0"));
    }
}