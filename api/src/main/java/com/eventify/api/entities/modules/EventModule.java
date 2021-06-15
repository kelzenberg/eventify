package com.eventify.api.entities.modules;

import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class EventModule extends BaseEntity {

    @NonNull
    @JsonView(Views.PublicExtended.class)
    @Column(nullable = false)
    private String title;

    public EventModule(@NonNull String title) {
        super();
        this.title = title;
    }

}
