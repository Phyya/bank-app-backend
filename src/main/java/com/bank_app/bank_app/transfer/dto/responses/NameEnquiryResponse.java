package com.bank_app.bank_app.transfer.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NameEnquiryResponse {
    private Long accountNumber;
    private String accountName;
    private String bankCode;
    private String bankName;
    private boolean isInternal;
}
