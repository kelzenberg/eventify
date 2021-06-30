package com.eventify.api.entities.event.controllers;

import com.eventify.api.ApplicationSecurityTestConfig;
import com.eventify.api.EntityTestUtil;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import com.eventify.api.entities.event.services.EventService;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.data.UserRepository;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.data.UserEventRoleRepository;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Autowired
    private EntityTestUtil entityTestUtil;

    @InjectMocks
    private EventService eventService;
    @MockBean
    private EventRepository eventRepository;

    @InjectMocks
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private UserEventRoleService userEventRoleService;
    @MockBean
    private UserEventRoleRepository userEventRoleRepository;

    String token = "";

    @BeforeEach
    void setUp() {
        token = entityTestUtil.createTestToken();
    }

    @AfterEach
    void tearDown() {
        token = "";
    }

    /*
    Cannot operate on reflectively inserted IDs, according to:
    https://stackoverflow.com/questions/46671472/illegal-reflective-access-by-org-springframework-cglib-core-reflectutils1
     */

    @Test
    @WithMockUser
    void getMyEvents() throws Exception {
        User user = entityTestUtil.createTestUser();
        Event event = entityTestUtil.createTestEvent();
        List<UserEventRole> userEventRoles = List.of(entityTestUtil.createTestUserEventRole(user, event, Map.of("_", "_")));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userEventRoleRepository.findAllByIdUserId(user.getId())).thenReturn(userEventRoles);

        mockMvc.perform(
                get("/me/events/").secure(true)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].title").value(event.getTitle()))
                .andExpect(jsonPath("$[0].description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void getAll() throws Exception {
        Event event = entityTestUtil.createTestEvent();

        when(eventRepository.findAll()).thenReturn(List.of(event));

        mockMvc.perform(
                get("/events").secure(true)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].title").value(event.getTitle()))
                .andExpect(jsonPath("$[0].description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void getById() throws Exception {
        Event event = entityTestUtil.createTestEvent();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        mockMvc.perform(
                get("/events/" + event.getId()).secure(true)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(event.getTitle()))
                .andExpect(jsonPath("$.description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void create() throws Exception {
        User user = entityTestUtil.createTestUser();
        Event event = entityTestUtil.createTestEvent();
        UserEventRole userEventRole = entityTestUtil.createTestUserEventRole(user, event, Map.of("_", "_"));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userEventRoleRepository.save(any(UserEventRole.class))).thenReturn(userEventRole);

        mockMvc.perform(
                post("/events").secure(true)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                String.format("{\"title\": \"%s\", \"description\": \"%s\"}",
                                        event.getTitle(),
                                        event.getDescription()
                                )
                        )
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(event.getTitle()))
                .andExpect(jsonPath("$.description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void updateById() throws Exception {
    }

    @Test
    @WithMockUser
    void inviteById() throws Exception {
    }

    @Test
    @WithMockUser
    void leaveById() throws Exception {
    }

    @Test
    @WithMockUser
    void bounceById() throws Exception {
    }
}