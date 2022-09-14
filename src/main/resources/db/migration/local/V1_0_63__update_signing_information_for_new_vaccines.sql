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
