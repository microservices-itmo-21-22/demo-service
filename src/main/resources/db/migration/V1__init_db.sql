-- user service

create table if not exists users (
    id uuid primary key,
    username varchar(64) unique not null,
    name varchar(255) not null,
    surname varchar(255) not null,
    email varchar(255) unique not null,
    password varchar(64) not null
);

-- order service

create table if not exists orders (
    id uuid primary key
);

-- item service

-- payment service

create type financial_operation_type as enum ('WITHDRAW', 'REFUND');

create type payment_status as enum ('SUCCESS', 'FAILED');

create table if not exists payment_log (
    transaction_id uuid primary key,
    status payment_status not null,
    amount integer not null,
    timestamp timestamp not null
);

create table if not exists user_account_financial_log (
    payment_transaction_id uuid primary key,
    amount integer not null,
    user_id uuid references users not null,
    order_id uuid references orders not null,
    timestamp timestamp not null,
    type financial_operation_type not null
);

-- delivery service