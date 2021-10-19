ALTER TABLE signing_information ADD COLUMN IF NOT EXISTS certificate_alias VARCHAR(16);
ALTER TABLE signing_information ADD COLUMN IF NOT EXISTS valid_from date not null default now();
ALTER TABLE signing_information ADD COLUMN IF NOT EXISTS valid_to date not null default '2999-12-31';