package com.eventify.api.entities.event.controllers;

import com.eventify.api.ApplicationSecurityTestConfig;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import com.eventify.api.entities.event.services.EventService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ApplicationSecurityTestConfig.class,
        properties = {"command.line.runner.enabled=false"}
)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private EventService eventService;

    @MockBean
    private EventRepository eventRepository;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @WithMockUser
    void getAll() throws Exception {
        Event event = Event.builder()
                .title("Test Title")
                .description("Test Description")
                .build();
        when(eventRepository.findAll()).thenReturn(List.of(event));

        mockMvc.perform(get("/events").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].id").value(event.getId()));
    }

    @Test
    @WithMockUser
    void getById() throws Exception {
        UUID uuid = UUID.randomUUID();
        Event event = Event.builder()
                .title("Test Title")
                .description("Test Description")
                .build();

        when(eventRepository.findById(uuid)).thenReturn(Optional.of(event));

        mockMvc.perform(get("/events/" + uuid).secure(true))
                .andExpect(jsonPath("$.id").value(event.getId()));
    }
}