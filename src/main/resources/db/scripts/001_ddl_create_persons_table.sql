create table persons (
    id serial primary key,
    login    varchar(2000) unique not null,
    password varchar(2000)        not null
);