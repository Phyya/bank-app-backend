package com.bank_app.bank_app.account;


import com.bank_app.bank_app.account.dto.requests.CreateAccountRequest;
import com.bank_app.bank_app.account.dto.requests.UpdateBalanceRequest;
import com.bank_app.bank_app.account.dto.responses.AccountResponse;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();
    Account getAccountByAccountNo(Long accountNumber);
    AccountResponse createAccount(CreateAccountRequest newAccount);
    AccountResponse updateAccountBalance(Long accountNumber, UpdateBalanceRequest newBalanceRequest);
}
