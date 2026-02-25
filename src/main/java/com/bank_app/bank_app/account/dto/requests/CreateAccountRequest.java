package com.bank_app.bank_app.account.dto.requests;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    @NotBlank(message="Account Name is required")
    @Size(max=100, message="Account name must not exceed 100 characters")
    private String accountName;

    @NotNull
    @Size(min=11, max=11, message="BVN must be 11 digits")
    private String bvn;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
