package com.bank_app.bank_app.transfer.impl;

import com.bank_app.bank_app.account.Account;
import com.bank_app.bank_app.account.AccountRepository;
import com.bank_app.bank_app.transfer.Transfer;
import com.bank_app.bank_app.transfer.TransferRepository;
import com.bank_app.bank_app.transfer.TransferService;
import com.bank_app.bank_app.transfer.TransferStatus;
import com.bank_app.bank_app.transfer.dto.requests.TransferRequest;
import com.bank_app.bank_app.transfer.dto.responses.TransferResponse;
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

    public TransferServiceImpl(TransferRepository transferRepository, AccountRepository accountRepository) {
        this.transferRepository = transferRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public TransferResponse initiateTransfer(@NonNull TransferRequest transferRequest) {
        Account sourceAccount = accountRepository.findByAccountNumber(transferRequest.getSourceAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account destinationAccount = accountRepository.findByAccountNumber(transferRequest.getDestinationAccountNumber())
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (sourceAccount.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new IllegalArgumentException("Source account has insufficient balance");
        }

        if (transferRequest.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }

        Transfer transfer = new Transfer();
        transfer.setReferenceId(generateReferenceId());
        transfer.setSourceAccountNumber(transferRequest.getSourceAccountNumber());
        transfer.setDestinationAccountNumber(transferRequest.getDestinationAccountNumber());
        transfer.setAmount(transferRequest.getAmount());
        transfer.setNarration(transferRequest.getNarration());
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
            throw new RuntimeException("Transfer failed: " + e.getMessage());
        }

        Transfer savedTransfer = transferRepository.save(transfer);
        return mapToResponse(savedTransfer);
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

    @Override
    public List<TransferResponse> getTransfersByDestinationAccount(Long accountNumber) {
        return transferRepository.findByDestinationAccountNumber(accountNumber)
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
                transfer.getAmount(),
                transfer.getNarration(),
                transfer.getStatus(),
                transfer.getCreatedAt(),
                transfer.getCompletedAt()
        );
    }
}
