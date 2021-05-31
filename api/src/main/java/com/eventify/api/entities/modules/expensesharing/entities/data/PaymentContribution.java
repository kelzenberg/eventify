package com.eventify.api.entities.modules.expensesharing.entities.data;

import com.eventify.api.entities.BaseEntity;
import com.eventify.api.entities.modules.expensesharing.constants.ShareType;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "expense_sharing_modules")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PaymentContribution extends BaseEntity {

    @NonNull
    @Column
    private String title;

    @NonNull
    @Column
    private Float amount;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShareType shareType;

    @Builder
    public PaymentContribution(@NonNull String title, @NonNull Float amount, @NonNull ShareType shareType) {
        super();
        this.title = title;
        this.amount = amount;
        this.shareType = shareType;
    }
}
