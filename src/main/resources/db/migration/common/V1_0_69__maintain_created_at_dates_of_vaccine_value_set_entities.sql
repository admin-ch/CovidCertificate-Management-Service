update vaccines_covid_19_names vn1 set created_at = (select modified_at from vaccines_covid_19_names vn2 where vn1.id = vn2.id);

update vaccines_covid_19_auth_holders ah1 set created_at = (select modified_at from vaccines_covid_19_names ah2 where ah1.id = ah2.id);

update sct_vaccines_covid_19 sct1 set created_at = (select modified_at from vaccines_covid_19_names sct2 where sct1.id = sct2.id);
