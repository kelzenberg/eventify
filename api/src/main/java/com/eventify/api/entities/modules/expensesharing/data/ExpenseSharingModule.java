package com.eventify.api.entities.modules.expensesharing.data;

import com.eventify.api.entities.modules.EventModule;
import com.eventify.api.entities.modules.expensesharing.entities.data.PaymentContribution;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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

    @Builder
    public ExpenseSharingModule(@NonNull String title, @NonNull String description) {
        super(title);
        this.description = description;
    }
}
