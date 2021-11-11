INSERT INTO public.vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, modified_at,
                                            swiss_medic, emea, who_eul, issuable, web_ui_selectable,
                                            api_gateway_selectable, api_platform_selectable, code, vaccine_order)
VALUES ('fe39b8af-eccd-45de-acd7-ce7206052b02', 'Covaxin (also known as BBV152 A, B, C)',
        '1ee3559a-ed64-4062-a10e-baeded624af0', '8f910905-1c14-4f92-befd-6174c1aba905', true, true,
        '2021-11-11 11:11:11.111111', false, false, true, 'abroad_only', true, false, false, 'Covaxin', 200)
ON CONFLICT (id) DO NOTHING;

update vaccines_covid_19_names
set issuable                = 'ch_and_abroad'
  , who_eul                 = true
  , web_ui_selectable       = false
  , api_gateway_selectable  = true
  , api_platform_selectable = true
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b00';

update vaccines_covid_19_names
set prophylaxis = '1ee3559a-ed64-4062-a10e-baeded624ae8'
  , auth_holder = '279ad11e-f040-442d-8e97-1262423e3e0b'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b02';

update vaccines_covid_19_names
set prophylaxis = '1ee3559a-ed64-4062-a10e-baeded624ae8'
  , auth_holder = '279ad11e-f040-442d-8e97-1262423e3e0b'
where id = 'fe39b8af-eccd-45de-acd7-ce7206052b00';
