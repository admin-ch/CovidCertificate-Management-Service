alter table vaccines_covid_19_names
    add column vaccine_order integer not null default 100;

-- Comirnaty
update vaccines_covid_19_names
set vaccine_order = 10 where id = '0af1a070-2647-4acb-a1be-4df81b47840b';

-- Spikevax (previously COVID-10 Vaccine Moderna)
update vaccines_covid_19_names
set vaccine_order = 20 where id = '2dc3feef-95d8-4e6f-930c-69300e10e6ee';

-- COVID-19 Vaccine Janssen
update vaccines_covid_19_names
set vaccine_order = 30 where id = 'ce2aad87-0d0b-4c5d-8a79-e15576020c05';

-- Vaxzevria (previously COVID-19 Vaccine AstraZeneca)
update vaccines_covid_19_names
set vaccine_order = 40 where id = '8ac65a1d-00fa-4425-8f3f-cb866c4aa053';

-- Covishield (ChAdOx1_nCoV-19)
update vaccines_covid_19_names
set vaccine_order = 50 where id = '2dc3feef-95d8-4e6f-930c-69300e10e6f0';

-- BBIBP-CorV (Vero Cells)
update vaccines_covid_19_names
set vaccine_order = 60 where id = '4109a8fa-84b2-4a3f-bf33-6c746a79a11d';

-- COVID-19 Vaccine (Vero Cell), Inactivated/ Coronavac
update vaccines_covid_19_names
set vaccine_order = 70 where id = 'fe39b8af-eccd-45de-acd7-ce7206052af7';

-- else
update vaccines_covid_19_names
set vaccine_order = 200 where web_ui_selectable = 'false';