package com.bank_app.bank_app.transfer.dto.responses;

import com.bank_app.bank_app.transfer.TransferStatus;
import com.bank_app.bank_app.transfer.TransferType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransferResponse {

    private String referenceId;
    private Long sourceAccountNumber;
    private Long destinationAccountNumber;
    private String destinationBankCode;
    private String destinationBankName;
    private String destinationAccountName;
    private TransferType transferType;
    private BigDecimal amount;
    private String narration;
    private TransferStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
}
