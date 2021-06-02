package com.eventify.api.entities.event.data;

import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "events")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Event extends BaseEntity {

    @NonNull
    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column
    private Date startedAt;

    @Column
    private Date endedAt;

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
