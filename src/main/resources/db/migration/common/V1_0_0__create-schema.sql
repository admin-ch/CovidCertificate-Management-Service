create table revocation
(
    id uuid not null primary key,
    uvci varchar(39) not null unique,
	creation_date_time timestamp not null default now()
);