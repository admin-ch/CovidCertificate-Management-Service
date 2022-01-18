ALTER TABLE kpi
    ADD COLUMN IF NOT EXISTS system_source VARCHAR(3);

UPDATE kpi
    SET system_source = 'UI';

ALTER TABLE kpi
    ADD COLUMN IF NOT EXISTS api_gateway_id VARCHAR(36);

