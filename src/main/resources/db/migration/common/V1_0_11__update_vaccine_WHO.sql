-- INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
-- VALUES ('279ad11e-f040-442d-8e97-1262423e3e0c', 'ORG-100002552', 'Janssen-Cilag AG', true,
--         '2021-09-22 15:00:00.000000');

-- INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
-- VALUES ('fe39b8af-eccd-45de-acd7-ce7206052af8', 'EU/1/20/1525', 'COVID-19 Vaccine Janssen',
--         '1ee3559a-ed64-4062-a10e-baeded624ae8', '279ad11e-f040-442d-8e97-1262423e3e0c', true, true,
--         '2021-09-22 15:00:00.000000');

INSERT INTO sct_vaccines_covid_19 (id, code, display, active, modified_at)
VALUES ('1ee3559a-ed64-4062-a10e-baeded624ae9', 'protein subunit', 'protein subunit', false,
        '2021-09-22 15:00:00.000000');

INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('fe39b8af-eccd-45de-acd7-ce7206052af8', 'EpiVacCorona', 'EpiVacCorona',
        '1ee3559a-ed64-4062-a10e-baeded624ae9', 'a701e873-66b8-426b-8c7c-2f99615f4781', true, false,
        '2021-09-22 15:00:00.000000');

update vaccines_covid_19_names
set ch_issuable = true,
    code        = 'BBIBP-CorV',
    display     = 'BBIBP-CorV (Vero Cells)',
    modified_at = '2021-09-22 15:00:00.000000'
where id = '4109a8fa-84b2-4a3f-bf33-6c746a79a11d';

INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('fe39b8af-eccd-45de-acd7-ce7206052af9', 'Inactivated-SARS-CoV-2-Vero-Cell',
        'SARS-CoV-2 Vaccine (Vero Cell), Inactivated(lnCoV)',
        '1ee3559a-ed64-4062-a10e-baeded624ae8', '0ccef01d-b2af-4fb8-9cf8-2b9cdc265562', true, true,
        '2021-09-22 15:00:00.000000');

update vaccines_covid_19_names
set ch_issuable = true,
    code        = 'CoronaVac',
    display     = 'COVID-19 Vaccine (Vero Cell), Inactivated/ Coronavac',
    modified_at = '2021-09-22 15:00:00.000000'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af7';

INSERT INTO sct_vaccines_covid_19 (id, code, display, active, modified_at)
VALUES ('1ee3559a-ed64-4062-a10e-baeded624af0', 'inactivated pathogen', 'inactivated pathogen', false,
        '2021-09-22 15:00:00.000000');

INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('fe39b8af-eccd-45de-acd7-ce7206052b00', 'Covaxin', 'Covaxin (also known as BBV152 A, B, C)',
        '1ee3559a-ed64-4062-a10e-baeded624af0', '8f910905-1c14-4f92-befd-6174c1aba905', true, false,
        '2021-09-22 15:00:00.000000');

-- INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
-- VALUES ('279ad11e-f040-442d-8e97-1262423e3e0d', 'ORG-100033151', 'Moderna Switzerland GmbH', true,
--         '2021-09-22 15:00:00.000000');

-- INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
-- VALUES ('2dc3feef-95d8-4e6f-930c-69300e10e6ef', 'EU/1/20/1507', 'Spikevax (previously COVID-19 Vaccine Moderna)',
--         '0936bf83-bfe5-4897-8b10-adf2664608e6', '279ad11e-f040-442d-8e97-1262423e3e0d', true, true,
--         '2021-09-22 15:00:00.000000');

INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e10e6f0', 'Covishield', 'Covishield (ChAdOx1_nCoV-19)',
        '1ee3559a-ed64-4062-a10e-baeded624ae8', '279ad11e-f040-442d-8e97-1262423e3e0b', true, true,
        '2021-09-22 15:00:00.000000');

update vaccines_covid_19_names
set ch_issuable = true,
    modified_at = '2021-09-22 15:00:00.000000'
where id = '8ac65a1d-00fa-4425-8f3f-cb866c4aa053';
