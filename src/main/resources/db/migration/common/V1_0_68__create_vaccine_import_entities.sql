create table value_set_update_log
(
    id uuid not null primary key,
    entity_type varchar(50) not null,
    code varchar(50) not null,
    update_action varchar(50) not null,
    updated_at timestamp not null default now()
);

create table display_name_modification
(
    id uuid not null primary key,
    code varchar(50) not null,
    display varchar(100) not null,
    entity_type varchar(50) not null
);

create table vaccine_import_control
(
    import_version varchar(50) not null primary key,
    import_date date not null default now(),
    done boolean not null default false
);

alter table vaccines_covid_19_names add column created_at timestamp default now();
alter table vaccines_covid_19_names drop column ch_issuable;

alter table vaccines_covid_19_auth_holders add column created_at timestamp default now();

alter table sct_vaccines_covid_19 add column created_at timestamp default now();

