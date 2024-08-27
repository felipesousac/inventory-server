ALTER TABLE `user_permission`
ADD CONSTRAINT `fk_user_permission_permission`
  FOREIGN KEY (`id_permission`)
  REFERENCES `permission` (`id`)
  ON DELETE RESTRICT
  ON UPDATE RESTRICT;