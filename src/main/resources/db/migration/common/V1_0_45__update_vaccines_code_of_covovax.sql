update vaccines_covid_19_names
set code = 'Covovax'
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e33e6f0',
             '2dc3feef-95d8-4e6f-930c-69300e33f7f0'
    );

update vaccines_covid_19_names
set display = 'BBIBP-CorV / Covilo'
where id in (
             '4109a8fa-84b2-4a3f-bf33-6c746a79a11d',
             '4109a8fa-84b2-4a3f-bf33-6c746a79b21d'
    );
