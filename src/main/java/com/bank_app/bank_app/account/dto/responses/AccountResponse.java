package com.bank_app.bank_app.account.dto.responses;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

    private Long accountNumber;
    private String accountName;
    private BigDecimal balance;

    // Custom constructor for just accountNumber and accountName
    public AccountResponse(Long accountNumber, String accountName) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = BigDecimal.ZERO;
    }



}
