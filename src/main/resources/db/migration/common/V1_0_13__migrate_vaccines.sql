alter table vaccines_covid_19_names
    add column swiss_medic boolean not null default true;
alter table vaccines_covid_19_names
    add column emea boolean not null default true;
alter table vaccines_covid_19_names
    add column who_eul boolean not null default true;
