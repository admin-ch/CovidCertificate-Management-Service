insert into display_name_modification ( id
                                      , code
                                      , display
                                      , entity_type)
values ( uuid_in(md5(random()::text || clock_timestamp()::text)::cstring)
       , 'BBIBP-CorV'
       , 'BBIBP-CorV / Covilo'
       , 'Vaccine');

insert into display_name_modification ( id
                                      , code
                                      , display
                                      , entity_type)
values ( uuid_in(md5(random()::text || clock_timestamp()::text)::cstring)
       , 'Covaxin'
       , 'Covaxin (also known as BBV152 A, B, C)'
       , 'Vaccine');

insert into display_name_modification ( id
                                      , code
                                      , display
                                      , entity_type)
values ( uuid_in(md5(random()::text || clock_timestamp()::text)::cstring)
       , 'Covishield'
       , 'Covishield (ChAdOx1_nCoV-19)'
       , 'Vaccine');

insert into display_name_modification ( id
                                      , code
                                      , display
                                      , entity_type)
values ( uuid_in(md5(random()::text || clock_timestamp()::text)::cstring)
       , 'Covovax'
       , 'COVOVAX (Novavax formulation)'
       , 'Vaccine');

insert into display_name_modification ( id
                                      , code
                                      , display
                                      , entity_type)
values ( uuid_in(md5(random()::text || clock_timestamp()::text)::cstring)
       , 'EU/1/20/1525'
       , 'COVID-19 Vaccine Janssen'
       , 'Vaccine');
