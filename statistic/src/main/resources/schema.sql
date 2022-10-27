drop table if exists statistic cascade;

create table if not exists statistic
(
    id          bigint generated always as identity primary key,
    app         varchar(100),
    attributes  jsonb,
    created_on timestamp
);