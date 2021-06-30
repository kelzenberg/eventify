package com.eventify.api;

import com.eventify.api.auth.utils.JwtTokenUtil;
import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.eventify.api.entities.usereventrole.data.UserEventRoleId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class EntityTestUtil {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public String createTestToken() {
        UserDetails userDetails = new MockUserDetailsWrapper("user@test.de", "password123", "ADMIN");
        return jwtTokenUtil.generateToken(userDetails);
    }

    public User createTestUser() {
        User user = User.builder()
                .email("user@test.de")
                .password("password123")
                .displayName("display name")
                .build();

        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
        return user;
    }

    public User createTestUser(Map<String, Object> args) {
        UUID id = (UUID) Optional.ofNullable(args.get("id")).orElse(UUID.randomUUID());
        String email = (String) Optional.ofNullable(args.get("email")).orElse("user@test.de");
        String password = (String) Optional.ofNullable(args.get("password")).orElse("password123");
        String displayName = (String) Optional.ofNullable(args.get("displayName")).orElse("display name");

        User user = User.builder()
                .email(email)
                .password(password)
                .displayName(displayName)
                .build();

        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    public Event createTestEvent() {
        Event event = Event.builder()
                .title("Test Event 1")
                .description("Test Event Description")
                .startedAt(new Date())
                .build();

        ReflectionTestUtils.setField(event, "id", UUID.randomUUID());
        return event;
    }

    public Event createTestEvent(Map<String, Object> args) {
        UUID id = (UUID) Optional.ofNullable(args.get("id")).orElse(UUID.randomUUID());
        String title = (String) Optional.ofNullable(args.get("title")).orElse("Test Event 1");
        String description = (String) Optional.ofNullable(args.get("description")).orElse("Test Event Description");
        Date startedAt = (Date) Optional.ofNullable(args.get("startedAt")).orElse(new Date());

        Event event = Event.builder()
                .title(title)
                .description(description)
                .startedAt(startedAt)
                .build();

        ReflectionTestUtils.setField(event, "id", id);
        return event;
    }

    public UserEventRole createTestUserEventRole(User user, Event event, Map<String, Object> args) {
        EventRole role = (EventRole) Optional.ofNullable(args.get("role")).orElse(EventRole.ORGANISER);

        UserEventRoleId id = new UserEventRoleId(user.getId(), event.getId());
        return UserEventRole.builder()
                .id(id)
                .user(user)
                .event(event)
                .role(role)
                .build();
    }
}
