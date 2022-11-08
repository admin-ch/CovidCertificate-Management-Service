

-- VERSION V1_0_0__create-schema.sql
create table revocation
(
    id uuid not null primary key,
    uvci varchar(39) not null unique,
    creation_date_time timestamp not null default now()
);

-- VERSION V1_0_1__kpi-log-schema.sql
create table kpi
(
    id uuid not null primary key,
    timestamp timestamp not null default now(),
    type varchar(64) not null,
    "value" varchar(64) not null
);

-- VERSION V1_0_3__add-uvci.sql
ALTER TABLE kpi ADD COLUMN IF NOT EXISTS uvci VARCHAR(39);

-- VERSION V1_0_4__valuesets-schema.sql
create table sct_vaccines_covid_19
(
    id uuid not null primary key,
    code varchar(50) not null unique,
    display varchar(100) not null,
    active boolean not null,
    modified_at timestamp not null default now()
);

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
    prophylaxis uuid not null REFERENCES sct_vaccines_covid_19 (id),
    auth_holder uuid not null REFERENCES vaccines_covid_19_auth_holders (id),
    active boolean not null,
    ch_issuable boolean not null default false,
    modified_at timestamp not null default now()
);

create table covid_19_lab_test_manufacturer_and_name
(
    id uuid not null primary key,
    code varchar(50) not null unique,
    display varchar(200) not null,
    active boolean not null,
    ch_issuable boolean not null default false,
    modified_at timestamp not null default now()
);

-- VERSION V1_0_5__change_valueset-tests.sql
alter table covid_19_lab_test_manufacturer_and_name alter column ch_issuable set default true;

-- VERSION V1_0_6__init_valuesets_vaccination.sql
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


