package com.eventify.api.entities.modules.expensesharing.entities.data;

import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.user.data.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "cost_shares")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CostShare extends BaseEntity {

    @NonNull
    @Column(nullable = false)
    private Double amount;

    @NonNull
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User shareHolder;

    @NonNull
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "payment_contributions_id", nullable = false)
    private PaymentContribution paymentContribution;

    @Builder
    public CostShare(@NonNull Double amount, @NonNull User shareHolder, @NonNull PaymentContribution paymentContribution) {
        super();
        this.amount = amount;
        this.shareHolder = shareHolder;
        this.paymentContribution = paymentContribution;
    }
}
