create table vaccines_covid_19_auth_holders
(
    id uuid not null primary key,
    code varchar(50) not null unique,
    display varchar(100) not null,
    active boolean not null,
    modified_at timestamp not null default now()
);

create table vaccines_covid_19_names
(
    id uuid not null primary key,
    code varchar(50) not null unique,
    display varchar(100) not null,
    auth_holders varchar(50) not null REFERENCES vaccines_covid_19_auth_holders (code),
    active boolean not null,
    ch_issuable boolean not null default false,
    modified_at timestamp not null default now()
);

create table covid_19_lab_test_manufacturer_and_name
(
    id uuid not null primary key,
    code varchar(10) not null unique,
    display varchar(200) not null,
    active boolean not null,
    ch_issuable boolean not null default false,
    modified_at timestamp not null default now()
);