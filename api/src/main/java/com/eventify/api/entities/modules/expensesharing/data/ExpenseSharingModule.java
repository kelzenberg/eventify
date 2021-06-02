package com.eventify.api.entities.modules.expensesharing.data;

import com.eventify.api.entities.event.data.Event;
import com.eventify.api.entities.modules.EventModule;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "expense_sharing_modules")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ExpenseSharingModule extends EventModule {

    @NonNull
    @Column(nullable = false)
    private String description;

    @JsonManagedReference
    @OneToMany(mappedBy = "expenseModule")
    private List<PaymentContribution> payments;

    @NonNull
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "events_id", nullable = false)
    private Event event;

    @Builder
    public ExpenseSharingModule(@NonNull String title, @NonNull String description, @NonNull Event event) {
        super(title);
        this.description = description;
        this.event = event;
    }
}
