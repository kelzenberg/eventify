package com.eventify.api.entities.event.controllers;

import com.eventify.api.ApplicationSecurityTestConfig;
import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.event.data.EventRepository;
import com.eventify.api.entities.event.services.EventService;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.user.data.UserRepository;
import com.eventify.api.entities.user.services.UserService;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.data.UserEventRoleRepository;
import com.eventify.api.entities.usereventrole.services.UserEventRoleService;
import com.eventify.api.handlers.exceptions.EntityNotFoundException;
import com.eventify.api.mail.services.MailService;
import com.eventify.api.mail.utils.MailUtil;
import com.eventify.api.utils.TestEntityUtil;
import com.eventify.api.utils.TestRequestUtil;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    private TestEntityUtil testEntityUtil;

    @Autowired
    private TestRequestUtil testRequestUtil;

    @SpyBean
    private MailService mailServiceSpy;

    @MockBean
    private MailUtil mailUtil;

    @InjectMocks
    private EventService eventService;
    @MockBean
    private EventRepository eventRepository;

    @InjectMocks
    private UserService userService;
    @MockBean
    private UserRepository userRepository;

    @SpyBean
    private UserEventRoleService userEventRoleService;
    @MockBean
    private UserEventRoleRepository userEventRoleRepository;

    /*
    Cannot operate on reflectively inserted IDs, according to:
    https://stackoverflow.com/questions/46671472/illegal-reflective-access-by-org-springframework-cglib-core-reflectutils1
     */

    @Test
    @WithMockUser
    void getMyEvents() throws Exception {
        User user = testEntityUtil.createTestUser();
        Event event = testEntityUtil.createTestEvent();
        List<UserEventRole> userEventRoles = List.of(testEntityUtil.createTestUserEventRole(user, event, Map.of("_", "_")));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userEventRoleRepository.findAllByIdUserId(user.getId())).thenReturn(userEventRoles);

        mockMvc.perform(testRequestUtil.getRequest("/me/events/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].title").value(event.getTitle()))
                .andExpect(jsonPath("$[0].description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void getAll() throws Exception {
        Event event = testEntityUtil.createTestEvent();

        when(eventRepository.findAll()).thenReturn(List.of(event));

        mockMvc.perform(testRequestUtil.getRequest("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", Matchers.is(1)))
                .andExpect(jsonPath("$[0].title").value(event.getTitle()))
                .andExpect(jsonPath("$[0].description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void getById() throws Exception {
        Event event = testEntityUtil.createTestEvent();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));

        mockMvc.perform(testRequestUtil.getRequest("/events/" + event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(event.getTitle()))
                .andExpect(jsonPath("$.description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void create() throws Exception {
        User user = testEntityUtil.createTestUser();
        Event event = testEntityUtil.createTestEvent();
        UserEventRole userEventRole = testEntityUtil.createTestUserEventRole(user, event, Map.of("_", "_"));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userEventRoleRepository.save(any(UserEventRole.class))).thenReturn(userEventRole);

        mockMvc.perform(testRequestUtil.postRequest(
                "/events",
                String.format("{\"title\": \"%s\", \"description\": \"%s\"}",
                        event.getTitle(),
                        event.getDescription()
                )
        ))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(event.getTitle()))
                .andExpect(jsonPath("$.description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void updateById() throws Exception {
        Event event = testEntityUtil.createTestEvent();
        Event updatedEvent = testEntityUtil.createTestEvent(Map.of(
                "title", "Test Event 1",
                "description", "Test Event Description"
                )
        );

        when(eventRepository.getOne(event.getId())).thenReturn(event);
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        mockMvc.perform(testRequestUtil.putRequest(
                "/events/" + event.getId(),
                String.format("{\"title\": \"%s\", \"description\": \"%s\"}",
                        updatedEvent.getTitle(),
                        updatedEvent.getDescription()
                )
        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedEvent.getTitle()))
                .andExpect(jsonPath("$.description").value(updatedEvent.getDescription()));
    }

    @Test
    @WithMockUser
    void inviteByIdWithoutUser() throws Exception {
        User user = testEntityUtil.createTestUser();
        Event event = testEntityUtil.createTestEvent();

        when(userRepository.findByEmail(user.getEmail())).thenThrow(new EntityNotFoundException("Test"));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        doNothing().when(mailUtil).sendMessagesInBatches(any());

        mockMvc.perform(testRequestUtil.postRequest(
                "/events/" + event.getId() + "/invite",
                String.format("{\"email\": \"%s\"}",
                        user.getEmail()
                )
        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());

        verify(mailServiceSpy, times(1)).sendInviteMail(any());
    }

    @Test
    @WithMockUser
    void inviteByIdWithUserWithoutUserEventRole() throws Exception {
        User user = testEntityUtil.createTestUser();
        Event event = testEntityUtil.createTestEvent();
        UserEventRole userEventRole = testEntityUtil.createTestUserEventRole(user, event, Map.of("role", EventRole.ATTENDEE));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userEventRoleRepository.findByIdUserIdAndIdEventId(user.getId(), event.getId())).thenThrow(new EntityNotFoundException("Test"));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userEventRoleRepository.save(any(UserEventRole.class))).thenReturn(userEventRole);

        mockMvc.perform(testRequestUtil.postRequest(
                "/events/" + event.getId() + "/invite",
                String.format("{\"email\": \"%s\"}",
                        user.getEmail()
                )
        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(event.getTitle()))
                .andExpect(jsonPath("$.description").value(event.getDescription()));

        verify(userEventRoleService, times(1)).create(user.getId(), event.getId(), EventRole.ATTENDEE);
    }

    @Test
    @WithMockUser
    void inviteByIdWithUserAndUserEventRole() throws Exception {
        User user = testEntityUtil.createTestUser();
        Event event = testEntityUtil.createTestEvent();
        UserEventRole userEventRole = testEntityUtil.createTestUserEventRole(user, event, Map.of("role", EventRole.ATTENDEE));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userEventRoleRepository.findByIdUserIdAndIdEventId(user.getId(), event.getId())).thenReturn(Optional.of(userEventRole));

        mockMvc.perform(testRequestUtil.postRequest(
                "/events/" + event.getId() + "/invite",
                String.format("{\"email\": \"%s\"}",
                        user.getEmail()
                )
        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(event.getTitle()))
                .andExpect(jsonPath("$.description").value(event.getDescription()));
    }

    @Test
    @WithMockUser
    void leaveById() throws Exception {
        User user = testEntityUtil.createTestUser();
        Event event = testEntityUtil.createTestEvent();
        UserEventRole userEventRole = testEntityUtil.createTestUserEventRole(user, event, Map.of("role", EventRole.ATTENDEE));

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        mockMvc.perform(testRequestUtil.postRequest(
                "/events/" + event.getId() + "/leave",
                ""
        ))
                .andExpect(status().isOk());

        verify(userEventRoleRepository, times(1)).deleteByIdUserIdAndIdEventId(user.getId(), event.getId());
    }

    @Test
    @WithMockUser
    void bounceById() throws Exception {
    }
}