ALTER TABLE `categories`
ADD COLUMN `created_at` DATETIME(2) AFTER `user_id`,
ADD COLUMN `offset` VARCHAR(45) NOT NULL AFTER `created_at`;
