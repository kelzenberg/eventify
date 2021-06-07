package com.eventify.api.entities.event.data;

import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.Views;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Event extends BaseEntity {

    @Override // To also include parent's attributes for JsonView 'Short'
    @JsonView(Views.Short.class)
    public @NonNull UUID getId() {
        return super.getId();
    }

    @Override // To also include parent's attributes for JsonView 'Short'
    @JsonView(Views.Short.class)
    public @NonNull Date getCreatedAt() {
        return super.getCreatedAt();
    }

    @Override // To also include parent's attributes for JsonView 'Short'
    @JsonView(Views.Short.class)
    public @NonNull Date getUpdatedAt() {
        return super.getUpdatedAt();
    }

    @NonNull
    @JsonView(Views.Short.class)
    @Column(nullable = false)
    private String title;

    @JsonView(Views.Short.class)
    @Column
    private String description;

    @JsonView(Views.Short.class)
    @Column
    private Date startedAt;

    @JsonView(Views.Short.class)
    @Column
    private Date endedAt;

    @JsonView(Views.Short.class)
    @Formula("(SELECT count(uer.events_id) FROM user_event_roles uer WHERE uer.events_id = id)")
    private int amountOfUsers;

    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private List<ExpenseSharingModule> expenseSharingModules;

    @Builder
    public Event(@NonNull String title, @NonNull String description, Date startedAt) {
        super();
        this.title = title;
        this.description = description;

        if (startedAt != null) {
            this.startedAt = startedAt;
        }
    }
}
