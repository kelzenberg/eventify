package com.eventify.api.entities.usereventrole.data;

import com.eventify.api.constants.EventRole;
import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.user.data.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_event_roles")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
@EntityListeners(AuditingEntityListener.class)
public class UserEventRole {

    @NonNull
    @EmbeddedId
    private UserEventRoleId id;

    @NonNull
    @JsonBackReference
    @ToString.Exclude
    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @NonNull
    @JsonBackReference
    @ToString.Exclude
    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "events_id", nullable = false)
    private Event event;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventRole role;

    @CreatedBy
    @NonNull
    @JsonIgnore
    @ToString.Exclude
    @Column(name = "created_by", nullable = false, updatable = false)
    protected String createdBy;

    @LastModifiedBy
    @NonNull
    @JsonIgnore
    @ToString.Exclude
    @Column(name = "updated_by")
    protected String lastModifiedBy;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @NonNull
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Date createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @NonNull
    @Column(name = "updated_at", nullable = false)
    protected Date updatedAt;

    @Builder
    public UserEventRole(@NonNull UserEventRoleId id, @NonNull User user, @NonNull Event event, @NonNull EventRole role) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.role = role;
    }
}
