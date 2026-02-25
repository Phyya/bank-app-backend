CREATE TABLE banks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bank_code VARCHAR(10) NOT NULL UNIQUE,
    bank_name VARCHAR(100) NOT NULL,
    simulated_account_name VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Seed initial banks
INSERT INTO banks (bank_code, bank_name, simulated_account_name, active) VALUES
('001', 'First Bank', 'Adamu Musa', TRUE),
('002', 'GTBank', 'Chioma Eze', TRUE),
('003', 'Zenith Bank', 'Tunde Bakare', TRUE),
('004', 'UBA', 'Fatima Yusuf', TRUE),
('005', 'Access Bank', 'Emeka Okonkwo', TRUE);
