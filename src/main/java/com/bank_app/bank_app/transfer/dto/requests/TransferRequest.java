package com.bank_app.bank_app.transfer.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequest {

    private Long sourceAccountNumber;
    private Long destinationAccountNumber;
    private BigDecimal amount;
    private String narration;
}
