ALTER TABLE `user_permission`
DROP FOREIGN KEY `fk_user_permission`,
DROP FOREIGN KEY `fk_user_permission_permission`;
ALTER TABLE `user_permission`
ADD CONSTRAINT `fk_user_permission`
  FOREIGN KEY (`id_user`)
  REFERENCES `users` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE,
ADD CONSTRAINT `fk_user_permission_permission`
  FOREIGN KEY (`id_permission`)
  REFERENCES `permission` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;