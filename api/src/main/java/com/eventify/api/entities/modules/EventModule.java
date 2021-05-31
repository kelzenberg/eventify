package com.eventify.api.entities.modules;

import com.eventify.api.entities.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class EventModule extends BaseEntity {

    @NonNull
    @Column(nullable = false)
    private String title;

}
