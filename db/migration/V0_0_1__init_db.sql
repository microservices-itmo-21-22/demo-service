-- user service

create table if not exists app_user (
    id uuid primary key,
    username varchar(64) unique not null,
    name varchar(255) not null,
    surname varchar(255) not null,
    email varchar(255) unique not null,
    password varchar(64) not null
);

-- order service
create type order_status as enum ('COLLECTING', 'DISCARD', 'BOOKED', 'PAID', 'SHIPPING', 'REFUND', 'COMPLETED');

create table if not exists orders (
    id uuid primary key,
    user_id uuid not null,
    time_created timestamp not null,
    status order_status not null,
    delivery_duration integer not null
);



create table if not exists order_items (
    id uuid primary key,
    title text not null,
    price text not null,
    amount integer not null,
    orderEntity uuid
);

-- item service

create table if not exists catalog_item (
    id uuid primary key,
    title text not null,
    description text not null,
    amount integer not null,
    price integer not null
);

-- payment service

create type financial_operation_type as enum ('WITHDRAW', 'REFUND');

create type payment_status as enum ('SUCCESS', 'FAILED');

create table if not exists payment_log_record (
    transaction_id uuid primary key,
    status payment_status not null,
    amount integer not null,
    timestamp timestamp not null
);

create table if not exists user_account_financial_log_record (
    payment_transaction_id uuid primary key,
    amount integer not null,
    user_id uuid not null,
    order_id uuid not null,
    timestamp timestamp not null,
    type financial_operation_type not null
);

create table if not exists payment_user (
    user_id uuid primary key,
    username varchar(64) unique not null,
    email varchar(255) unique not null
);

create table if not exists payment_order (
    order_id uuid primary key,
    user_id uuid not null
);

-- TODO: add user stubs here and in other services

-- delivery service

