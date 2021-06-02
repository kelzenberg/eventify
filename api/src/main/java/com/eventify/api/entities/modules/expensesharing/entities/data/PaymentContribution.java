package com.eventify.api.entities.modules.expensesharing.entities.data;

import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import com.eventify.api.entities.modules.expensesharing.data.ExpenseSharingModule;
import com.eventify.api.entities.user.data.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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
    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User payer;

    @NonNull
    @JsonBackReference
    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "expense_sharing_modules_id", nullable = false)
    private ExpenseSharingModule expenseModule;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareType shareType;

    @JsonManagedReference
    @OneToMany(mappedBy = "paymentContribution")
    private List<CostShare> shares;

    @Builder
    public PaymentContribution(
            @NonNull ExpenseSharingModule expenseModule,
            @NonNull String title,
            @NonNull Double amount,
            @NonNull User payer,
            @NonNull ShareType shareType,
            @NonNull List<CostShare> shares
    ) {
        super();
        this.title = title;
        this.amount = amount;
        this.payer = payer;
        this.expenseModule = expenseModule;
        this.shareType = shareType;
        this.shares = shares;
    }
}
