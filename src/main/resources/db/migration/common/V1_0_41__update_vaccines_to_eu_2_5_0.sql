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
