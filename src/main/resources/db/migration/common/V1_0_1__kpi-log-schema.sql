create table kpi
(
    id uuid not null primary key,
    timestamp timestamp not null default now(),
    type varchar(64) not null,
    value varchar(64) not null
);