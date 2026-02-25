-- Seed banks data (only inserts if not exists)
INSERT INTO banks (bank_code, bank_name, simulated_account_name, active)
SELECT '001', 'First Bank', 'Adamu Musa', TRUE
WHERE NOT EXISTS (SELECT 1 FROM banks WHERE bank_code = '001');

INSERT INTO banks (bank_code, bank_name, simulated_account_name, active)
SELECT '002', 'GTBank', 'Chioma Eze', TRUE
WHERE NOT EXISTS (SELECT 1 FROM banks WHERE bank_code = '002');

INSERT INTO banks (bank_code, bank_name, simulated_account_name, active)
SELECT '003', 'Zenith Bank', 'Tunde Bakare', TRUE
WHERE NOT EXISTS (SELECT 1 FROM banks WHERE bank_code = '003');

INSERT INTO banks (bank_code, bank_name, simulated_account_name, active)
SELECT '004', 'UBA', 'Fatima Yusuf', TRUE
WHERE NOT EXISTS (SELECT 1 FROM banks WHERE bank_code = '004');

INSERT INTO banks (bank_code, bank_name, simulated_account_name, active)
SELECT '005', 'Access Bank', 'Emeka Okonkwo', TRUE
WHERE NOT EXISTS (SELECT 1 FROM banks WHERE bank_code = '005');
