ALTER TABLE `categories`
ADD COLUMN `is_deleted` TINYINT(1) NOT NULL AFTER `offset`;