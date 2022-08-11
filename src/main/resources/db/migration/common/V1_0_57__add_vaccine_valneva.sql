INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable, api_platform_selectable, code, vaccine_order)
VALUES ('3ec674ac-c003-4518-96eb-9d3d9a7dc841', 'VLA2001', '1ee3559a-ed64-4062-a10e-baeded624ae8', '14f4b340-59c2-483f-b6c3-74199f446ee2', true, true, false, true, true, 'abroad_only', true, false, false, 'EU/1/21/1624/001', 51);

INSERT INTO vaccines_covid_19_names (id, display, prophylaxis, auth_holder, active, ch_issuable, swiss_medic, emea, who_eul, issuable, web_ui_selectable, api_gateway_selectable, api_platform_selectable, code, vaccine_order)
VALUES ('60580bef-e0e2-4207-98af-5e58005691e0', 'VLA2001', '1ee3559a-ed64-4062-a10e-baeded624ae8', '14f4b340-59c2-483f-b6c3-74199f446ee2', true, true, false, true, true, 'ch_and_abroad', false, true, true, 'EU/1/21/1624/001', 51);

-- TODO: Add certificate_alias when ready
INSERT INTO signing_information (id, certificate_type, code, alias, certificate_alias, valid_from, valid_to, slot_number)
VALUES ('8e4bb6f0-615a-4017-add7-6489926cc36c', 'vaccination', 'EU/1/21/1624/001', 'PRV_CCEU1201529_2109', 'TODO', '2022-08-21', '2999-12-31', 0)
