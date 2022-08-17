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