UPDATE vaccines_covid_19_auth_holders
set code = 'ORG-100036422'
where id = 'ed9c2a60-4b8e-4b1c-9370-d4df32781057';

DELETE
FROM vaccines_covid_19_names
where id = 'bc0e71cf-bc52-45cb-b6f0-1dba39cf2a97';

UPDATE vaccines_covid_19_names
set vaccine_order = '47'
where id in ('8ac65a1d-00fa-4425-8f4f-cb866c4aa057', '8ac65a1d-00fa-4425-8f4f-cb866c4ab157');
