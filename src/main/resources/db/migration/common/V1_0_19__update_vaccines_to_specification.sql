-- Comirnaty
update vaccines_covid_19_names
set issuable                = 'ch_and_abroad',
    web_ui_selectable       = true,
    api_gateway_selectable  = false,
    api_platform_selectable = false
where id = '0af1a070-2647-4acb-a1be-4df81b47840b';

-- Spikevax (previously COVID-19 Vaccine Moderna)
update vaccines_covid_19_names
set issuable                = 'ch_and_abroad',
    web_ui_selectable       = true,
    api_gateway_selectable  = false,
    api_platform_selectable = false
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6ee';

-- COVID-19 Vaccine Janssen
update vaccines_covid_19_names
set issuable                = 'ch_and_abroad',
    web_ui_selectable       = true,
    api_gateway_selectable  = false,
    api_platform_selectable = false
where id = 'ce2aad87-0d0b-4c5d-8a79-e15576020c05';

-- Vaxzevria (AstraZeneca AB)
update vaccines_covid_19_names
set issuable                = 'abroad_only',
    web_ui_selectable       = true,
    api_platform_selectable = true
where id = '8ac65a1d-00fa-4425-8f3f-cb866c4aa053';

-- Covishield
update vaccines_covid_19_names
set issuable                = 'abroad_only',
    web_ui_selectable       = true,
    api_platform_selectable = true
where id = '2dc3feef-95d8-4e6f-930c-69300e10e6f0';

-- BBIBP-CorV (China Sinopharm International Corp. - Beijing location)
update vaccines_covid_19_names
set issuable          = 'abroad_only',
    web_ui_selectable = true
where id = '4109a8fa-84b2-4a3f-bf33-6c746a79a11d';

-- CoronaVac (Sinovac Biotech)
update vaccines_covid_19_names
set issuable          = 'abroad_only',
    web_ui_selectable = true
where id = 'fe39b8af-eccd-45de-acd7-ce7206052af7';
