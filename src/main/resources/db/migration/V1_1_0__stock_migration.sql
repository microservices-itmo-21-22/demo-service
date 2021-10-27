CREATE TABLE IF NOT EXISTS stosk
(
    id       SERIAL PRIMARY KEY,
    name     VARCHAR(255),
    price   decimal(10, 2),
    totalCount   VARCHAR(255),
    reservedCount   VARCHAR(255),
    category  VARCHAR(255)
    );