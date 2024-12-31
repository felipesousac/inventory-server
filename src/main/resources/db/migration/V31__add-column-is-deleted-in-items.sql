ALTER TABLE `items`
ADD COLUMN `is_deleted` TINYINT(1) NOT NULL AFTER `offset`;