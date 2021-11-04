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
