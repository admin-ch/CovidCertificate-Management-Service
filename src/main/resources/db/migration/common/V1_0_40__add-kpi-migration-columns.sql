ALTER TABLE kpi
    ADD COLUMN IF NOT EXISTS system_source VARCHAR(10);

UPDATE kpi
    SET system_source = 'UI'
    WHERE system_source is null;

ALTER TABLE kpi
    ADD COLUMN IF NOT EXISTS original_id VARCHAR(36);

