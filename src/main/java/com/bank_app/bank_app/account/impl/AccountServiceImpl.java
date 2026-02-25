package com.bank_app.bank_app.account.impl;


import com.bank_app.bank_app.account.Account;
import com.bank_app.bank_app.account.AccountRepository;
import com.bank_app.bank_app.account.dto.requests.CreateAccountRequest;
import com.bank_app.bank_app.account.AccountService;
import com.bank_app.bank_app.account.dto.requests.UpdateBalanceRequest;
import com.bank_app.bank_app.account.dto.responses.AccountResponse;
import com.bank_app.bank_app.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<AccountResponse> getAllAccounts (){


        return accountRepository.findAll().stream()
                .map(account -> new AccountResponse(
                        account.getAccountNumber(),
                        account.getAccountName(),
                        account.getBalance()))
                .toList();

    }

    @Transactional
    public AccountResponse getAccountByAccountNo (Long accountNumber){
Account accountFound = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));
        return new AccountResponse(
                accountFound.getAccountNumber(),
                accountFound.getAccountName(),
                accountFound.getBalance()
        );
    }
@Transactional
public String nameEnquiry(Long accountNumber) {
    Account accountFound = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));
    return accountFound.getAccountName();



    }
    public AccountResponse createAccount(CreateAccountRequest newAccount) {

        if (newAccount == null) {
            throw new IllegalArgumentException("CreateAccountRequest cannot be null");
        }

        String accountName = newAccount.getAccountName();
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new IllegalArgumentException("Account name is required");
        }

        if (accountName.length() < 3 || accountName.length() > 100) {
            throw new IllegalArgumentException("Account name must be between 3 and 100 characters");
        }

        if (accountName.trim().length() < 3) {
            throw new IllegalArgumentException("Account name cannot be blank or whitespace");
        }

        String bvn = newAccount.getBvn();
        if (bvn == null || bvn.trim().isEmpty()) {
            throw new IllegalArgumentException("BVN is required");
        }

        bvn = bvn.trim();

        if (!bvn.matches("\\d{11}")) {
            throw new IllegalArgumentException("BVN must be exactly 11 digits");
        }

        String finalBvn = bvn;
        accountRepository.findByBvn(bvn)
                .ifPresent(a -> {
                    throw new DataIntegrityViolationException(
                            "An account with BVN " + finalBvn + " already exists."
                    );
                });
        accountRepository.findByAccountName(newAccount.getAccountName())
                .ifPresent(a -> {
                    throw new DataIntegrityViolationException(
                            "An account with exact name" + " already exists."
                    );
                });

        Account account = new Account(accountName.trim(), bvn);
        account.setPassword(passwordEncoder.encode(newAccount.getPassword()));

        try {
            Account savedAccount = accountRepository.save(account);

            if (savedAccount == null) {
                throw new RuntimeException("Failed to persist account");
            }

            return new AccountResponse(
                    savedAccount.getAccountNumber(),
                    savedAccount.getAccountName()
            );

        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException(
                    "Account could not be created due to data integrity violation: " + e.getMessage()
            );
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error occurred while creating account", e);
        }
    }

    @Transactional
    public AccountResponse updateAccountBalance(Long accountNumber, UpdateBalanceRequest newBalanceRequest){

        BigDecimal updatedBalance;
        char flag = newBalanceRequest.getFlag();
        BigDecimal amount = newBalanceRequest.getAmount();


        if (accountNumber == null) {
            throw new IllegalArgumentException("Account number cannot be null");
        }
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
        char normalizedFlag = Character.toLowerCase(flag);

        if (normalizedFlag == 'c') {
            updatedBalance = account.getBalance().add(amount);
        } else if (normalizedFlag == 'd') {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new IllegalArgumentException("Insufficient balance.");
            }
            updatedBalance = account.getBalance().subtract(amount);
        } else {
            throw new IllegalArgumentException("Invalid flag. Use 'C' for credit or 'D' for debit.");
        }

        account.setBalance(updatedBalance);

          Account savedAccount=  accountRepository.save(account);

            // Map entity to DTO
            return new AccountResponse(
                    savedAccount.getAccountNumber(),
                    savedAccount.getAccountName(),
                    savedAccount.getBalance()
            );



    }

}
