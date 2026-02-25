ALTER TABLE transfers
ADD COLUMN destination_bank_code VARCHAR(10),
ADD COLUMN destination_bank_name VARCHAR(100),
ADD COLUMN destination_account_name VARCHAR(100),
ADD COLUMN transfer_type VARCHAR(20) NOT NULL DEFAULT 'INTRA_BANK';
