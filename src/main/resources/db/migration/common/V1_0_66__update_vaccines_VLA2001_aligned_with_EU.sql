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