-- VERSION V1_0_7__init_valuesets_rapidtest.sql
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('01fe3158-94bf-4d68-a037-e9492a0eeb59','2241','NESAPOR EUROPA SL, MARESKIT',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('04c1f790-fae8-4a8d-af8e-8416fb5943f8','1420','NanoEntek, FREND COVID-19 Ag',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('051383d0-03af-42ea-a1be-e5c5bc3e3d0c','1437','Guangzhou Wondfo Biotech Co., Ltd, Wondfo 2019-nCoV Antigen Test (Lateral Flow Method)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('06116acc-11b4-4db2-b3a0-d77a9d22eed8','1242','Bionote, Inc, NowCheck COVID-19 Ag Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('087cb719-a3c3-4415-8071-a4427f6f7509','1216','Guangdong Longsee Biomedical Co., Ltd, COVID-19 Ag Rapid Test Kit (Immuno-Chromatography)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('095a5812-371f-4b83-9820-742718ffa6e0','1759','Hubei Jinjian Biology Co., Ltd, SARS-CoV-2 Antigen Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('0dbb1456-b92c-4e92-8e52-69e9893d1c53','1267','LumiQuick Diagnostics Inc, QuickProfile COVID-19 Antigen Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('0e958be8-b414-435d-916a-96e5a628bb77','2029','Merlin Biomedical (Xiamen) Co., Ltd., SARS-CoV-2 Antigen Rapid Test Cassette',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('156994d0-d2f7-4694-8f29-b9e5854ba399','1295','Zhejiang Anji Saianfu Biotech Co., Ltd, reOpenTest COVID-19 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('17e0679b-cdaa-42af-b751-8913e4f25937','1844','Hangzhou Immuno Biotech Co.,Ltd, Immunobio SARS-CoV-2 Antigen ANTERIOR NASAL Rapid Test Kit (minimal invasive)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('18b02b79-f722-447c-a87c-95466f4febef','2067','BIOTEKE CORPORATION (WUXI) CO., LTD, SARS-CoV-2 Antigen Test Kit (colloidal gold method)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2231cc47-a385-4499-a1c5-4294193efcaf','1468','ACON Laboratories, Inc, Flowflex SARS-CoV-2 Antigen rapid test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2299f513-6498-47de-9f87-1a4ccfa9124b','1263','Humasis, Humasis COVID-19 Ag Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('25996875-2781-4dd2-9adf-1be3f8f78971','1363','Hangzhou Clongene Biotech Co., Ltd, Covid-19 Antigen Rapid Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('271f9286-2596-420f-b756-b5ac79327d66','1271','Precision Biosensor, Inc, Exdia COVID-19 Ag',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2b05874a-1a4c-4a2e-a82f-23bbc8361281','1815','Anhui Deep Blue Medical Technology Co., Ltd, COVID-19 (SARS-CoV-2) Antigen Test Kit (Colloidal Gold) - Nasal Swab',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2b369f8d-f822-493b-8ba0-b953f25ae3f6','2109','Shenzhen Lvshiyuan Biotechnology Co., Ltd., Green Spring SARS-CoV-2 Antigen-Rapid test-Set',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2b670701-4ed6-4c97-8188-ca4c8b53e1de','2200','NanoRepro AG, NanoRepro SARS-CoV-2 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2bfc27a5-1175-42b5-8453-b6a12cc7568d','1906','Azure Biotech Inc, COVID-19 Antigen Rapid Test Device',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2c2f8d7e-e86d-4736-860a-ac82efd79bc5','2103','VivaChek Biotech (Hangzhou) Co., Ltd, VivaDiag Pro SARS-CoV-2 Ag Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2ca6443b-e26c-4e30-9803-7cd49494191e','1767','Healgen Scientific, Coronavirus Ag Rapid Test Cassette',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2cb90b72-cffd-4b56-a4b4-467ebc50f21d','1065','Becton Dickinson, BD Veritor™ System for Rapid Detection of SARS CoV 2',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('2d68d566-ea69-49fa-94ed-e7492574a7f2','1173','CerTest Biotec, CerTest SARS-CoV-2 Card test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('320dd47d-87a6-4a91-b2e4-08895d12ff3c','1747','Guangdong Hecin Scientific, Inc., 2019-nCoV Antigen Test Kit (colloidal gold method)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('342d0796-f684-4941-9228-16fbc7543f4f','1278','Xiamen Boson Biotech Co. Ltd, Rapid SARS-CoV-2 Antigen Test Card',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('37075b2b-3c12-457b-8f88-ce22111ded46','1884','Xiamen Wiz Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('38e2aad9-a6a8-44dd-8f6a-b0ee89b07dc2','1225','DDS DIAGNOSTIC, Test Rapid Covid-19 Antigen (tampon nazofaringian)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('3a705c6b-5b5e-4b75-b049-e8bf149e4421','2090','Wuhan UNscience Biotechnology Co., Ltd., SARS-CoV-2 Antigen Rapid Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('3acd3691-39d8-4c06-a418-009527f3f15b','308','PCL Inc, PCL COVID19 Ag Rapid FIA',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('3db44a81-2cf4-42a4-8882-1d2dfcadc6c4','1223','BIOSYNEX S.A., BIOSYNEX COVID-19 Ag BSS',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('3dd1d5e1-8109-4728-95e9-2f7b178c060f','2010','Atlas Link Technology Co., Ltd., NOVA Test® SARS-CoV-2 Antigen Rapid Test Kit (Colloidal Gold Immunochromatography)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('3e0cb6b6-ebae-4b32-b2f5-99200824205e','1736','Anhui Deep Blue Medical Technology Co., Ltd, COVID-19 (SARS-CoV-2) Antigen Test Kit(Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('40dbe405-58b4-4f2c-b07a-27401949286c','1764','JOYSBIO (Tianjin) Biotechnology Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('41d6aed7-c9cf-4f41-bd91-a4d77f64960f','1199','Oncosem Onkolojik Sistemler San. ve Tic. A.S., CAT',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('4289c25e-ebd0-4ae8-860f-9e428ca5c9b0','2031','Bio-Rad Laboratories / Zhejiang Orient Gene Biotech, Coronavirus Ag Rapid Test Cassette (Swab)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('46e292cc-2cbe-4149-b4e5-1fbae1c9a9e8','1762','Novatech, SARS CoV-2 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('47c3cb99-a1ed-4a86-b505-40c3f40c01e3','2317','Hangzhou Immuno Biotech Co.,Ltd, SARS-CoV-2 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('4bf0d619-6f7e-4b97-8b63-064e123dca37','1236','BTNX Inc, Rapid Response COVID-19 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('4e8113f1-3d45-49e2-ba39-1d06202a7cec','1466','TODA PHARMA, TODA CORONADIAG Ag',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('4ffa669d-cc23-4c67-a53c-7bd18f0dccbe','2035','BioMaxima SA, SARS-CoV-2 Ag Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('509c4631-79b5-4937-9cc4-02c4e327f91b','1232','Abbott Rapid Diagnostics, Panbio Covid-19 Ag Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('51472914-e5cf-4cff-8b35-da6c0ebab85a','1654','Asan Pharmaceutical CO., LTD, Asan Easy Test COVID-19 Ag',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('52253914-3a09-45c8-b804-18aa85a3634d','1253','GenSure Biotech Inc, GenSure COVID-19 Antigen Rapid Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('533f6b74-e548-4cf3-95d4-d3a6804ee605','1763','Xiamen AmonMed Biotechnology Co., Ltd, COVID-19 Antigen Rapid Test Kit (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('5583b0cc-038e-4ac9-a94a-9b75a97cde35','1870','Beijing Hotgen Biotech Co., Ltd, Novel Coronavirus 2019-nCoV Antigen Test (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('59834313-a7bf-4061-b203-44ddbe5c6e62','2098','Wuhan EasyDiagnosis Biomedicine Co., Ltd., COVID-19 (SARS-CoV-2) Antigen Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('5bd9e416-dd44-47cb-a3d8-1839ed439cf1','1190','möLab, mö-screen Corona Antigen Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('5beef1ee-3ce5-4817-ad7a-067a7b09c4d6','1957','Zhuhai Lituo Biotechnology Co., Ltd, COVID-19 Antigen Detection Kit (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('5c1fbf5f-e7aa-406c-a46f-32d9eda296c8','1286','BIOHIT HealthCare (Hefei) Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit (Fluorescence Immunochromatography)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('5c667b10-2992-476a-b248-f5752f2fa8c2','1180','MEDsan GmbH, MEDsan SARS-CoV-2 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('5c85c821-85ab-4211-b342-7df996154219','1296','Zhejiang Anji Saianfu Biotech Co., Ltd, AndLucky COVID-19 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('5cc6bf97-041e-4f61-a552-31a8026f931f','1465','Triplex International Biosciences Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('5fba549c-314c-4eb5-81bb-110e40efe5de','1934','Tody Laboratories Int., Coronavirus (SARS-CoV 2) Antigen - Oral Fluid',FALSE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('65fd4d9a-b8cc-4c9e-ad28-18437d6d5a3b','1266','Labnovation Technologies Inc, SARS-CoV-2 Antigen Rapid Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('663a1679-ee90-4f65-8d03-a89e0f8ea30e','1178','Shenzhen Microprofit Biotech Co., Ltd, SARS-CoV-2 Spike Protein Test Kit (Colloidal Gold Chromatographic Immunoassay)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('6ccbaf5b-6c24-4a56-a8fe-57bc4ab28a7f','1967','Shenzhen Microprofit Biotech Co., Ltd, SARS-CoV-2 Antigen Test Kit (Colloidal Gold Chromatographic Immunoassay)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('6f1c4a87-e33d-4676-b010-0707c9fdb4ec','1443','Vitrosens Biotechnology Co., Ltd, RapidFor SARS-CoV-2 Rapid Ag Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('6f89d216-266d-4814-a2a3-f8503f16e939','1574','Shenzhen Zhenrui Biotechnology Co., Ltd, Zhenrui ®COVID-19 Antigen Test Cassette',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('6fd5ddd8-3511-4583-9475-de974b5e093c','1257','Hangzhou AllTest Biotech Co., Ltd, COVID-19 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('73ddd915-3714-4db2-9f98-f712d28b4ba1','2078','ArcDia International Oy Ltd, mariPOC Respi+',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('7477742d-e6f8-4dbd-82f4-2746cf398478','1739','Eurobio Scientific, EBS SARS-CoV-2 Ag Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('777c6a7b-aa3d-4f4c-aabf-0b49ee6e6cd0','1581','CTK Biotech, Inc, OnSite COVID-19 Ag Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('77c746d4-9a7e-4195-b2fb-b61162352d98','1097','Quidel Corporation, Sofia SARS Antigen FIA',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('7a1b2b8c-ca1d-471d-986b-7827e01c4e89','1114','Sugentech, Inc, SGTi-flex COVID-19 Ag',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('7e692236-97d5-4365-ba50-43b4787f42c7','1218','Siemens Healthineers, CLINITEST Rapid Covid-19 Antigen Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('8056513b-a48e-4387-9981-63b033bdd5bb','770','Assure Tech. (Hangzhou) Co., Ltd, ECOTEST COVID-19 Antigen Rapid Test Device',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('81578365-d837-4b32-8e44-c401b0f8b38b','2107','Jiangsu Bioperfectus Technologies Co., Ltd., Novel Corona Virus (SARS-CoV-2) Ag Rapid Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('82557ef0-fcb5-4d90-afdb-683e8f0ab25f','2101','AXIOM Gesellschaft für Diagnostica und Biochemica mbH, COVID-19 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('86b8139c-5919-48b0-8032-2e0cdbc3c6e6','1201','ScheBo Biotech AG, ScheBo SARS CoV-2 Quick Antigen',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('88ce030b-365f-4ead-afc2-5f131f3bf4a4','1485','Beijing Wantai Biological Pharmacy Enterprise Co., Ltd, WANTAI SARS-CoV-2 Ag Rapid Test (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('8aecfe86-9ca6-469e-bc24-a6341a4cb170','1333','Joinstar Biomedical Technology Co., Ltd, COVID-19 Rapid Antigen Test (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('8c10f4e8-f21a-43a1-b1aa-12580fdc876a','2228','Roche (SD BIOSENSOR), SARS-CoV-2 Rapid Antigen Test Nasal',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('8c3afb97-8fd8-4b86-9198-c35d24a84959','2013','Biotical Health S.L.U., biotical SARS-CoV-2 Ag Card',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('8e53ed78-002a-41c6-b913-0302fb230559','1331','Beijing Lepu Medical Technology Co., Ltd, SARS-CoV-2 Antigen Rapid Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('8e6910d6-a8f4-477e-a80e-4850997910dc','2147','Fujirebio, ESPLINE SARS-CoV-2',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('924fecc9-fe39-4230-b15f-9510f0f1dcb9','1800','AVALUN SAS, Ksmart® SARS-COV2 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('9314f787-da26-4830-a93b-56a90e10b7af','1324','Guangzhou Decheng Biotechnology Co., LTD, V-CHEK, 2019-nCoV Ag Rapid Test Kit (Immunochromatography)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('949a94e6-0c8e-43bf-9ab3-d6cf9a86c400','344','SD BIOSENSOR Inc, STANDARD F COVID-19 Ag FIA',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('96b1ebbd-8932-44b5-b2e8-98203998f341','2247','BioGnost Ltd, CoviGnost AG Test Device 1x20',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('97224583-fcc4-4d63-ae98-5f70d85bf4fd','1501','New Gene (Hangzhou) Bioengineering Co., Ltd, COVID-19 Antigen Detection Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('9749fca2-c3ad-431a-b00c-269f4e3df019','1490','Safecare Biotech (Hangzhou) Co. Ltd, Multi-Respiratory Virus Antigen Test Kit(Swab)  (Influenza A+B/ COVID-19)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('97fae862-76fa-4e06-99d1-ac00f14104a9','1375','DIALAB GmbH, DIAQUICK COVID-19 Ag Cassette',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('9981ec27-34c7-474d-9921-e0682c3d2aa2','1855','GA Generic Assays GmbH, GA CoV-2 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('9b2ee5c3-bd2d-4aa1-8a61-cb8aa5233472','2012','Genrui Biotech Inc, SARS-CoV-2 Antigen Test Kit (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('9b72b193-542b-442c-98c5-235f8b38a44f','2350','Assure Tech. (Hangzhou) Co., Ltd., ECOTEST COVID-19 Antigen Rapid Test Device',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('9c208adf-c779-4b6d-8d03-5e0c2c008ce9','2104','Nal von minden GmbH, NADAL COVID -19 Ag +Influenza A/B Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('9f1d3e19-4c63-4365-8160-e07e3802dd8a','1268','LumiraDX, LumiraDx SARS-CoV-2 Ag Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('a0638008-4044-4190-908b-43944390a8d2','1457','Acon Biotech (Hangzhou) Co., Ltd, SARS-CoV-2 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('a3b669f0-f8ed-4b73-a7b9-806f90f8890a','1769','Shenzhen Watmind Medical Co., Ltd, SARS-CoV-2 Ag Diagnostic Test Kit (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('a4efcd87-89db-4df8-a8c0-79f7a4967654','1494','BIOSYNEX S.A., BIOSYNEX COVID-19 Ag+ BSS',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('a540804f-3e15-4411-99c9-6cec4bcbd634','1456','Xiamen Wiz Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('a5e75038-7711-4095-beab-1c15c53b9a2f','2290','Rapid Pathogen Screening, Inc., LIAISON® Quick Detect Covid Ag Assay',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('a7a4da6e-90a8-4dc9-a6a9-e6a82e851f58','1773','Wuhan Life Origin Biotech Joint Stock Co., Ltd., The SARS-CoV-2 Antigen Assay Kit (Immunochromatography)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('aa6702fd-ca85-4b95-91be-4ea7fcfecd18','1243','Edinburgh Genetics Limited, ActivXpress+ COVID-19 Antigen Complete Testing Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('b0951eb9-17ee-4515-b119-b21e607966c7','2183','Getein Biotech, Inc., One Step Test for SARS-CoV-2 Antigen (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('b160d0ff-923f-4551-82df-f6289f7a923d','345','SD BIOSENSOR Inc, STANDARD Q COVID-19 Ag Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('b30c39d4-fcd6-4355-a505-09bbf980a6b5','1820','Getein Biotech, Inc, SARS-CoV-2 Antigen (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('b58d2e5a-e350-4d76-98ac-a0f16d003f91','2130','Affimedix, Inc., TestNOW® - COVID-19 Antigen Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('b6ac84fe-97aa-45cd-aae5-a557240c11e4','2242','DNA Diagnostic, COVID-19 Antigen Detection Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('b93f5411-b835-4546-af20-346af0b3af85','2072','Beijing Jinwofu Bioengineering Technology Co.,Ltd., Novel Coronavirus (SARS-CoV-2) Antigen Rapid Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('b97c2ea1-f8a8-4fcd-a414-3b8a4fe794b5','768','ArcDia International Ltd, mariPOC SARS-CoV-2',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('ba22aeb9-f0eb-4a7f-8eb6-5644b760926e','2006','Jiangsu Medomics medical technology Co.,Ltd., SARS-CoV-2 antigen Test Kit (LFIA)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('bd3587e6-ee7a-4133-9af8-bdf7f99c9cb4','2116','PerGrande BioTech Development Co., Ltd., SARS-CoV-2 Antigen Detection Kit (Colloidal Gold Immunochromatographic Assay)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('be56b922-9dc1-437a-b871-e060f44a4d33','1599','Biomerica, Inc., Biomerica COVID-19 Antigen Rapid Test (nasopharyngeal swab)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('c01c7be4-1670-4753-86c6-083e9ee61dba','1833','AAZ-LMB, COVID-VIRO',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('c04ce1ad-52d5-4e4b-8a27-c38d012a2e3c','2128','Lumigenex (Suzhou) Co., Ltd, PocRoc®SARS-CoV-2 Antigen Rapid Test Kit (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('c08db4eb-54da-42d3-a753-7986211d179a','1197','Goldsite Diagnostics Inc, SARS-CoV-2 Antigen Kit (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('c5070c3b-7a26-4824-bc36-a73cf5c742d9','1481','MP Biomedicals, Rapid SARS-CoV-2 Antigen Test Card',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('c6890c2a-2ee3-4c60-9b46-e0b5e8dfcd75','2052','SD BIOSENSOR Inc, STANDARD Q COVID-19 Ag Test Nasal',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('c753cae9-6d2f-46ed-b56d-255f68f9c9a6','1989','Boditech Med Inc, AFIAS COVID-19 Ag',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('c85f8dff-6600-4efd-bb23-8b2719b0f4d4','1489','Safecare Biotech (Hangzhou) Co. Ltd, COVID-19 Antigen Rapid Test Kit (Swab)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('ce71991d-cedd-4091-a71c-3b8fd93513ec','1162','Nal von minden GmbH, NADAL COVID-19 Ag Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('cf483abd-05e6-4de3-8e03-ad36ab7eb250','1768','Shenzhen Watmind Medical Co., Ltd, SARS-CoV-2 Ag Diagnostic Test Kit (Immuno-fluorescence)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('d23a880a-e1ee-4a74-9d14-25409d1760a8','1775','MEXACARE GmbH, MEXACARE COVID-19 Antigen Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('d2938672-a665-4703-b274-493b717155d2','2079','ArcDia International Oy Ltd, mariPOC Quick Flu+',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('d743be37-a1a0-47e4-b312-12ec0dc664bf','1304','AMEDA Labordiagnostik GmbH, AMP Rapid Test SARS-CoV-2 Ag',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('db8f290b-e48f-4eb9-8723-cf14df466cc1','2017','Shenzhen Ultra-Diagnostics Biotec.Co.,Ltd, SARS-CoV-2 Antigen Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('dd48233e-395c-437a-aaa7-dfeea73266c0','2243','PCL Inc., PCL COVID19 Ag Gold',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('dd8b6db3-1857-45a8-a5ba-7468f47d7d61','1341','Qingdao Hightop Biotech Co., Ltd, SARS-CoV-2 Antigen Rapid Test (Immunochromatography)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('e00b85b6-c7d8-4819-bc70-bf404a814e82','1495','Prognosis Biotech, Rapid Test Ag 2019-nCov',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('e2aa6613-7505-4651-b04d-5b81b5f2e9e2','1919','Core Technology Co., Ltd, Coretests COVID-19 Ag Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('e47bdbfe-bc8a-46e4-919e-17648a7a0fec','1319','SGA Medikal, V-Chek SARS-CoV-2 Ag Rapid Test Kit (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('e67504c2-0992-4c98-a7bb-2d3e917f9c4a','1606','RapiGEN Inc, BIOCREDIT COVID-19 Ag - SARS-CoV 2 Antigen test',FALSE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('e9219b83-9444-4728-8dc5-800f3559bfff','1618','Artron Laboratories Inc, Artron COVID-19 Antigen Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('ee82844c-49aa-4b99-9753-4cbd900761c5','1343','Zhezhiang Orient Gene Biotech Co., Ltd, Coronavirus Ag Rapid Test Cassette (Swab)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('eea01102-c601-4820-a721-4620c665542e','1244','GenBody, Inc, Genbody COVID-19 Ag Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('ef5de043-a637-4f59-bb89-3fc7bed7726f','1365','Hangzhou Clongene Biotech Co., Ltd, COVID-19/Influenza A+B Antigen Combo Rapid Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f046eff2-0e81-455f-86b0-e778909a6575','1392','Hangzhou Testsea Biotechnology Co., Ltd, COVID-19 Antigen Test Cassette',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f0859feb-8f53-44be-8a48-55782c2b8a00','1920','Jiangsu Diagnostics Biotechnology Co.,Ltd., COVID-19 Antigen Rapid Test Cassette (Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f3b3eb5d-0c39-4752-b8f5-e1b25c72d08b','1484','Beijing Wantai Biological Pharmacy Enterprise Co., Ltd, Wantai SARS-CoV-2 Ag Rapid Test (FIA)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f4323fe3-22f9-4331-b6e6-d1e79e0c3bbd','1215','Hangzhou Laihe Biotech Co., Ltd, LYHER Novel Coronavirus (COVID-19) Antigen Test Kit(Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f46d967b-191d-4085-b16b-d2835a8e2c09','1604','Roche (SD BIOSENSOR), SARS-CoV-2 Rapid Antigen Test',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f473c937-8cdd-46ff-8d57-e64465b9eb53','1610','Hangzhou Clongene Biotech Co., Ltd, COVID-19 Antigen Rapid Test Cassette',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f480c66f-5f87-4168-892c-2bd7dcb5eae6','2108','AESKU.DIAGNOSTICS GmbH & Co. KG, AESKU.RAPID SARS-CoV-2',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f5c47557-a883-4b84-97a1-2340fcbd84da','1360','Guangdong Wesail Biotech Co., Ltd, COVID-19 Ag Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('f6768858-8a5d-476d-bbc8-a485126336f5','1144','Green Cross Medical Science Corp., GENEDIA W COVID-19 Ag',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('fa189007-e53e-4757-a3cc-d6a7d4a888c1','2074','Triplex International Biosciences (China) Co., LTD., SARS-CoV-2 Antigen Rapid Test Kit',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('fc7abc6e-3d66-457a-8a9f-b6da067db689','2139','HANGZHOU LYSUN BIOTECHNOLOGY CO., LTD., COVID-19 Antigen Rapid Test Device（Colloidal Gold）',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('fd3079c1-a959-431e-9a2e-151230706e63','1822','Anbio (Xiamen) Biotechnology Co., Ltd, Rapid COVID-19 Antigen Test(Colloidal Gold)',TRUE,TRUE);
INSERT INTO covid_19_lab_test_manufacturer_and_name (id, code, display, active, ch_issuable) values ('fd746408-98eb-4365-b4b7-64cf328ca0dc','1357','SGA Medikal, V-Chek SARS-CoV-2 Rapid Ag Test (colloidal gold)',TRUE,TRUE);


-- VERSION V1_0_8__update_vaccine_manufacturer.sql
update vaccines_covid_19_names vn
set auth_holder = 'c325995b-b493-40cc-934c-1404ed2c8353'
where vn.id = '2dc3feef-95d8-4e6f-930c-69300e10e6ee';


-- VERSION V1_0_9__create-signing-information-schema.sql
create table signing_information
(
    id uuid not null primary key,
    certificate_type varchar (50) not null,
    code varchar(50),
    alias varchar(50) not null,
    key_identifier varchar(16) not null,
    CONSTRAINT uq_signing_information_certificate_type_code UNIQUE (certificate_type, code)
);

CREATE INDEX idx_signing_information_certificate_type_code ON signing_information (certificate_type, code);


-- VERSION V1_0_10__init_signing_information.sql
INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('9758e5b1-2c8b-4aff-96dd-8757cbb6561a', 'vaccination', 'EU/1/20/1528', 'mock', '49d7bcd4e15fa060');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('0f83eba0-584a-4a29-8fb6-b5bd3408cabb', 'vaccination', 'EU/1/21/1529', 'mock', '0f5614e855d176a6');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('e967f822-7f84-4cd6-b330-7b8ecf4fae1c', 'vaccination', 'EU/1/20/1525', 'mock', '9fb3c2bae039a32a');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('4516dbaf-95de-48c3-868a-42073150a813', 'vaccination', 'CH/120/0001', 'mock', '12725e32bd706ce5');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('fa24ecc0-73c4-4ee1-978f-495fa0451812', 'vaccination', 'CH/120/0002', 'mock', 'b9a95e7d3048123c');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('a616e661-f58d-40d4-9ca6-029fcf42b1bb', 'vaccination', 'CH/120/0003', 'mock', '00b8e1530c275376');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('5ea4f883-103b-4cba-a35c-8a6d5419a0eb', 'vaccination', 'EU/1/20/1507', 'mock', 'd0d3950a0985f1ca');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('7cac86f1-ada1-4fa2-98b6-278e06384234', 'vaccination', 'CH/120/0004', 'mock', 'ba60bf3e3d384eb9');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('20b46fb3-0251-4763-9bea-6a141ee2d07b', 'recovery_ch', null, 'mock', 'bdf1d42c49e52104');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('022f9ad9-0a97-4f14-a88e-db2e14b27d44', 'recovery_non_ch', null, 'mock', '3d599325e2683251');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('9fd4227a-566f-47d0-9524-cd08940eed1b', 'test', null, 'mock', '187a1da2f44e71f9');



-- VERSION V1_0_11__update_vaccine_WHO.sql
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


-- VERSION V1_0_12__update_vaccine_WHO_corrections.sql
update vaccines_covid_19_names
set ch_issuable = false
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af9';



-- VERSION V1_0_13__migrate_vaccines.sql
alter table vaccines_covid_19_names
    add column swiss_medic boolean not null default true;
alter table vaccines_covid_19_names
    add column emea boolean not null default true;
alter table vaccines_covid_19_names
    add column who_eul boolean not null default true;


-- VERSION V1_0_14__update_vaccine_WHO_using_list.sql
update vaccines_covid_19_names
set swiss_medic = false
where id = '8ac65a1d-00fa-4425-8f3f-cb866c4aa053';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false,
    who_eul= false
where id = '4affd667-55fd-4780-bb6e-c86b0dcc7193';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false,
    who_eul= false
where id = 'dfc61e57-8c62-43d4-a9c0-390e128793c9';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false,
    who_eul= false
where id = 'bc0e71cf-bc52-45cb-b6f0-1dba39cf2a97';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false,
    who_eul= false
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af8';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false
where id = '4109a8fa-84b2-4a3f-bf33-6c746a79a11d';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false,
    who_eul= false
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af9';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af7';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false,
    who_eul= false
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b00';

update vaccines_covid_19_names
set swiss_medic = false,
    emea= false
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6f0';


-- VERSION V1_0_15__add-kpi-details.sql
ALTER TABLE kpi ADD COLUMN IF NOT EXISTS details VARCHAR(128);

-- VERSION V1_0_16__add-kpi-country.sql
ALTER TABLE kpi ADD COLUMN IF NOT EXISTS country VARCHAR(2);


-- VERSION V1_0_17__migrate_vaccines.sql
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


-- VERSION V1_0_18__create_vaccines_to_segregate_issueable.sql
INSERT INTO vaccines_covid_19_names ( id
                                    , code
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable)
VALUES ( 'fe39b8af-eccd-45de-acd7-ce7206052b01'
       , 'EU/1/20/1528'
       , 'Comirnaty'
       , '0936bf83-bfe5-4897-8b10-adf2664608e6'
       , '51240395-839e-40c2-8d03-09964a3f9011'
       , true
       , true
       , '2021-09-01 15:59:23.700729'
       , true
       , true
       , true
       , 'ch_only'
       , false
       , true
       , false);

INSERT INTO vaccines_covid_19_names ( id
                                    , code
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable)
VALUES ( 'fe39b8af-eccd-45de-acd7-ce7206052b02'
       , 'EU/1/20/1528'
       , 'Comirnaty'
       , '0936bf83-bfe5-4897-8b10-adf2664608e6'
       , '51240395-839e-40c2-8d03-09964a3f9011'
       , true
       , true
       , '2021-09-01 15:59:23.700729'
       , true
       , true
       , true
       , 'abroad_only'
       , false
       , false
       , true);

INSERT INTO vaccines_covid_19_names ( id
                                    , code
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable)
VALUES ( 'fe39b8af-eccd-45de-acd7-ce7206052b03'
       , 'EU/1/20/1507'
       , 'Spikevax (previously COVID-19 Vaccine Moderna)'
       , '0936bf83-bfe5-4897-8b10-adf2664608e6'
       , 'c325995b-b493-40cc-934c-1404ed2c8353'
       , true
       , true
       , '2021-09-01 15:59:23.700729'
       , true
       , true
       , true
       , 'ch_only'
       , false
       , true
       , false);

INSERT INTO vaccines_covid_19_names ( id
                                    , code
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable)
VALUES ( 'fe39b8af-eccd-45de-acd7-ce7206052b04'
       , 'EU/1/20/1507'
       , 'Spikevax (previously COVID-19 Vaccine Moderna)'
       , '0936bf83-bfe5-4897-8b10-adf2664608e6'
       , 'c325995b-b493-40cc-934c-1404ed2c8353'
       , true
       , true
       , '2021-09-01 15:59:23.700729'
       , true
       , true
       , true
       , 'abroad_only'
       , false
       , false
       , true);

INSERT INTO vaccines_covid_19_names ( id
                                    , code
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable)
VALUES ( 'fe39b8af-eccd-45de-acd7-ce7206052b05'
       , 'EU/1/20/1525'
       , 'COVID-19 Vaccine Janssen'
       , '1ee3559a-ed64-4062-a10e-baeded624ae8'
       , 'bdda14a3-db94-41f6-8c20-963112713a76'
       , true
       , true
       , '2021-09-01 15:59:23.700729'
       , true
       , true
       , true
       , 'ch_only'
       , false
       , true
       , false);

INSERT INTO vaccines_covid_19_names ( id
                                    , code
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable)
VALUES ( 'fe39b8af-eccd-45de-acd7-ce7206052b06'
       , 'EU/1/20/1525'
       , 'COVID-19 Vaccine Janssen'
       , '1ee3559a-ed64-4062-a10e-baeded624ae8'
       , 'bdda14a3-db94-41f6-8c20-963112713a76'
       , true
       , true
       , '2021-09-01 15:59:23.700729'
       , true
       , true
       , true
       , 'abroad_only'
       , false
       , false
       , true);



-- VERSION V1_0_19__update_vaccines_to_specification.sql
-- Comirnaty
update vaccines_covid_19_names
set issuable                = 'ch_and_abroad',
    web_ui_selectable       = true,
    api_gateway_selectable  = false,
    api_platform_selectable = false
where id = '0af1a070-2647-4acb-a1be-4df81b47840b';

-- Spikevax (previously COVID-19 Vaccine Moderna)
update vaccines_covid_19_names
set issuable                = 'ch_and_abroad',
    web_ui_selectable       = true,
    api_gateway_selectable  = false,
    api_platform_selectable = false
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6ee';

-- COVID-19 Vaccine Janssen
update vaccines_covid_19_names
set issuable                = 'ch_and_abroad',
    web_ui_selectable       = true,
    api_gateway_selectable  = false,
    api_platform_selectable = false
where id = 'ce2aad87-0d0b-4c5d-8a79-e15576020c05';

-- Vaxzevria (AstraZeneca AB)
update vaccines_covid_19_names
set issuable                = 'abroad_only',
    web_ui_selectable       = true,
    api_platform_selectable = true
where id = '8ac65a1d-00fa-4425-8f3f-cb866c4aa053';

-- Covishield
update vaccines_covid_19_names
set issuable                = 'abroad_only',
    web_ui_selectable       = true,
    api_platform_selectable = true
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6f0';

-- BBIBP-CorV (China Sinopharm International Corp. - Beijing location)
update vaccines_covid_19_names
set issuable          = 'abroad_only',
    web_ui_selectable = true
where id = '4109a8fa-84b2-4a3f-bf33-6c746a79a11d';

-- CoronaVac (Sinovac Biotech)
update vaccines_covid_19_names
set issuable          = 'abroad_only',
    web_ui_selectable = true
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af7';


-- VERSION V1_0_20__migrate_vaccines.sql
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
set vaccine_order = 200 where web_ui_selectable = false;

-- VERSION V1_0_21__update-signing-information-schema.sql
ALTER TABLE signing_information ADD COLUMN IF NOT EXISTS certificate_alias VARCHAR(16);
ALTER TABLE signing_information ADD COLUMN IF NOT EXISTS valid_from date not null default now();
ALTER TABLE signing_information ADD COLUMN IF NOT EXISTS valid_to date not null default '2999-12-31';

-- VERSION V1_0_22__update-signing-information-schema.sql
ALTER TABLE signing_information
    ALTER COLUMN certificate_alias TYPE VARCHAR(50);


-- VERSION V1_0_24__update_vaccines.sql
update vaccines_covid_19_names
set api_gateway_selectable  = true
  , api_platform_selectable = true
  , modified_at             = '2021-10-20 15:00:00.000000'
where id = '0af1a070-2647-4acb-a1be-4df81b47840b';

delete
from vaccines_covid_19_names
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b01';
delete
from vaccines_covid_19_names
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b02';

update vaccines_covid_19_names
set api_gateway_selectable  = true
  , api_platform_selectable = true
  , modified_at             = '2021-10-20 15:00:00.000000'
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6ee';

delete
from vaccines_covid_19_names
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b03';
delete
from vaccines_covid_19_names
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b04';

update vaccines_covid_19_names
set api_gateway_selectable  = true
  , api_platform_selectable = true
  , modified_at             = '2021-10-20 15:00:00.000000'
where id = 'ce2aad87-0d0b-4c5d-8a79-e15576020c05';

delete
from vaccines_covid_19_names
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b05';
delete
from vaccines_covid_19_names
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b06';

update vaccines_covid_19_names
set api_platform_selectable = false
  , modified_at             = '2021-10-20 15:00:00.000000'
where id = '8ac65a1d-00fa-4425-8f3f-cb866c4aa053';

INSERT INTO public.vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                            swiss_medic, emea, who_eul, issuable, web_ui_selectable,
                                            api_gateway_selectable, api_platform_selectable, code, vaccine_order)
VALUES ('8ac65a1d-00fa-4425-8f3f-cb866c4ab153', 'Vaxzevria', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906032', true, true, '2021-10-20 15:00:00.000000', false, true, true,
        'ch_and_abroad', false, true, true, 'EU/1/21/1529', 40)
ON CONFLICT DO NOTHING;

update vaccines_covid_19_names
set api_platform_selectable = false
  , display                 = 'other AstraZeneca vaccines: COVISHIELD / AZD1222 / ChAdOx1 nCoV-19/ChAdOx1-S/…'
  , modified_at             = '2021-10-20 15:00:00.000000'
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6f0';

INSERT INTO public.vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                            swiss_medic, emea, who_eul, issuable, web_ui_selectable,
                                            api_gateway_selectable, api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e10f7f0',
        'other AstraZeneca vaccines: COVISHIELD / AZD1222 / ChAdOx1 nCoV-19/ChAdOx1-S/…',
        '1ee3559a-ed64-4062-a10e-baeded624ae8', '279ad11e-f040-442d-8e97-1262423e3e0b', true, true,
        '2021-10-20 15:00:00.000000', false, false, true, 'ch_and_abroad', false, true, true, 'Covishield', 50)
ON CONFLICT DO NOTHING;

update vaccines_covid_19_names
set modified_at = '2021-10-20 15:00:00.000000'
where id = '4109a8fa-84b2-4a3f-bf33-6c746a79a11d';

INSERT INTO public.vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                            swiss_medic, emea, who_eul, issuable, web_ui_selectable,
                                            api_gateway_selectable, api_platform_selectable, code, vaccine_order)
VALUES ('4109a8fa-84b2-4a3f-bf33-6c746a79b21d', 'BBIBP-CorV (Vero Cells)', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        '0ccef01d-b2af-4fb8-9cf8-2b9cdc265562', true, true, '2021-10-20 15:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'BBIBP-CorV', 60)
ON CONFLICT DO NOTHING;

update vaccines_covid_19_names
set modified_at = '2021-10-20 15:00:00.000000'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af7';

INSERT INTO public.vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                            swiss_medic, emea, who_eul, issuable, web_ui_selectable,
                                            api_gateway_selectable, api_platform_selectable, code, vaccine_order)
VALUES ('fe39b8af-eccd-45de-acd7-ce7206053bf7', 'COVID-19 Vaccine (Vero Cell), Inactivated/ Coronavac',
        '1ee3559a-ed64-4062-a10e-baeded624ae8', 'fefacc78-58f1-4d80-a97f-6884cb99da06', true, true,
        '2021-10-20 15:00:00.000000', false, false, true, 'ch_and_abroad', false, true, true, 'CoronaVac', 70)
ON CONFLICT DO NOTHING;


-- VERSION V1_0_25__init_signing_information_antibody_ch.sql
INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('20b46fb3-0251-4763-9bea-6a141ee2d07c', 'antibody_ch', null, 'mock', 'bdf1d42c49e52104');


-- VERSION V1_0_26__update_vaccines_covaxin.sql
update vaccines_covid_19_names
set modified_at             = '2021-11-11 11:11:11.111111'
  , ch_issuable             = true
  , web_ui_selectable       = true
  , api_gateway_selectable  = true
  , api_platform_selectable = true
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b00';


-- VERSION V1_0_27__update_local_vaccine_codes.sql
update signing_information
set code = 'BBIBP-CorV'
where id = '4516dbaf-95de-48c3-868a-42073150a813';
update signing_information
set code = 'CoronaVac'
where id = 'a616e661-f58d-40d4-9ca6-029fcf42b1bb';
update signing_information
set code = 'Covishield'
where id = '7cac86f1-ada1-4fa2-98b6-278e06384234';


-- VERSION V1_0_28__init_signing_information_covaxin.sql
INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('20b46fb3-0251-4763-9bea-6a141ee2d17c', 'vaccination', 'Covaxin', 'mock', 'YXpNbFM4b01sUXM9');


-- VERSION V1_0_29__update_vaccines_covaxin_corrections.sql
delete
from vaccines_covid_19_names
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b02';

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable,
                                     api_gateway_selectable, api_platform_selectable, code, vaccine_order)
VALUES ('fe39b8af-eccd-45de-acd7-ce7206052b02', 'Covaxin (also known as BBV152 A, B, C)',
        '1ee3559a-ed64-4062-a10e-baeded624af0', '8f910905-1c14-4f92-befd-6174c1aba905', true, true,
        '2021-11-11 11:11:11.111111', false, false, true, 'abroad_only', true, false, false, 'Covaxin', 200);

update vaccines_covid_19_names
set issuable                = 'ch_and_abroad'
  , who_eul                 = true
  , web_ui_selectable       = false
  , api_gateway_selectable  = true
  , api_platform_selectable = true
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b00';

update vaccines_covid_19_names
set prophylaxis = '1ee3559a-ed64-4062-a10e-baeded624ae8'
  , auth_holder = '8f910905-1c14-4f92-befd-6174c1aba905'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b02';

update vaccines_covid_19_names
set prophylaxis = '1ee3559a-ed64-4062-a10e-baeded624ae8'
  , auth_holder = '8f910905-1c14-4f92-befd-6174c1aba905'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b00';


-- VERSION V1_0_30__migrate_vaccines_analog_vaccine.sql
alter table vaccines_covid_19_names
    add column analog_vaccine varchar(50);


-- VERSION V1_0_31__update_vaccines_analog_vaccine.sql
update vaccines_covid_19_names
set analog_vaccine = 'EU/1/21/1529'
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e10e6f0',
             '2dc3feef-95d8-4e6f-930c-69300e10f7f0'
    );


-- VERSION V1_0_32__init_signing_information_vaccination_tourist_ch.sql
INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('20b46fb3-0251-4763-9bea-6a141ee2d07d', 'vaccination_tourist_ch', null, 'mock', 'bdf1d42c49e52105');


-- VERSION V1_0_33__drop_signing_information_unique_constraint.sql
ALTER TABLE signing_information DROP CONSTRAINT IF EXISTS uq_signing_information_certificate_type_code;

-- VERSION V1_0_34__add_valid_until_column.sql
ALTER TABLE covid_19_lab_test_manufacturer_and_name ADD COLUMN valid_until TIMESTAMP WITH TIME ZONE DEFAULT NULL;

-- VERSION V1_0_35__update_vaccines_separate_astra_zeneca.sql
INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('dd5d6ced-0156-4f0c-a49a-db9b71906133', 'ORG-100007893', 'R-Pharm CJSC', true, '2021-12-13 12:00:00.000000');

INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('dd5d6ced-0156-4f0c-a49a-db9b71906134', 'Fiocruz', 'Fiocruz', true, '2021-12-13 12:00:00.000000');

INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('dd5d6ced-0156-4f0c-a49a-db9b71906135', 'CIGB', 'Center for Genetic Engineering and Biotechnology', true,
        '2021-12-13 12:00:00.000000');

INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('dd5d6ced-0156-4f0c-a49a-db9b71906136', 'Chumakov-Federal-Scientific-Center',
        'Chumakov Federal Scientific Center for Research and Development of Immune-and-Biological Products', true,
        '2021-12-13 12:00:00.000000');

INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('dd5d6ced-0156-4f0c-a49a-db9b71906137', 'ORG-100023050', 'Gulf Pharmaceutical Industries', true,
        '2021-12-13 12:00:00.000000');

INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('dd5d6ced-0156-4f0c-a49a-db9b71906138', 'ORG-100033914', 'Medigen Vaccine Biologics Corporation', true,
        '2021-12-13 12:00:00.000000');

INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at)
VALUES ('dd5d6ced-0156-4f0c-a49a-db9b71906139', 'Sinopharm-WIBP', 'Sinopharm - Wuhan Institute of Biological Products',
        true, '2021-12-13 12:00:00.000000');

update vaccines_covid_19_names
set display     = 'Covishield (ChAdOx1_nCoV-19)',
    modified_at = '2021-12-13 12:00:00.000000'
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6f0';
update vaccines_covid_19_names
set display     = 'Covishield (ChAdOx1_nCoV-19)',
    modified_at = '2021-12-13 12:00:00.000000'
where id = '2dc3feef-95d8-4e6f-930c-69300e10f7f0';

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e11e6f0', 'R-COVI', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906133', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'abroad_only', true, false, false, 'R-COVI', 50, 'EU/1/21/1529');

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e11f7f0', 'R-COVI', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906133', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'R-COVI', 50, 'EU/1/21/1529');

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e12e6f0', 'Covid-19 (recombinant)', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906134', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'abroad_only', true, false, false, 'Covid-19-recombinant', 50, 'EU/1/21/1529');

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e12f7f0', 'Covid-19 (recombinant)', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906134', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'Covid-19-recombinant', 50, 'EU/1/21/1529');

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e12f8f0', 'Abdala', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906135', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'Abdala', 50);

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e12f9f0', 'CoviVac', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906136', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'CoviVac', 50);

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e12faf0', 'Hayat-Vax', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906137', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'Hayat-Vax', 50);

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e12fbf0', 'MVC COVID-19 vaccine', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906138', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'MVC-COV1901', 50);

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e12fcf0', 'Sputnik Light', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        '4a05dd07-f1ef-4d46-b771-5a93ab17c96e', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'Sputnik-Light', 50);

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e12fdf0', 'WIBP-CorV', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906139', true, true, '2021-12-13 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'WIBP-CorV', 50);

update vaccines_covid_19_names
set prophylaxis = '1ee3559a-ed64-4062-a10e-baeded624ae8'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af8';
update vaccines_covid_19_names
set auth_holder = 'f1876bf8-b8fd-4090-8a3f-17d12347d695'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af9';

delete
from sct_vaccines_covid_19
where id in ('1ee3559a-ed64-4062-a10e-baeded624ae9', '1ee3559a-ed64-4062-a10e-baeded624af0');


-- VERSION V1_0_36__update_vaccines_error_and_sorting_corrections.sql
update vaccines_covid_19_names
set api_gateway_selectable = false
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e12fdf0',
             '2dc3feef-95d8-4e6f-930c-69300e12faf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fbf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fcf0',
             '2dc3feef-95d8-4e6f-930c-69300e12f8f0',
             '2dc3feef-95d8-4e6f-930c-69300e12f9f0'
    );

update vaccines_covid_19_names
set api_platform_selectable = false
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e12fdf0',
             '2dc3feef-95d8-4e6f-930c-69300e12faf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fbf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fcf0',
             '2dc3feef-95d8-4e6f-930c-69300e12f8f0',
             '2dc3feef-95d8-4e6f-930c-69300e12f9f0'
    );

update vaccines_covid_19_names
set vaccine_order = 55
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e11f7f0',
             '2dc3feef-95d8-4e6f-930c-69300e11e6f0'
    );

update vaccines_covid_19_names
set vaccine_order = 57
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e12f7f0',
             '2dc3feef-95d8-4e6f-930c-69300e12e6f0'
    );

update vaccines_covid_19_names
set display = 'Vaxzevria'
where id = '8ac65a1d-00fa-4425-8f3f-cb866c4aa053';

update vaccines_covid_19_names
set vaccine_order = 200
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e12fdf0',
             '2dc3feef-95d8-4e6f-930c-69300e12f9f0',
             '2dc3feef-95d8-4e6f-930c-69300e12faf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fbf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fcf0',
             '2dc3feef-95d8-4e6f-930c-69300e12f8f0'
    );

update vaccines_covid_19_names
set vaccine_order = 80
where id in (
             'fe39b8af-eccd-45de-acd7-ce7206052b02',
             'fe39b8af-eccd-45de-acd7-ce7206052b00'
    );


-- VERSION V1_0_37__init_signing_information_exceptional_ch.sql
INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('20b46fb3-0251-4763-9bea-6a141ee2d07e', 'exceptional_ch', null, 'mock', 'bdf1d42c49e52106');


-- VERSION V1_0_38__update_vaccines_nvx_cov2373_covovax.sql
INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e22e6f0', 'NVX-CoV2373 (also known as Nuvaxovid)',
        '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'ed9c2a60-4b8e-4b1c-9370-d4df32786086', true, true, '2022-01-14 12:00:00.000000', false, true, false,
        'abroad_only', true, false, false, 'EU/1/21/1618', 45);

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e22e7f0', 'NVX-CoV2373 (also known as Nuvaxovid)',
        '1ee3559a-ed64-4062-a10e-baeded624ae8',
        '279ad11e-f040-442d-8e97-1262423e3e0b', true, true, '2022-01-14 12:00:00.000000', false, true, false,
        'ch_and_abroad', false, true, true, 'EU/1/21/1618', 45);

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e33e6f0', 'COVOVAX (Novavax formulation)', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        '279ad11e-f040-442d-8e97-1262423e3e0b', true, true, '2022-01-14 12:00:00.000000', false, false, true,
        'abroad_only', true, false, false, 'COVOVAX', 55, 'EU/1/21/1618');

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e33f7f0', 'COVOVAX (Novavax formulation)', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'dd5d6ced-0156-4f0c-a49a-db9b71906133', true, true, '2022-01-14 12:00:00.000000', false, false, true,
        'ch_and_abroad', false, true, true, 'COVOVAX', 55, 'EU/1/21/1618');


-- VERSION V1_0_39__init_signing_information_new_vaccines.sql
INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('20b46fb3-0251-4763-9bea-6a141ef3d07e', 'vaccination', 'EU/1/21/1618', 'mock', 'bdf1d42c49e52107');

INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('20b46fb3-0251-4763-9bea-6a141ef3e07e', 'vaccination', 'COVOVAX', 'mock', 'bdf1d42c49e52108');


-- VERSION V1_0_40__update_vaccines_sort_order.sql
update vaccines_covid_19_names
set vaccine_order = 58
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e33e6f0',
             '2dc3feef-95d8-4e6f-930c-69300e33f7f0'
    );


-- VERSION V1_0_41__update_vaccines_to_eu_2_5_0.sql
INSERT INTO vaccines_covid_19_names (id, display, prophylaxis,
                                     auth_holder, active, ch_issuable, modified_at,
                                     swiss_medic, emea, who_eul, issuable,
                                     web_ui_selectable, api_gateway_selectable, api_platform_selectable,
                                     code, vaccine_order, analog_vaccine)
VALUES ('2dc3feef-95d8-4e6f-930c-69300e22e6f1', 'NVX-CoV2373 (deprecated)', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'ed9c2a60-4b8e-4b1c-9370-d4df32786086', false, false, '2022-01-19 12:00:00.000000', false, false, false,
        'abroad_only',
        false, false, false, 'NVX-CoV2373', 210, null);

update vaccines_covid_19_names
set display     = 'Nuvaxovid',
    modified_at = '2022-01-19 12:00:00.000000'
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e22e6f0',
             '2dc3feef-95d8-4e6f-930c-69300e22e7f0'
    );

update vaccines_covid_19_names
set display     = 'Inactivated SARS-CoV-2 (Vero Cell) (deprecated)',
    active      = false,
    modified_at = '2022-01-19 12:00:00.000000'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af9';

update vaccines_covid_19_names
set display     = 'Spikevax',
    modified_at = '2022-01-19 12:00:00.000000'
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6ee';

update vaccines_covid_19_auth_holders
set display     = 'Novavax CZ a.s.',
    modified_at = '2022-01-19 12:00:00.000000'
where id = 'ed9c2a60-4b8e-4b1c-9370-d4df32786086';


-- VERSION V1_0_42__add-kpi-migration-columns.sql
ALTER TABLE kpi
    ADD COLUMN IF NOT EXISTS system_source VARCHAR(3);

ALTER TABLE kpi
    ADD COLUMN IF NOT EXISTS api_gateway_id VARCHAR(36);



-- VERSION V1_0_43__init_signing_information_recovery_rat_ch.sql
INSERT INTO signing_information (id, certificate_type, code, alias, key_identifier)
VALUES ('20b46fb3-0251-4763-9bea-6a141ee2d07a', 'recovery_rat_ch', null, 'mock', 'bdf1d42c49e52104');


-- VERSION V1_0_44__update_vaccines_display_names.sql
update vaccines_covid_19_names
set display = 'BBIBP-CorV'
where id = '4109a8fa-84b2-4a3f-bf33-6c746a79a11d';
update vaccines_covid_19_names
set display = 'BBIBP-CorV'
where id = '4109a8fa-84b2-4a3f-bf33-6c746a79b21d';
update vaccines_covid_19_names
set display = 'CoronaVac'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af7';
update vaccines_covid_19_names
set display = 'CoronaVac'
where id = 'fe39b8af-eccd-45de-acd7-ce7206053bf7';

-- VERSION V1_0_45__update_vaccines_code_of_covovax.sql
update vaccines_covid_19_names
set code = 'Covovax'
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e33e6f0',
             '2dc3feef-95d8-4e6f-930c-69300e33f7f0'
    );

update vaccines_covid_19_names
set display = 'BBIBP-CorV / Covilo'
where id in (
             '4109a8fa-84b2-4a3f-bf33-6c746a79a11d',
             '4109a8fa-84b2-4a3f-bf33-6c746a79b21d'
    );


-- VERSION V1_0_46__add-kpi-in-app-delivery-column.sql
ALTER TABLE kpi
    ADD COLUMN IF NOT EXISTS in_app_delivery_code VARCHAR(15);


-- VERSION V1_0_47__add_fraud_columns.sql
alter table kpi add column if not exists fraud boolean not null default false;
alter table revocation add column if not exists fraud boolean not null default false;

-- VERSION V1_0_48__update_vaccines_nuvaxovid.sql
update vaccines_covid_19_names
set swiss_medic = true,
    web_ui_selectable = true,
    who_eul = true
where id = '2dc3feef-95d8-4e6f-930c-69300e22e7f0';

delete
from vaccines_covid_19_names
where id = '2dc3feef-95d8-4e6f-930c-69300e22e6f0';

-- VERSION V1_0_49__update_vaccines_nuvaxovid_auth_holder.sql
update vaccines_covid_19_names
set auth_holder = 'ed9c2a60-4b8e-4b1c-9370-d4df32786086' -- Novavax CZ a.s.
where id = '2dc3feef-95d8-4e6f-930c-69300e22e7f0'   -- Nuvaxovid
;

update vaccines_covid_19_names
set auth_holder = '279ad11e-f040-442d-8e97-1262423e3e0b' -- Serum Institute Of India Private Limited
where id = '2dc3feef-95d8-4e6f-930c-69300e33f7f0' -- Covovax
;

-- VERSION V1_0_50__add-kpi-column-fixed-user-id.sql
ALTER TABLE kpi
    ADD COLUMN IF NOT EXISTS fixed_user_id VARCHAR(64);


-- VERSION V1_0_51__migrate_signing_information.sql
alter table signing_information
    drop column key_identifier;


-- VERSION V1_0_52__migrate_kpi_for_key_identifier.sql
alter table kpi
    add column IF NOT EXISTS key_identifier varchar(16);


-- VERSION V1_0_53__update_signing_information_for_local_development.sql
update signing_information
set certificate_alias = 'mock'
where id in (
             '9758e5b1-2c8b-4aff-96dd-8757cbb6561a',
             '0f83eba0-584a-4a29-8fb6-b5bd3408cabb',
             'e967f822-7f84-4cd6-b330-7b8ecf4fae1c',
             'fa24ecc0-73c4-4ee1-978f-495fa0451812',
             '5ea4f883-103b-4cba-a35c-8a6d5419a0eb',
             '20b46fb3-0251-4763-9bea-6a141ee2d07b',
             '022f9ad9-0a97-4f14-a88e-db2e14b27d44',
             '9fd4227a-566f-47d0-9524-cd08940eed1b',
             '20b46fb3-0251-4763-9bea-6a141ee2d07c',
             '4516dbaf-95de-48c3-868a-42073150a813',
             'a616e661-f58d-40d4-9ca6-029fcf42b1bb',
             '7cac86f1-ada1-4fa2-98b6-278e06384234',
             '20b46fb3-0251-4763-9bea-6a141ee2d17c',
             '20b46fb3-0251-4763-9bea-6a141ee2d07d',
             '20b46fb3-0251-4763-9bea-6a141ee2d07e',
             '20b46fb3-0251-4763-9bea-6a141ef3d07e',
             '20b46fb3-0251-4763-9bea-6a141ef3e07e',
             '20b46fb3-0251-4763-9bea-6a141ee2d07a'
    );


-- VERSION V1_0_54__migrate_kpi_for_conversion_kpi.sql
alter table kpi
    drop column if exists fraud;

alter table kpi
    add column if not exists origin_uvci varchar(39);

alter table kpi
    add column if not exists conversion_reason varchar(64);


-- VERSION V1_0_55__update_signing_information_for_conversion_locally.sql
INSERT INTO signing_information
( id
, certificate_type
, alias
, certificate_alias
, valid_from
, valid_to)
VALUES ( '20b46fb3-0251-4763-9bea-6a141ff2d07d'
       , 'vaccination_converted'
       , 'mock'
       , 'mock'
       , '2022-06-08', '2999-12-31');


-- VERSION V1_0_56__add_slot_to_signing_information.sql
alter table signing_information
    add column if not exists slot_number integer not null default 0;

-- VERSION V1_0_57__update_vaccines_add_VLA2001.sql
INSERT INTO vaccines_covid_19_auth_holders ( id
                                           , code
                                           , display
                                           , active
                                           , modified_at)
VALUES ( 'ed9c2a60-4b8e-4b1c-9370-d4df32781057'
       , 'ORG-10013793'
       , 'Valneva France'
       , true
       , '2022-08-16 09:00:00.000000');

INSERT INTO vaccines_covid_19_names ( id
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable
                                    , code
                                    , vaccine_order
                                    , analog_vaccine)
VALUES ( '8ac65a1d-00fa-4425-8f4f-cb866c4aa057'
       , 'VLA2001'
       , '1ee3559a-ed64-4062-a10e-baeded624ae8'
       , 'ed9c2a60-4b8e-4b1c-9370-d4df32781057'
       , true
       , true
       , '2022-08-16 09:00:00.000000'
       , false
       , true
       , true
       , 'abroad_only'
       , true
       , false
       , false
       , 'EU/1/21/1624/001'
       , 42
       , null);

INSERT INTO vaccines_covid_19_names ( id
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable
                                    , code
                                    , vaccine_order
                                    , analog_vaccine)
VALUES ( '8ac65a1d-00fa-4425-8f4f-cb866c4ab157'
       , 'VLA2001'
       , '1ee3559a-ed64-4062-a10e-baeded624ae8'
       , 'ed9c2a60-4b8e-4b1c-9370-d4df32781057'
       , true
       , true
       , '2022-08-16 09:00:00.000000'
       , false
       , true
       , true
       , 'ch_and_abroad'
       , false
       , true
       , true
       , 'EU/1/21/1624/001'
       , 42
       , null);

-- VERSION V1_0_58__update_vaccines_add_Convidecia.sql
INSERT INTO vaccines_covid_19_names ( id
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable
                                    , code
                                    , vaccine_order
                                    , analog_vaccine)
VALUES ( '8ac65a1d-00fa-4425-8f4f-cb866c4bb057'
       , 'Convidecia'
       , '1ee3559a-ed64-4062-a10e-baeded624ae8'
       , '14f4b340-59c2-483f-b6c3-74199f446ee2'
       , true
       , true
       , '2022-08-16 09:00:00.000000'
       , false
       , false
       , true
       , 'abroad_only'
       , true
       , false
       , false
       , 'Convidecia'
       , 82
       , null);

INSERT INTO vaccines_covid_19_names ( id
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable
                                    , code
                                    , vaccine_order
                                    , analog_vaccine)
VALUES ( '8ac65a1d-00fa-4425-8f4f-cb866c4bc157'
       , 'Convidecia'
       , '1ee3559a-ed64-4062-a10e-baeded624ae8'
       , '14f4b340-59c2-483f-b6c3-74199f446ee2'
       , true
       , true
       , '2022-08-16 09:00:00.000000'
       , false
       , false
       , true
       , 'ch_and_abroad'
       , false
       , true
       , true
       , 'Convidecia'
       , 82
       , null);

-- VERSION V1_0_59__update_signing_information_for_new_vaccinesy.sql
INSERT INTO signing_information
( id
, certificate_type
, code
, alias
, certificate_alias
, valid_from
, valid_to)
VALUES ( '20b46fb3-0251-4763-9bea-6a142aa2d07d'
       , 'vaccination'
       , 'EU/1/21/1624/001'
       , 'mock'
       , 'mock'
       , '2022-08-16', '2999-12-31');

INSERT INTO signing_information
( id
, certificate_type
, code
, alias
, certificate_alias
, valid_from
, valid_to)
VALUES ( '20b46fb3-0251-4763-9bea-6a142ab2d07d'
       , 'vaccination'
       , 'Convidecia'
       , 'mock'
       , 'mock'
       , '2022-08-16', '2999-12-31');


-- VERSION V1_0_60__update_vaccines_change_code_of_VLA2001.sql
UPDATE vaccines_covid_19_names
set code = 'VLA2001'
where id = '8ac65a1d-00fa-4425-8f4f-cb866c4aa057';
UPDATE vaccines_covid_19_names
set code = 'VLA2001'
where id = '8ac65a1d-00fa-4425-8f4f-cb866c4ab157';


-- VERSION V1_0_61__update_vaccines_bug_corrections.sql
UPDATE vaccines_covid_19_auth_holders
set code = 'ORG-100036422'
where id = 'ed9c2a60-4b8e-4b1c-9370-d4df32781057';

DELETE
FROM vaccines_covid_19_names
where id = 'bc0e71cf-bc52-45cb-b6f0-1dba39cf2a97';

UPDATE vaccines_covid_19_names
set vaccine_order = '47'
where id in ('8ac65a1d-00fa-4425-8f4f-cb866c4aa057', '8ac65a1d-00fa-4425-8f4f-cb866c4ab157');


-- VERSION V1_0_62__update_vaccines_last_minute_changes.sql
delete
from vaccines_covid_19_names
where id = '8ac65a1d-00fa-4425-8f4f-cb866c4aa057';

update vaccines_covid_19_names
set swiss_medic       = true,
    web_ui_selectable = true
where id = '8ac65a1d-00fa-4425-8f4f-cb866c4ab157';


-- VERSION V1_0_63__update_signing_information_for_new_vaccines.sql
INSERT INTO signing_information
( id
, certificate_type
, code
, alias
, certificate_alias
, valid_from
, valid_to
, slot_number)
VALUES ( '7cac86f1-ada1-4fa2-98b6-278e06385334'
       , 'vaccination'
       , 'R-COVI'
       , 'mock'
       , 'mock'
       , '2022-09-13'
       , '2999-12-31'
       , 0);

INSERT INTO signing_information
( id
, certificate_type
, code
, alias
, certificate_alias
, valid_from
, valid_to
, slot_number)
VALUES ( '7cac86f1-ada1-4fa2-98b6-278e06385434'
       , 'vaccination'
       , 'Covid-19-recombinant'
       , 'mock'
       , 'mock'
       , '2022-09-13'
       , '2999-12-31'
       , 0);

update signing_information
set code = 'VLA2001'
where id = '20b46fb3-0251-4763-9bea-6a142aa2d07d';


-- VERSION V1_0_64__update_vaccines_Covovax_wrong_code.sql
update signing_information
set code = 'Covovax'
where id = '20b46fb3-0251-4763-9bea-6a141ef3e07e';


-- VERSION V1_0_65__add_isdeleted_to_revocation_table.sql
alter table revocation
    add column if not exists deleted_date_time timestamp;

-- VERSION V1_0_66__update_vaccines_VLA2001_aligned_with_EU.sql
update vaccines_covid_19_names
set active  = false,
    display = 'VLA2001 (deprecated)'
where id = '8ac65a1d-00fa-4425-8f4f-cb866c4ab157';

insert into vaccines_covid_19_names ( id
                                    , display
                                    , prophylaxis
                                    , auth_holder
                                    , active
                                    , ch_issuable
                                    , modified_at
                                    , swiss_medic
                                    , emea
                                    , who_eul
                                    , issuable
                                    , web_ui_selectable
                                    , api_gateway_selectable
                                    , api_platform_selectable
                                    , code
                                    , vaccine_order
                                    , analog_vaccine)
values ( '8ac65a1d-00fa-4425-8f4f-cb866c4ab268'
       , 'COVID-19 Vaccine Valneva'
       , '1ee3559a-ed64-4062-a10e-baeded624ae8'
       , 'ed9c2a60-4b8e-4b1c-9370-d4df32781057'
       , true
       , true
       , '2022-09-30 09:00:00.000000'
       , true
       , true
       , true
       , 'ch_and_abroad'
       , true
       , true
       , true
       , 'EU/1/21/1624'
       , 47
       , null);


-- VERSION V1_0_67__update_signing_information_EU_2.9.0.sql
INSERT INTO signing_information
( id
, certificate_type
, code
, alias
, certificate_alias
, valid_from
, valid_to
, slot_number)
VALUES ( '7cac86f1-ada1-4fa2-98b6-278e06385445'
       , 'vaccination'
       , 'EU/1/21/1624'
       , 'mock'
       , 'mock'
       , '2022-09-30'
       , '2999-12-31'
       , 0);
