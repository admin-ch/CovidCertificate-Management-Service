alter table signing_information
    add column if not exists slot_number integer not null default 0;