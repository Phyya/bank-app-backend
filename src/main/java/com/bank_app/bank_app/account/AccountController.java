package com.bank_app.bank_app.account;


import com.bank_app.bank_app.account.dto.requests.CreateAccountRequest;
import com.bank_app.bank_app.account.dto.requests.UpdateBalanceRequest;
import com.bank_app.bank_app.account.dto.responses.AccountResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {


    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
    }
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Account> getAccountByAccountNo(@PathVariable Long accountNumber) {
        return new ResponseEntity<>(accountService.getAccountByAccountNo(accountNumber), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody CreateAccountRequest newAccount) {
        return new ResponseEntity<>(accountService.createAccount(newAccount), HttpStatus.CREATED);


    }
    @PatchMapping("/{accountNumber}/balance")
    public ResponseEntity<AccountResponse> updateBalance(
            @PathVariable Long accountNumber,
            @RequestBody UpdateBalanceRequest balanceRequest) {

        AccountResponse response =
                accountService.updateAccountBalance(accountNumber,balanceRequest);

        return ResponseEntity.ok(response);
    }

}
