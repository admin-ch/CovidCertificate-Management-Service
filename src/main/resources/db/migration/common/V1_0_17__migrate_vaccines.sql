alter table vaccines_covid_19_names
    add column issuable varchar(50) not null default 'ch_only';
alter table vaccines_covid_19_names
    add column web_ui_selectable boolean not null default false;
alter table vaccines_covid_19_names
    add column api_gateway_selectable boolean not null default false;
alter table vaccines_covid_19_names
    add column api_platform_selectable boolean not null default false;

alter table vaccines_covid_19_names
    drop constraint vaccines_covid_19_names_code_key;
