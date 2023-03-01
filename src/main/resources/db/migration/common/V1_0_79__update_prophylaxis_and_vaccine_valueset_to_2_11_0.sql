INSERT INTO sct_vaccines_covid_19 (id, code, display, active, modified_at, created_at)
SELECT '92ce28fa-d3d7-42d7-914c-5e680b9c94cf',
       '1187593009',
       'SARS-CoV-2 DNA plasmid encoding spike protein vaccine',
       true,
       '2023-02-02 00:00:02.115316',
       '2023-02-02 00:00:02.115316'
WHERE NOT EXISTS(SELECT id FROM sct_vaccines_covid_19 WHERE id = '92ce28fa-d3d7-42d7-914c-5e680b9c94cf');

INSERT INTO sct_vaccines_covid_19 (id, code, display, active, modified_at, created_at)
SELECT '808fc9e7-aced-446d-ba83-3d6aa4a35eda',
       '30141000087107',
       'SARS-CoV-2 virus-like particle antigen vaccine',
       true,
       '2023-02-02 00:00:02.111563',
       '2023-02-02 00:00:02.111563'
WHERE NOT EXISTS(SELECT id FROM sct_vaccines_covid_19 WHERE id = '808fc9e7-aced-446d-ba83-3d6aa4a35eda');

INSERT INTO sct_vaccines_covid_19 (id, code, display, active, modified_at, created_at)
SELECT 'f1fa77fb-1f50-4da5-b222-4c445ee2ca8e',
       '28531000087107',
       'COVID-19 vaccine',
       true,
       '2023-02-02 00:00:02.107582',
       '2023-02-02 00:00:02.107582'
WHERE NOT EXISTS(SELECT id FROM sct_vaccines_covid_19 WHERE id = 'f1fa77fb-1f50-4da5-b222-4c445ee2ca8e');

UPDATE sct_vaccines_covid_19
set display     = 'covid-19 vaccines (deprecated)',
    active      = false,
    modified_at = '2023-02-02 00:00:02.093270'
where id = '1ee3559a-ed64-4062-a10e-baeded624ae8';

UPDATE vaccines_covid_19_names
set prophylaxis = 'f1fa77fb-1f50-4da5-b222-4c445ee2ca8e',
    modified_at = '2023-02-02 00:00:02.093270'
where prophylaxis = '1ee3559a-ed64-4062-a10e-baeded624ae8';
