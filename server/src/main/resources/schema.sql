drop table if exists compilations_events cascade;
drop table if exists requests cascade;
drop table if exists events cascade;
drop table if exists compilations cascade;
drop table if exists categories cascade;
drop table if exists users cascade;

create table if not exists users
(
    id          bigint generated always as identity primary key,
    name        varchar(255) not null ,
    email       varchar(100) not null
);

create table if not exists categories
(
    id          bigint generated always as identity primary key,
    name        varchar(100) not null unique
);

create table if not exists compilations
(
    id          bigint generated always as identity primary key,
    pinned      boolean,
    title       varchar(256)
);

create table if not exists events
(
    id          bigint generated always as identity primary key,
    annotation  varchar,
    category_id bigint,
    created_on  timestamp,
    description varchar,
    event_date  timestamp,
    initiator   bigint,
    location_lat    float,
    location_lon    float,
    paid        boolean,
    participant_limit   int,
    published_on    timestamp,
    request_moderation      boolean,
    state       varchar(100),
    title       varchar,
    constraint fk_event_to_category foreign key (category_id) references categories (id) on delete cascade,
    constraint fk_event_to_user foreign key (initiator) references users (id) on delete cascade
);

create table if not exists requests
(
    id          bigint generated always as identity primary key,
    created     timestamp,
    event_id    bigint,
    requester   bigint,
    status      varchar(100),
    constraint fk_request_to_event foreign key (event_id) references events (id) on delete cascade,
    constraint fk_request_to_user foreign key (requester) references users (id) on delete cascade
);

create table if not exists compilations_events
(
    compilation_id bigint,
    event_id    bigint,
    constraint fk_compilation_events_to_compilation foreign key (compilation_id) references compilations (id) on delete cascade,
    constraint fk_compilation_events_to_event foreign key (event_id) references events (id) on delete cascade
);