CREATE TABLE `user_permission` (
  `id_user` BIGINT(20) NOT NULL,
  `id_permission` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id_user`,`id_permission`),
  CONSTRAINT `fk_user_permission` FOREIGN KEY (`id_user`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_permission_permission` FOREIGN KEY (`id_permission`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
);