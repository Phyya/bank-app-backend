package com.bank_app.bank_app.transfer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {

    Optional<Transfer> findByReferenceId(String referenceId);

    List<Transfer> findBySourceAccountNumber(Long sourceAccountNumber);

    List<Transfer> findByDestinationAccountNumber(Long destinationAccountNumber);

    List<Transfer> findByStatus(TransferStatus status);
}
