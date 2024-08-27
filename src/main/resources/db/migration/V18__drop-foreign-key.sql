ALTER TABLE `user_permission`
DROP FOREIGN KEY `fk_user_permission_permission`;
ALTER TABLE `user_permission`
ADD INDEX `fk_user_permission_permission_idx` (`id_permission` ASC) VISIBLE,
DROP INDEX `fk_user_permission_permission` ;
