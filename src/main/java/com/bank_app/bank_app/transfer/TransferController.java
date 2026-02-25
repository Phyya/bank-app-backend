package com.bank_app.bank_app.transfer;

import com.bank_app.bank_app.config.APIResponse;
import com.bank_app.bank_app.transfer.dto.requests.NameEnquiryRequest;
import com.bank_app.bank_app.transfer.dto.requests.TransferRequest;
import com.bank_app.bank_app.transfer.dto.responses.BankDTO;
import com.bank_app.bank_app.transfer.dto.responses.NameEnquiryResponse;
import com.bank_app.bank_app.transfer.dto.responses.TransferResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;
    private final ExternalBankService externalBankService;

    public TransferController(TransferService transferService, ExternalBankService externalBankService) {
        this.transferService = transferService;
        this.externalBankService = externalBankService;
    }

    @PostMapping
    public ResponseEntity<APIResponse<TransferResponse>> initiateTransfer(@RequestBody TransferRequest transferRequest) {
        TransferResponse response = transferService.initiateTransfer(transferRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(APIResponse.success("Transfer initiated successfully", response));
    }

    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<APIResponse<TransferResponse>> getTransferByReference(@PathVariable String referenceId) {
        TransferResponse response = transferService.getTransferByReference(referenceId);
        return ResponseEntity.ok(APIResponse.success("Transfer retrieved successfully", response));
    }

    @GetMapping("/history/{accountNumber}")
    public ResponseEntity<APIResponse<List<TransferResponse>>> getTransfersBySourceAccount(@PathVariable Long accountNumber) {
        List<TransferResponse> responses = transferService.getTransfersBySourceAccount(accountNumber);
        return ResponseEntity.ok(APIResponse.success("Transfer history retrieved successfully", responses));
    }

    @PostMapping("/name-enquiry")
    public ResponseEntity<APIResponse<NameEnquiryResponse>> nameEnquiry(@RequestBody NameEnquiryRequest request) {
        NameEnquiryResponse response = transferService.nameEnquiry(request);
        return ResponseEntity.ok(APIResponse.success("Name enquiry successful", response));
    }
    @GetMapping("/banks")
    public ResponseEntity<APIResponse<List<BankDTO>>> getAllBanks() {
        List<BankDTO> response = externalBankService.getAllBanks();
        return ResponseEntity.ok(APIResponse.success("Banks retrieved successfully", response));
    }
}
