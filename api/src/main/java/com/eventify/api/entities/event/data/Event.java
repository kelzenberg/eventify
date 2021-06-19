package com.eventify.api.entities.event.data;

import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.user.data.User;
import com.eventify.api.entities.usereventrole.data.UserEventRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "events")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Event extends BaseEntity {

    @NonNull
    @JsonView(Views.PublicShort.class)
    @Column(nullable = false)
    private String title;

    @NonNull
    @JsonView(Views.PublicShort.class)
    @Column
    private String description;

    @JsonView(Views.PublicShort.class)
    @Column
    private Date startedAt;

    @JsonView(Views.PublicShort.class)
    @Column
    private Date endedAt;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "event")
    private List<UserEventRole> userEventRoles;

    @Transient
    @JsonView(Views.PublicShort.class)
    private int amountOfUsers;

    public int getAmountOfUsers() {
        return this.userEventRoles == null ? 0 : this.userEventRoles.size();
    }

    @JsonView(Views.PublicExtended.class)
    @Transient
    private List<User> users;

    public List<User> getUsers() {
        return this.userEventRoles == null ? null :
                this.userEventRoles.stream().map(userEventRole -> {
                    User user = userEventRole.getUser();
                    user.setEventRole(userEventRole.getRole());
                    return user;
                }).collect(Collectors.toList());
    }

    @JsonView(Views.PublicExtended.class)
    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private List<ExpenseSharingModule> expenseSharingModules;

    @Builder
    public Event(@NonNull String title, @NonNull String description, Date startedAt, Date endedAt) {
        super();
        this.title = title;
        this.description = description;

        if (startedAt != null) {
            this.startedAt = startedAt;
        }

        if (endedAt != null) {
            this.endedAt = endedAt;
        }
    }
}
