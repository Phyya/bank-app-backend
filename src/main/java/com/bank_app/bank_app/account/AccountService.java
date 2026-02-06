package com.bank_app.bank_app.account;


import com.bank_app.bank_app.account.dto.requests.CreateAccountRequest;
import com.bank_app.bank_app.account.dto.requests.UpdateBalanceRequest;
import com.bank_app.bank_app.account.dto.responses.AccountResponse;

public interface AccountService {
public Account getAccountByAccountNo(Long accountNumber);
public AccountResponse createAccount(CreateAccountRequest newAccount);
public AccountResponse updateAccountBalance(UpdateBalanceRequest newBalanceRequest);


}
