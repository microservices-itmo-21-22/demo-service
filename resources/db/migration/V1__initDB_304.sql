CREATE TABLE IF NOT EXISTS stock
(
    id              INT8 not null,
    name            VARCHAR(255),
    price           decimal(10, 2),
    total_count     VARCHAR(255),
    reserved_count  VARCHAR(255),
    category        VARCHAR(255),
    primary key (id)
    );
/*
CREATE TABLE IF NOT EXISTS catalog_item
(
    id              INT8 not null,
    product_id      INTEGER REFERENCES stock (id),
    amount          INTEGER(50),
    primary key (id)
    );

CREATE TABLE IF NOT EXISTS shopping_cart
(
    id       INT8 not null,
    status   INTEGER(10),
    primary key (id)
    );

CREATE TABLE IF NOT EXISTS user
(
    id              INT8 not null,
    ip_address      INTEGER(10),
    name            VARCHAR(255),
    email           VARCHAR(255),
    phone           INTEGER(50),
    last_basket_id  INTEGER REFERENCES shopping_cart (id),
    primary key (id)
    );

CREATE TABLE IF NOT EXISTS order
(
    id          INT8 not null,
    status      INTEGER(10),
    basket_id   INTEGER REFERENCES shopping_cart (id),
    user_id     INTEGER REFERENCES user (id),
    date        DATE,
    primary key (id)
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
);*/