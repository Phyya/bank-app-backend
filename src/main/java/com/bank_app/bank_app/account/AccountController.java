package com.bank_app.bank_app.account;


import com.bank_app.bank_app.account.dto.requests.CreateAccountRequest;
import com.bank_app.bank_app.account.dto.requests.UpdateBalanceRequest;
import com.bank_app.bank_app.account.dto.responses.AccountResponse;
import com.bank_app.bank_app.auth.impl.AuthServiceImpl;
import com.bank_app.bank_app.config.APIResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("")
    public ResponseEntity<APIResponse<List<AccountResponse>>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(APIResponse.success("Accounts retrieved successfully", accounts));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<APIResponse<AccountResponse>> getAccountByAccountNo(@PathVariable Long accountNumber) {
        AccountResponse account = accountService.getAccountByAccountNo(accountNumber);
        return ResponseEntity.ok(APIResponse.success("Account retrieved successfully", account));
    }

    @GetMapping("/{accountNumber}/name-enquiry")
    public ResponseEntity<APIResponse<String>> nameEnquiry(@PathVariable Long accountNumber) {
        String name = accountService.nameEnquiry(accountNumber);
        return ResponseEntity.ok(APIResponse.success("Name enquiry successful", name));
    }
    @GetMapping("/my-account")
    public ResponseEntity<APIResponse<AccountResponse>> getMyAccount(HttpSession session) {
        Long accountNumber = (Long) session.getAttribute(AuthServiceImpl.SESSION_ACCOUNT_NUMBER);
        if (accountNumber == null) {
            throw new IllegalArgumentException("Not authenticated. Kindly log in");
        }
        AccountResponse account = accountService.getAccountByAccountNo(accountNumber);
        return ResponseEntity.ok(APIResponse.success("Account retrieved successfully", account));
    }

    @PostMapping
    public ResponseEntity<APIResponse<AccountResponse>> createAccount(@Valid @RequestBody CreateAccountRequest newAccount) {
        AccountResponse response = accountService.createAccount(newAccount);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Account created successfully", response));
    }

    @PatchMapping("/{accountNumber}/balance")
    public ResponseEntity<APIResponse<AccountResponse>> updateBalance(
            @PathVariable Long accountNumber,
            @RequestBody UpdateBalanceRequest balanceRequest) {
        AccountResponse response = accountService.updateAccountBalance(accountNumber, balanceRequest);
        return ResponseEntity.ok(APIResponse.success("Balance updated successfully", response));
    }
}
