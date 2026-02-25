package com.bank_app.bank_app.transfer;

import com.bank_app.bank_app.bank.Bank;
import com.bank_app.bank_app.bank.BankRepository;
import com.bank_app.bank_app.transfer.dto.requests.TransferRequest;
import com.bank_app.bank_app.transfer.dto.responses.BankDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class ExternalBankService {

    private final BankRepository bankRepository;
    private final Random random = new Random();

    public ExternalBankService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public ExternalBankResponse sendMoney(TransferRequest request) {
        log.info("Initiating external bank transfer to bank: {}", request.getDestinationBankCode());

        // Simulate network delay (500ms - 2000ms)
        simulateNetworkDelay();

        Optional<Bank> bankOpt = bankRepository.findByBankCode(request.getDestinationBankCode());
        if (bankOpt.isEmpty() || !bankOpt.get().isActive()) {
            return new ExternalBankResponse(
                    false,
                    "INVALID_BANK",
                    "Bank code not found or inactive",
                    null
            );
        }

        // Simulate 90% success rate
        boolean isSuccess = random.nextInt(100) < 90;

        if (isSuccess) {
            String externalRef = "EXT" + System.currentTimeMillis();
            log.info("External transfer successful. Reference: {}", externalRef);
            return new ExternalBankResponse(
                    true,
                    "SUCCESS",
                    "Transfer completed successfully",
                    externalRef
            );
        } else {
            // Simulate random failures
            String[] failureReasons = {
                    "Destination account is inactive",
                    "Bank service temporarily unavailable",
                    "Invalid account number",
                    "Daily transfer limit exceeded"
            };
            String reason = failureReasons[random.nextInt(failureReasons.length)];
            log.warn("External transfer failed: {}", reason);
            return new ExternalBankResponse(
                    false,
                    "FAILED",
                    reason,
                    null
            );
        }
    }

    public NameEnquiryResponse nameEnquiry(String bankCode, Long accountNumber) {
        log.info("Name enquiry for bank: {}, account: {}", bankCode, accountNumber);

        simulateNetworkDelay();

        // Validate bank code from database
        Optional<Bank> bankOpt = bankRepository.findByBankCode(bankCode);
        if (bankOpt.isEmpty() || !bankOpt.get().isActive()) {
            return new NameEnquiryResponse(false, null, "Invalid bank code");
        }

        Bank bank = bankOpt.get();

        // Validate account number format (must be 10 digits)
        String accountStr = String.valueOf(accountNumber);
        if (accountStr.length() != 10) {
            return new NameEnquiryResponse(false, null, "Invalid account number. Must be 10 digits");
        }

        // Simulate "account not found" for accounts ending in 0
        if (accountNumber % 10 == 0) {
            return new NameEnquiryResponse(false, null, "Account not found");
        }

        // Simulate "inactive account" for accounts ending in 9
        if (accountNumber % 10 == 9) {
            return new NameEnquiryResponse(false, null, "Account is inactive");
        }

        // Return simulated account name from database
        return new NameEnquiryResponse(true, bank.getSimulatedAccountName(), "Success");
    }

    public String getBankName(String bankCode) {
        return bankRepository.findByBankCode(bankCode)
                .filter(Bank::isActive)
                .map(Bank::getBankName)
                .orElse(null);
    }

    public List<BankDTO> getAllBanks() {
        return bankRepository.findByActiveTrue()
                .stream()
                .map(bank -> new BankDTO(bank.getBankCode(), bank.getBankName()))
                .toList();
    }

    private void simulateNetworkDelay() {
        try {
            int delay = 500 + random.nextInt(1500);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExternalBankResponse {
        private boolean success;
        private String responseCode;
        private String message;
        private String externalReference;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NameEnquiryResponse {
        private boolean success;
        private String accountName;
        private String message;
    }
}
