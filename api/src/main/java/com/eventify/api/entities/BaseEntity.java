package com.eventify.api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @NonNull
    @Column(updatable = false, nullable = false)
    private UUID id;

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
    @Column(name = "updated_at")
    protected Date updatedAt;
}
