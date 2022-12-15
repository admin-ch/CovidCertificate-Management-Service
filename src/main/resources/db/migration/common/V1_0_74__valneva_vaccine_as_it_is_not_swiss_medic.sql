INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, modified_at, swiss_medic,
                                     emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine, created_at)
VALUES ('8ac65a1d-00fa-4425-8f4f-cb866c4ab267', 'VLA2001 (deprecated)', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'ed9c2a60-4b8e-4b1c-9370-d4df32781057', false, '2022-11-29 02:00:00.868355', false, true, true, 'ch_and_abroad',
        false, true, true, 'VLA2001', 47, null, '2022-11-29 02:00:00.000000');

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, modified_at, swiss_medic,
                                     emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable,
                                     api_platform_selectable, code, vaccine_order, analog_vaccine, created_at)
VALUES ('8ac65a1d-00fa-4425-8f4f-cb866c4ab378', 'COVID-19 Vaccine Valneva', '1ee3559a-ed64-4062-a10e-baeded624ae8',
        'ed9c2a60-4b8e-4b1c-9370-d4df32781057', true, '2022-11-29 02:00:00.900598', false, true, true, 'ch_and_abroad',
        false, true, true, 'EU/1/21/1624', 47, null, '2022-11-29 02:00:00.000000');

UPDATE vaccines_covid_19_names
SET issuable                = 'abroad_only',
    api_gateway_selectable  = false,
    api_platform_selectable = false,
    swiss_medic             = false
WHERE id in ('8ac65a1d-00fa-4425-8f4f-cb866c4ab157', '8ac65a1d-00fa-4425-8f4f-cb866c4ab268');
