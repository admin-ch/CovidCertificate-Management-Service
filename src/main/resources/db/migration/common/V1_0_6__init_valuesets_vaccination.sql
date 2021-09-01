INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('dd5d6ced-0156-4f0c-a49a-db9b71906032', 'ORG-100001699', 'AstraZeneca AB', true, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('51240395-839e-40c2-8d03-09964a3f9011', 'ORG-100030215', 'Biontech Manufacturing GmbH', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('bdda14a3-db94-41f6-8c20-963112713a76', 'ORG-100001417', 'Janssen-Cilag International', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('c325995b-b493-40cc-934c-1404ed2c8353', 'ORG-100031184', 'Moderna Biotech Spain S.L.', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('c48dd52c-b6e4-4db5-af06-9c13f64555e5', 'ORG-100006270', 'Curevac AG', true, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('14f4b340-59c2-483f-b6c3-74199f446ee2', 'ORG-100013793', 'CanSino Biologics', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('0ccef01d-b2af-4fb8-9cf8-2b9cdc265562', 'ORG-100020693',
        'China Sinopharm International Corp. - Beijing location', true, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('f1876bf8-b8fd-4090-8a3f-17d12347d695', 'ORG-100010771',
        'Sinopharm Weiqida Europe Pharmaceutical s.r.o. - Prague location', true, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('ede8e33b-190d-48a2-b2d9-525bd3986794', 'ORG-100024420',
        'Sinopharm Zhijun (Shenzhen) Pharmaceutical Co. Ltd. - Shenzhen location', true, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('ed9c2a60-4b8e-4b1c-9370-d4df32786086', 'ORG-100032020', 'Novavax CZ AS', true, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('4a05dd07-f1ef-4d46-b771-5a93ab17c96e', 'Gamaleya-Research-Institute', 'Gamaleya Research Institute', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('a701e873-66b8-426b-8c7c-2f99615f4781', 'Vector-Institute', 'Vector Institute', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('fefacc78-58f1-4d80-a97f-6884cb99da06', 'Sinovac-Biotech', 'Sinovac Biotech', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('8f910905-1c14-4f92-befd-6174c1aba905', 'Bharat-Biotech', 'Bharat Biotech', true, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('279ad11e-f040-442d-8e97-1262423e3e0b', 'ORG-100001981', 'Serum Institute Of India Private Limited', true,
        '2021-09-01 15:59:23.700729');

INSERT INTO sct_vaccines_covid_19 (id, code, display, active, modified_at)
VALUES ('0936bf83-bfe5-4897-8b10-adf2664608e6', '1119349007', 'SARS-CoV-2 mRNA vaccine', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO sct_vaccines_covid_19 (id, code, display, active, modified_at)
VALUES ('ac270386-6c1c-4cbf-994b-fe01370b7e53', '1119305005', 'SARS-CoV-2 antigen vaccine', true,
        '2021-09-01 15:59:23.700729');
INSERT INTO sct_vaccines_covid_19 (id, code, display, active, modified_at)
VALUES ('1ee3559a-ed64-4062-a10e-baeded624ae8', 'J07BX03', 'covid-19 vaccines', true, '2021-09-01 15:59:23.700729');

INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('0af1a070-2647-4acb-a1be-4df81b47840b', 'EU/1/20/1528', 'Comirnaty', '0936bf83-bfe5-4897-8b10-adf2664608e6',
        '51240395-839e-40c2-8d03-09964a3f9011', true, true, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e10e6ee', 'EU/1/20/1507', 'Spikevax (previously COVID-19 Vaccine Moderna)',
        '0936bf83-bfe5-4897-8b10-adf2664608e6', 'bdda14a3-db94-41f6-8c20-963112713a76', true, true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('8ac65a1d-00fa-4425-8f3f-cb866c4aa053', 'EU/1/21/1529', 'Vaxzevria', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906032', true, false, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('ce2aad87-0d0b-4c5d-8a79-e15576020c05', 'EU/1/20/1525', 'COVID-19 Vaccine Janssen',
        '1ee3559a-ed64-4062-a10e-baeded624ae8', 'bdda14a3-db94-41f6-8c20-963112713a76', true, true,
        '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('4affd667-55fd-4780-bb6e-c86b0dcc7193', 'CVnCoV', 'CVnCoV', '0936bf83-bfe5-4897-8b10-adf2664608e6',
        'c48dd52c-b6e4-4db5-af06-9c13f64555e5', true, false, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('dfc61e57-8c62-43d4-a9c0-390e128793c9', 'Sputnik-V', 'Sputnik-V', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        '4a05dd07-f1ef-4d46-b771-5a93ab17c96e', true, false, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('bc0e71cf-bc52-45cb-b6f0-1dba39cf2a97', 'Convidecia', 'Convidecia', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        '14f4b340-59c2-483f-b6c3-74199f446ee2', true, false, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('4109a8fa-84b2-4a3f-bf33-6c746a79a11d', 'BBIBP-CorV', 'BBIBP-CorV', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        '0ccef01d-b2af-4fb8-9cf8-2b9cdc265562', true, false, '2021-09-01 15:59:23.700729');
INSERT INTO vaccines_covid_19_names (id, code, display, prophylaxis, auth_holder, active, ch_issuable, modified_at)
VALUES ('fe39b8af-eccd-45de-acd7-ce7206052af7', 'CoronaVac', 'CoronaVac', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'fefacc78-58f1-4d80-a97f-6884cb99da06', true, false, '2021-09-01 15:59:23.700729');
