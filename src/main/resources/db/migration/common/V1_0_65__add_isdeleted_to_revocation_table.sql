alter table revocation
    add column if not exists deleted_date_time timestamp;