package com.epam.clientinterface.entity;

import com.epam.clientinterface.enumerated.TransactionOperationType;
import com.epam.clientinterface.enumerated.TransactionState;
import java.time.LocalDateTime;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.lang.Nullable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "transaction", schema = "public")
public class Transaction {
    @Id
    @Column(name = "id", nullable = false)
    @SequenceGenerator(name = "transaction_id_seq", sequenceName = "transaction_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_id_seq")
    private Long id;

    @Nullable
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "accountNumber", column = @Column(name = "source_account_number")),
        @AttributeOverride(name = "isExternal", column = @Column(name = "source_is_external"))
    })
    private TransactionAccountData sourceAccount;

    @Nullable
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "accountNumber", column = @Column(name = "destination_account_number")),
        @AttributeOverride(name = "isExternal", column = @Column(name = "destination_is_external"))
    })
    private TransactionAccountData destinationAccount;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "date_time", nullable = false)
    private LocalDateTime dateTime;

    @Column(name = "operation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionOperationType operationType;

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionState state;

    public Transaction(
        @NonNull TransactionAccountData sourceAccount,
        @NonNull TransactionAccountData destinationAccount,
        double amount,
        @NonNull TransactionOperationType operationType,
        @NonNull TransactionState state
    ) {
        this.sourceAccount = sourceAccount;
        this.destinationAccount = destinationAccount;
        this.amount = amount;
        this.dateTime = LocalDateTime.now();
        this.operationType = operationType;
        this.state = state;
    }
}
