alter table kpi add column if not exists fraud boolean not null default false;
alter table revocation add column if not exists fraud boolean not null default false;