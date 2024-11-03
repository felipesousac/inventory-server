ALTER TABLE categories
ADD FOREIGN KEY (user_id) REFERENCES users(id);