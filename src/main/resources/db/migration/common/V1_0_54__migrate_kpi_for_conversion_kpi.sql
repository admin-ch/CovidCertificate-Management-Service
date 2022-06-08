alter table kpi
    drop column if exists fraud;

alter table kpi
    add column if not exists origin_uvci varchar(39);

alter table kpi
    add column if not exists conversion_reason varchar(64);
