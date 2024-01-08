package com.example.helloworld.controllers;

import com.example.helloworld.HelloWorldApplication;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Tag("integration")
@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
public class SqlControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
    public void testStartFlinkCluster() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/sql/session"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().string("hello"));
    }
}
