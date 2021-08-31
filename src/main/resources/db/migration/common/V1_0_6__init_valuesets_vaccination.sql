do $$

    declare sct_1119349007 uuid := gen_random_uuid ();
    declare sct_1119305005 uuid := gen_random_uuid ();
    declare sct_J07BX03 uuid := gen_random_uuid ();

    declare ah_ORG_100001699 uuid := gen_random_uuid ();
    declare ah_ORG_100030215 uuid := gen_random_uuid ();
    declare ah_ORG_100001417 uuid := gen_random_uuid ();
    declare ah_ORG_100031184 uuid := gen_random_uuid ();
    declare ah_ORG_100006270 uuid := gen_random_uuid ();
    declare ah_ORG_100013793 uuid := gen_random_uuid ();
    declare ah_ORG_100020693 uuid := gen_random_uuid ();
    declare ah_ORG_100010771 uuid := gen_random_uuid ();
    declare ah_ORG_100024420 uuid := gen_random_uuid ();
    declare ah_ORG_100032020 uuid := gen_random_uuid ();
    declare ah_Gamaleya_Research_Institute uuid := gen_random_uuid ();
    declare ah_Vector_Institute uuid := gen_random_uuid ();
    declare ah_Sinovac_Biotech uuid := gen_random_uuid ();
    declare ah_Bharat_Biotech uuid := gen_random_uuid ();
    declare ah_ORG_100001981 uuid := gen_random_uuid ();

    begin

        insert into sct_vaccines_covid_19 values (sct_1119349007, '1119349007', 'SARS-CoV-2 mRNA vaccine', true);
        insert into sct_vaccines_covid_19 values (sct_1119305005, '1119305005', 'SARS-CoV-2 antigen vaccine', true);
        insert into sct_vaccines_covid_19 values (sct_J07BX03, 'J07BX03', 'covid-19 vaccines', true);

        insert into vaccines_covid_19_auth_holders values (ah_ORG_100001699, 'ORG-100001699', 'AstraZeneca AB', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100030215, 'ORG-100030215', 'Biontech Manufacturing GmbH', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100001417, 'ORG-100001417', 'Janssen-Cilag International', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100031184, 'ORG-100031184', 'Moderna Biotech Spain S.L.', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100006270, 'ORG-100006270', 'Curevac AG', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100013793, 'ORG-100013793', 'CanSino Biologics', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100020693, 'ORG-100020693', 'China Sinopharm International Corp. - Beijing location', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100010771, 'ORG-100010771', 'Sinopharm Weiqida Europe Pharmaceutical s.r.o. - Prague location', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100024420, 'ORG-100024420', 'Sinopharm Zhijun (Shenzhen) Pharmaceutical Co. Ltd. - Shenzhen location', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100032020, 'ORG-100032020', 'Novavax CZ AS', true);
        insert into vaccines_covid_19_auth_holders values (ah_Gamaleya_Research_Institute, 'Gamaleya-Research-Institute', 'Gamaleya Research Institute', true);
        insert into vaccines_covid_19_auth_holders values (ah_Vector_Institute, 'Vector-Institute', 'Vector Institute', true);
        insert into vaccines_covid_19_auth_holders values (ah_Sinovac_Biotech, 'Sinovac-Biotech', 'Sinovac Biotech', true);
        insert into vaccines_covid_19_auth_holders values (ah_Bharat_Biotech, 'Bharat-Biotech', 'Bharat Biotech', true);
        insert into vaccines_covid_19_auth_holders values (ah_ORG_100001981, 'ORG-100001981', 'Serum Institute Of India Private Limited', true);

        insert into vaccines_covid_19_names values (gen_random_uuid (), 'EU/1/20/1528', 'Comirnaty', sct_1119349007, ah_ORG_100030215, true, true);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'EU/1/20/1507', 'Spikevax (previously COVID-19 Vaccine Moderna)', sct_1119349007, ah_ORG_100001417, true, true);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'EU/1/21/1529', 'Vaxzevria', sct_J07BX03, ah_ORG_100001699, true, false);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'EU/1/20/1525', 'COVID-19 Vaccine Janssen', sct_J07BX03, ah_ORG_100001417, true, true);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'CVnCoV', 'CVnCoV', sct_1119349007, ah_ORG_100006270, true, false);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'Sputnik-V', 'Sputnik-V', sct_J07BX03, ah_Gamaleya_Research_Institute, true, false);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'Convidecia', 'Convidecia', sct_J07BX03, ah_ORG_100013793, true, false);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'EpiVacCorona', 'EpiVacCorona', TODO, ah_Vector_Institute, true, false);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'BBIBP-CorV', 'BBIBP-CorV', sct_J07BX03, ah_ORG_100020693, true, false);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'Inactivated-SARS-CoV-2-Vero-Cell', 'Inactivated SARS-CoV-2 (Vero Cell)', TODO, TODO, true, TODO);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'CoronaVac', 'CoronaVac', sct_J07BX03, ah_Sinovac_Biotech, true, false);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'Covaxin', 'Covaxin (also known as BBV152 A, B, C)', TODO, ah_Bharat_Biotech, true, false);
        insert into vaccines_covid_19_names values (gen_random_uuid (), 'Covishield', 'Covishield (ChAdOx1_nCoV-19)', sct_J07BX03, TODO, true, false);

    end $$;
