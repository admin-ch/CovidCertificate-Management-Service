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
