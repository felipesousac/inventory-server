ALTER TABLE items
ADD COLUMN user_id BIGINT(20) AFTER categorie_id,
ADD FOREIGN KEY (user_id) REFERENCES users(id);