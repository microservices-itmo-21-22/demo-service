CREATE TABLE IF NOT EXISTS stosk
(
    id              SERIAL PRIMARY KEY,
    name            VARCHAR(255),
    price           decimal(10, 2),
    total_count     VARCHAR(255),
    reserved_count  VARCHAR(255),
    category        VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS catalog_item
(
    id              SERIAL PRIMARY KEY,
    product_id      INTEGER REFERENCES stock (id),
    amount          INTEGER(50)
);

CREATE TABLE IF NOT EXISTS shopping_cart
(
    id       SERIAL PRIMARY KEY,
    status   INTEGER(10)
);

CREATE TABLE IF NOT EXISTS user
(
    id              SERIAL PRIMARY KEY,
    ip_address      INTEGER(10),
    name            VARCHAR(255),
    email           VARCHAR(255),
    phone           INTEGER(50),
    last_basket_id  INTEGER REFERENCES shopping_cart (id)
);

CREATE TABLE IF NOT EXISTS order
(
    id          SERIAL PRIMARY KEY,
    status      INTEGER(10),
    basket_id   INTEGER REFERENCES shopping_cart (id),
    user_id     INTEGER REFERENCES user (id),
    date        DATE
);

CREATE TABLE IF NOT EXISTS basket_catalog_item
(
    basket_id       INTEGER REFERENCES shopping_cart (id),
    catalog_item_id INTEGER REFERENCES catalog_item (id)
);

(CREATE TABLE IF NOT EXISTS payment
    order_id        INTEGER REFERENCES order (id),
    type            INTEGER(10),
    amount          INTEGER(10),
    time            TIME
);

(CREATE TABLE IF NOT EXISTS delivery
    order_id        INTEGER REFERENCES order (id),
    address         VARCHAR(255),
    date            DATE
);
