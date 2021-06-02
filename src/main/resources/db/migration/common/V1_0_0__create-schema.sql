create table revocation
(
    id uuid not null primary key,
    uvci varchar(39) not null unique,
	creation_date_time timestamptz not null default now()
);
