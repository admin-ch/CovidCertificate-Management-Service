alter table vaccines_covid_19_names
    add column issuable varchar(50) not null default 'ch_only';
alter table vaccines_covid_19_names
    add column web_ui_selectable boolean not null default false;
alter table vaccines_covid_19_names
    add column api_gateway_selectable boolean not null default false;
alter table vaccines_covid_19_names
    add column api_platform_selectable boolean not null default false;

alter table vaccines_covid_19_names
    add column code2 varchar(50) not null default '';

update vaccines_covid_19_names one
set code2 = (select code from vaccines_covid_19_names two where two.id = one.id);

alter table vaccines_covid_19_names
    drop column code;

alter table vaccines_covid_19_names
    rename column code2 to code;
