update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'ORG-100000788'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'Vidprevtyn';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'Finlay-Institute'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'Soberana-Plus';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'Vector-Institute'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'EpiVacCorona-N';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'Gamaleya-Research-Institute'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'Sputnik-M';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'Instituto-Butantan'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'Covid-19-adsorvida-inativada';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'NVSI'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'NVSI-06-08';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'Yisheng-Biopharma'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'YS-SC2-010';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'ORG-100026614'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'SCTV01C';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'ORG-100008549'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'Covifenz';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'ORG-100001699'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'AZD2816';

update vaccines_covid_19_names
set auth_holder = (select id from vaccines_covid_19_auth_holders where code = 'Finlay-Institute'),
    prophylaxis = (select id from sct_vaccines_covid_19 where code = 'J07BX03')
where code = 'Soberana-02';
