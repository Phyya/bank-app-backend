CREATE TABLE accounts (
                          account_number BIGINT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                          account_name VARCHAR(255) NOT NULL,
                          bvn INT NOT NULL,
                          balance DECIMAL(19,2) NOT NULL DEFAULT 0
) AUTO_INCREMENT=200000001;
