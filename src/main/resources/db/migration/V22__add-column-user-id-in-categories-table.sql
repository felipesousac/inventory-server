ALTER TABLE categories
ADD COLUMN user_id BIGINT(20) AFTER description,
ADD FOREIGN KEY (user_id) REFERENCES users(id);