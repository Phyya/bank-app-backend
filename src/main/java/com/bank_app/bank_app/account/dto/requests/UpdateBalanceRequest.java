package com.bank_app.bank_app.account.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceRequest {

    private BigDecimal amount;
    private Long accountNumber;
    private char flag;
}
