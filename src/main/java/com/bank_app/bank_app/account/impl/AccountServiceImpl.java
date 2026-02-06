package com.bank_app.bank_app.account.impl;


import com.bank_app.bank_app.account.Account;
import com.bank_app.bank_app.account.AccountRepository;
import com.bank_app.bank_app.account.dto.requests.CreateAccountRequest;
import com.bank_app.bank_app.account.AccountService;
import com.bank_app.bank_app.account.dto.requests.UpdateBalanceRequest;
import com.bank_app.bank_app.account.dto.responses.AccountResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public Account getAccountByAccountNo (Long accountNumber){

        return accountRepository.findByAccountNumber(accountNumber).orElse(null);

    };

    public AccountResponse createAccount(CreateAccountRequest newAccount){

        accountRepository.findByBvn(newAccount.getBvn())
                .ifPresent(a -> {
                    throw new DataIntegrityViolationException(
                            "An account with BVN " + newAccount.getBvn() + " already exists."
                    );
                });
Account account = new Account(newAccount.getAccountName(), newAccount.getBvn());
        try {
          Account savedAccount=  accountRepository.save(account);

            // Map entity to DTO
            return new AccountResponse(
                    savedAccount.getAccountNumber(),
                    savedAccount.getAccountName()
            );

        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Account could not be created: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
    public AccountResponse updateAccountBalance(UpdateBalanceRequest newBalanceRequest){

        BigDecimal updatedBalance;
        Account account = accountRepository.findByAccountNumber(newBalanceRequest.getAccountNumber()).orElse(null);

        if(account != null) {
            updatedBalance = newBalanceRequest.getFlag() == 'C'
                    ? account.getBalance().add(newBalanceRequest.getAmount())
                    : account.getBalance().subtract(newBalanceRequest.getAmount());
            account.setBalance(updatedBalance);

          Account savedAccount=  accountRepository.save(account);

            // Map entity to DTO
            return new AccountResponse(
                    savedAccount.getAccountNumber(),
                    savedAccount.getAccountName()
            );

        }
return null;

    }

}
