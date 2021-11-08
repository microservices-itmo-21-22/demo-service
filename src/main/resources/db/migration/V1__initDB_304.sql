CREATE TABLE IF NOT EXISTS stock304
(
    id              VARCHAR(255) not null,
    name            VARCHAR(255),
    price           decimal(10, 2),
    total_count     VARCHAR(255),
    reserved_count  VARCHAR(255),
    category        VARCHAR(255),
    primary key (id)
    );

CREATE TABLE IF NOT EXISTS catalog_item304
(
    id              VARCHAR(255) not null,
    product_id      VARCHAR(255) REFERENCES stock304 (id),
    amount          INTEGER(50),
    primary key (id)
    );

CREATE TABLE IF NOT EXISTS shopping_cart304
(
    id       VARCHAR(255) not null,
    status   INTEGER(10),
    primary key (id)
    );

CREATE TABLE IF NOT EXISTS user304
(
    id              VARCHAR(255) not null,
    ip_address      INTEGER(10),
    name            VARCHAR(255),
    email           VARCHAR(255),
    phone           INTEGER(50),
    last_basket_id  VARCHAR(255) REFERENCES shopping_cart304 (id),
    primary key (id)
    );

CREATE TABLE IF NOT EXISTS order304
(
    id          VARCHAR(255) not null,
    status      INTEGER(10),
    basket_id   VARCHAR(255) REFERENCES shopping_cart304 (id),
    user_id     VARCHAR(255) REFERENCES user304 (id),
    date        DATE,
    primary key (id)
    );

CREATE TABLE IF NOT EXISTS basket_catalog_item304
(
    basket_id       VARCHAR(255) REFERENCES shopping_cart304 (id),
    catalog_item_id VARCHAR(255) REFERENCES catalog_item304 (id)
    );

CREATE TABLE IF NOT EXISTS payment304
  (
    order_id        VARCHAR(255) REFERENCES order304 (id),
    type            INTEGER(10),
    amount          INTEGER(10),
    time            TIME
);

CREATE TABLE IF NOT EXISTS delivery304
  (
    order_id        VARCHAR(255) REFERENCES order304 (id),
    address         VARCHAR(255),
    date            DATE
);