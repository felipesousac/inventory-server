CREATE TABLE items (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL,
    description VARCHAR(50) NOT NULL,
    categorie_id BIGINT(20) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    number_in_stock INTEGER NOT NULL,

    PRIMARY KEY(id),
    FOREIGN KEY (categorie_id) REFERENCES categories(id)
);