package com.eventify.api.entities.modules.expensesharing.entities.data;

import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "payment_contributions")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaymentContribution extends BaseEntity {

    @NonNull
    @Column(nullable = false)
    private String title;

    @NonNull
    @Column(nullable = false)
    private Double amount;

    @NonNull
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "expense_sharing_modules_id", nullable = false)
    private ExpenseSharingModule expenseSharingModule;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareType shareType;

    @Builder
    public PaymentContribution(@NonNull ExpenseSharingModule expenseSharingModule, @NonNull String title, @NonNull Double amount, @NonNull ShareType shareType) {
        super();
        this.title = title;
        this.amount = amount;
        this.expenseSharingModule = expenseSharingModule;
        this.shareType = shareType;
    }
}
