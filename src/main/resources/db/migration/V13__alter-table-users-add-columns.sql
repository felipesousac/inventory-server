ALTER TABLE users
ADD COLUMN (`account_non_expired` BIT(1) NULL,
            `account_non_locked` BIT(1) NULL,
            `credentials_non_expired` BIT(1) NULL,
            `enabled` BIT(1) NULL);