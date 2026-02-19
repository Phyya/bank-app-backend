package com.bank_app.bank_app.transfer;

import com.bank_app.bank_app.transfer.dto.requests.TransferRequest;
import com.bank_app.bank_app.transfer.dto.responses.TransferResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<TransferResponse> initiateTransfer(@RequestBody TransferRequest transferRequest) {
        TransferResponse response = transferService.initiateTransfer(transferRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<TransferResponse> getTransferByReference(@PathVariable String referenceId) {
        TransferResponse response = transferService.getTransferByReference(referenceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/source/{accountNumber}")
    public ResponseEntity<List<TransferResponse>> getTransfersBySourceAccount(@PathVariable Long accountNumber) {
        List<TransferResponse> responses = transferService.getTransfersBySourceAccount(accountNumber);
        return ResponseEntity.ok(responses);
    }


}
