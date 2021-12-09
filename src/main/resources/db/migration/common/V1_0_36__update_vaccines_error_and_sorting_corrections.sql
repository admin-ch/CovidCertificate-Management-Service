update vaccines_covid_19_names
set api_gateway_selectable = false
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e12fdf0',
             '2dc3feef-95d8-4e6f-930c-69300e12faf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fbf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fcf0',
             '2dc3feef-95d8-4e6f-930c-69300e12f8f0',
             '2dc3feef-95d8-4e6f-930c-69300e12f9f0'
    );

update vaccines_covid_19_names
set api_platform_selectable = false
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e12fdf0',
             '2dc3feef-95d8-4e6f-930c-69300e12faf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fbf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fcf0',
             '2dc3feef-95d8-4e6f-930c-69300e12f8f0',
             '2dc3feef-95d8-4e6f-930c-69300e12f9f0'
    );

update vaccines_covid_19_names
set vaccine_order = 55
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e11f7f0',
             '2dc3feef-95d8-4e6f-930c-69300e11e6f0'
    );

update vaccines_covid_19_names
set vaccine_order = 57
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e12f7f0',
             '2dc3feef-95d8-4e6f-930c-69300e12e6f0'
    );

update vaccines_covid_19_names
set display = 'Vaxzevria'
where id = '8ac65a1d-00fa-4425-8f3f-cb866c4aa053';

update vaccines_covid_19_names
set vaccine_order = 200
where id in (
             '2dc3feef-95d8-4e6f-930c-69300e12fdf0',
             '2dc3feef-95d8-4e6f-930c-69300e12f9f0',
             '2dc3feef-95d8-4e6f-930c-69300e12faf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fbf0',
             '2dc3feef-95d8-4e6f-930c-69300e12fcf0',
             '2dc3feef-95d8-4e6f-930c-69300e12f8f0'
    );

update vaccines_covid_19_names
set vaccine_order = 80
where id in (
             'fe39b8af-eccd-45de-acd7-ce7206052b02',
             'fe39b8af-eccd-45de-acd7-ce7206052b00'
    );
