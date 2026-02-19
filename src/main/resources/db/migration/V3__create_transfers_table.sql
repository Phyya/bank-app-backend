CREATE TABLE transfers (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           reference_id VARCHAR(255) NOT NULL UNIQUE,
                           source_account_number BIGINT NOT NULL,
                           destination_account_number BIGINT NOT NULL,
                           amount DECIMAL(19, 2) NOT NULL,
                           narration VARCHAR(255),
                           status VARCHAR(50) NOT NULL,
                           created_at TIMESTAMP NOT NULL,
                           completed_at TIMESTAMP
);