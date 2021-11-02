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
    id uuid primary key,
    userId uuid references users not null,
    timeCreated date not null,
    status order_status not null,
    deliveryDuration integer not null,
    itemsMap uuid[] references order_items not null,
    paymentHistory payment_log
);

create type order_status as enum ('COLLECTING', 'DISCARD', 'BOOKED', 'PAID', 'SHIPPING', 'REFUND', 'COMPLETED');

create table if not exists order_items (
    id uuid primary key,
    title text not null,
    price text not null,
    amount integer not null,
    orderEntity uuid references orders
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