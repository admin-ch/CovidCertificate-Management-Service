INSERT INTO vaccines_covid_19_auth_holders (id, code, display, active, modified_at, created_at)
SELECT '2c606986-abac-4dcc-bb60-888901e8722b',
       'ORG-100000788',
       'Sanofi Pasteur',
       true,
       '2022-12-22 00:00:02.394679',
       '2022-10-31 10:00:01.611582'
WHERE NOT EXISTS(SELECT id FROM vaccines_covid_19_auth_holders WHERE id = '2c606986-abac-4dcc-bb60-888901e8722b');

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, modified_at, swiss_medic, emea,
                                     who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine, created_at)
SELECT '23da4499-0d5b-4397-8d1e-fe614c26b604',
       'VidPrevtyn Beta',
       '1ee3559a-ed64-4062-a10e-baeded624ae8',
       '2c606986-abac-4dcc-bb60-888901e8722b',
       true,
       '2022-12-22 00:00:01.866260',
       false,
       true,
       true,
       'ch_and_abroad',
       false,
       true,
       true,
       'EU/1/21/1580',
       46,
       null,
       '2022-12-22 00:00:01.866260'
WHERE NOT EXISTS(SELECT id FROM vaccines_covid_19_names WHERE id = '23da4499-0d5b-4397-8d1e-fe614c26b604');

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, modified_at, swiss_medic, emea,
                                     who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine, created_at)
SELECT '23da4499-0d5b-4397-8d1e-fe614c26c614',
       'VidPrevtyn Beta',
       '1ee3559a-ed64-4062-a10e-baeded624ae8',
       '2c606986-abac-4dcc-bb60-888901e8722b',
       true,
       '2022-12-22 00:00:01.866260',
       false,
       true,
       true,
       'abroad_only',
       true,
       false,
       false,
       'EU/1/21/1580',
       46,
       null,
       '2022-12-22 00:00:01.866260'
WHERE NOT EXISTS(SELECT id FROM vaccines_covid_19_names WHERE id = '23da4499-0d5b-4397-8d1e-fe614c26c614');