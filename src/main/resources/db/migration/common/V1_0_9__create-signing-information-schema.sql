create table signing_information
(
    id uuid not null primary key,
    certificate_type varchar (50) not null,
    code varchar(50),
    alias varchar(50) not null,
    key_identifier varchar(16) not null,
    CONSTRAINT uq_signing_information_certificate_type_code UNIQUE (certificate_type, code)
);

CREATE INDEX idx_signing_information_certificate_type_code ON signing_information (certificate_type, code);
