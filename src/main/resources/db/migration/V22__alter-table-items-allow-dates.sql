ALTER TABLE `items`
ADD COLUMN `created_at` DATETIME(2) AFTER `number_in_stock`;
ALTER TABLE `items`
ADD COLUMN `offset` VARCHAR(45) NOT NULL AFTER `created_at`;
