package com.bank_app.bank_app.transfer.impl;

import com.bank_app.bank_app.account.Account;
import com.bank_app.bank_app.account.AccountRepository;
import com.bank_app.bank_app.transfer.ExternalBankService;
import com.bank_app.bank_app.transfer.Transfer;
import com.bank_app.bank_app.transfer.TransferRepository;
import com.bank_app.bank_app.transfer.TransferService;
import com.bank_app.bank_app.transfer.TransferStatus;
import com.bank_app.bank_app.transfer.TransferType;
import com.bank_app.bank_app.transfer.dto.requests.NameEnquiryRequest;
import com.bank_app.bank_app.transfer.dto.requests.TransferRequest;
import com.bank_app.bank_app.transfer.dto.responses.NameEnquiryResponse;
import com.bank_app.bank_app.transfer.dto.responses.TransferResponse;
import com.bank_app.bank_app.exception.ResourceNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransferServiceImpl implements TransferService {

    private final TransferRepository transferRepository;
    private final AccountRepository accountRepository;
    private final ExternalBankService externalBankService;

    public TransferServiceImpl(TransferRepository transferRepository,
                               AccountRepository accountRepository,
                               ExternalBankService externalBankService) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
        this.externalBankService = externalBankService;
    }

    @Override
    @Transactional
    public TransferResponse initiateTransfer(@NonNull TransferRequest transferRequest) {
        // Determine transfer type
        boolean isInterBank = transferRequest.getDestinationBankCode() != null
                && !transferRequest.getDestinationBankCode().isBlank();

        if (isInterBank) {
            return processInterBankTransfer(transferRequest);
        } else {
            return processIntraBankTransfer(transferRequest);
        }
    }

    private TransferResponse processIntraBankTransfer(TransferRequest transferRequest) {
        Account sourceAccount = accountRepository.findByAccountNumber(transferRequest.getSourceAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account destinationAccount = accountRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        validateTransferAmount(sourceAccount, transferRequest.getAmount());

        Transfer transfer = new Transfer();
        transfer.setReferenceId(generateReferenceId());
        transfer.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
        transfer.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
        transfer.setAmount(transferRequest.getAmount());
        transfer.setNarration(transferRequest.getNarration());
        transfer.setTransferType(TransferType.INTRA_BANK);
        transfer.setStatus(TransferStatus.PENDING);

        try {
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(transferRequest.getAmount()));
            destinationAccount.setBalance(destinationAccount.getBalance().add(transferRequest.getAmount()));

            accountRepository.save(sourceAccount);
            accountRepository.save(destinationAccount);

            transfer.setStatus(TransferStatus.COMPLETED);
            transfer.setCompletedAt(LocalDateTime.now());
        } catch (Exception e) {
            transfer.setStatus(TransferStatus.FAILED);
            throw new RuntimeException("Intra-bank transfer failed: " + e.getMessage());
        }

        Transfer savedTransfer = transferRepository.save(transfer);
        return mapToResponse(savedTransfer);
    }

    private TransferResponse processInterBankTransfer(TransferRequest transferRequest) {
        Account sourceAccount = accountRepository.findByAccountNumber(transferRequest.getSourceAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        String bankCode = transferRequest.getDestinationBankCode();
        String bankName = externalBankService.getBankName(bankCode);
        if (bankName == null) {
            throw new IllegalArgumentException("Invalid bank code: " + bankCode);
        }

        if (transferRequest.getDestinationAccountName() == null || transferRequest.getDestinationAccountName().isBlank()) {
            throw new IllegalArgumentException("Destination account name is required for inter-bank transfers");
        }

        validateTransferAmount(sourceAccount, transferRequest.getAmount());

        Transfer transfer = new Transfer();
        transfer.setReferenceId(generateReferenceId());
        transfer.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
        transfer.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
        transfer.setDestinationBankCode(bankCode);
        transfer.setDestinationBankName(bankName);
        transfer.setDestinationAccountName(transferRequest.getDestinationAccountName());
        transfer.setAmount(transferRequest.getAmount());
        transfer.setNarration(transferRequest.getNarration());
        transfer.setTransferType(TransferType.INTER_BANK);
        transfer.setStatus(TransferStatus.PENDING);

        try {
            // Debit source account
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(transferRequest.getAmount()));
            accountRepository.save(sourceAccount);

            // Call external bank API
            ExternalBankService.ExternalBankResponse response = externalBankService.sendMoney(transferRequest);

            if (response.isSuccess()) {
                transfer.setStatus(TransferStatus.COMPLETED);
                transfer.setCompletedAt(LocalDateTime.now());
            } else {
                // Reverse the debit if external transfer fails
                sourceAccount.setBalance(sourceAccount.getBalance().add(transferRequest.getAmount()));
                accountRepository.save(sourceAccount);
                transfer.setStatus(TransferStatus.FAILED);
                transfer.setNarration(transfer.getNarration() + " | Failed: " + response.getMessage());
            }
        } catch (Exception e) {
            transfer.setStatus(TransferStatus.FAILED);
            throw new RuntimeException("Inter-bank transfer failed: " + e.getMessage());
        }

        Transfer savedTransfer = transferRepository.save(transfer);
        return mapToResponse(savedTransfer);
    }

    private void validateTransferAmount(Account sourceAccount, java.math.BigDecimal amount) {
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    @Override
    public TransferResponse getTransferByReference(String referenceId) {
        Transfer transfer = transferRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found"));
        return mapToResponse(transfer);
    }

    @Override
    public List<TransferResponse> getTransfersBySourceAccount(Long accountNumber) {
        return transferRepository.findBySourceAccountNumber(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    private String generateReferenceId() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    private TransferResponse mapToResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getReferenceId(),
                transfer.getSourceAccountNumber(),
                transfer.getDestinationAccountNumber(),
                transfer.getDestinationBankCode(),
                transfer.getDestinationBankName(),
                transfer.getDestinationAccountName(),
                transfer.getTransferType(),
                transfer.getAmount(),
                transfer.getNarration(),
                transfer.getStatus(),
                transfer.getCreatedAt(),
                transfer.getCompletedAt()
        );
    }

    @Override
    @Transactional
    public NameEnquiryResponse nameEnquiry(NameEnquiryRequest request) {
        if (request.getAccountNumber() == null) {
            throw new IllegalArgumentException("Account number is required");
        }

        boolean isInternal = request.getBankCode() == null || request.getBankCode().isBlank();

        if (isInternal) {
            // Internal lookup - search our database
            Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + request.getAccountNumber()));

            return new NameEnquiryResponse(
                    account.getAccountNumber(),
                    account.getAccountName(),
                    null,
                    "Internal",
                    true
            );
        } else {
            // External lookup - call external bank API
            String bankName = externalBankService.getBankName(request.getBankCode());
            if (bankName == null) {
                throw new IllegalArgumentException("Invalid bank code: " + request.getBankCode());
            }

            ExternalBankService.NameEnquiryResponse externalResponse =
                    externalBankService.nameEnquiry(request.getBankCode(), request.getAccountNumber());

            if (!externalResponse.isSuccess()) {
                throw new ResourceNotFoundException("Account not found at " + bankName);
            }

            return new NameEnquiryResponse(
                    request.getAccountNumber(),
                    externalResponse.getAccountName(),
                    request.getBankCode(),
                    bankName,
                    false
            );
        }
    }
}
