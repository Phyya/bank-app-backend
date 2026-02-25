package com.bank_app.bank_app.account;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name="accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountNumber; // THIS is now the primary key

    private String bvn;
    private String accountName;
    private boolean isActive;
    private String password;



    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;



    public Account(String accountName, String bvn) {
        this.accountName = accountName;
        this.bvn = bvn;
        this.balance = BigDecimal.ZERO;
    }
    public Account(Long accountNumber) {
        this.accountNumber = accountNumber;

    }

}
