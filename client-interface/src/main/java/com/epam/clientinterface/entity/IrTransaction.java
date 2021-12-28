package com.epam.clientinterface.entity;

import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ir_transaction", schema = "public")
public class IrTransaction {

    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "ir_transaction_id_seq", sequenceName = "ir_transaction_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ir_transaction_id_seq")
    private Long id;

    @Column(name = "balance", nullable = false)
    private double balance;

    @Column(name = "date", nullable = false)
    private ZonedDateTime date;

    @ManyToOne
    private Account account;

    public IrTransaction(double balance, @NonNull ZonedDateTime date, @NonNull Account account) {
        this.balance = balance;
        this.date = date;
        this.account = account;
    }
}
