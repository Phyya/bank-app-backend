package com.bank_app.bank_app.transfer.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NameEnquiryRequest {
    private Long accountNumber;
    private String bankCode;  // Optional - if null, search internal DB
}
