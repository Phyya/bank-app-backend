package com.bank_app.bank_app.transfer;

import com.bank_app.bank_app.transfer.dto.requests.TransferRequest;
import com.bank_app.bank_app.transfer.dto.responses.TransferResponse;

import java.util.List;

public interface TransferService {

    TransferResponse initiateTransfer(TransferRequest transferRequest);

    TransferResponse getTransferByReference(String referenceId);

    List<TransferResponse> getTransfersBySourceAccount(Long accountNumber);

    List<TransferResponse> getTransfersByDestinationAccount(Long accountNumber);
}
