update vaccines_covid_19_names
set swiss_medic = true,
    web_ui_selectable = true,
    who_eul = true
where id = '2dc3feef-95d8-4e6f-930c-69300e22e7f0';

delete
from vaccines_covid_19_names
where id = '2dc3feef-95d8-4e6f-930c-69300e22e6f0';