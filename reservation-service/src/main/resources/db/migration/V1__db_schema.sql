
BEGIN;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE public.availabilities
(
    id varchar(50) NOT NULL,
    start_time timestamp,
    end_time timestamp,
    created_at timestamp,
    updated_at timestamp,
    version integer not null default 0,
    booked boolean default false,
    PRIMARY KEY (id)
);

CREATE TABLE public."user"
(
    id varchar(50) NOT NULL,
    email varchar(100) NULL,
    created_at timestamp,
    updated_at timestamp,
    version integer not null default 0,
    PRIMARY KEY (id)
);

CREATE TABLE public.reservations
(
    id varchar(50) NOT NULL,
    email varchar(100) NULL,
    title varchar(255) NULL,
    start_time timestamp,
    end_time timestamp,
    created_at timestamp,
    updated_at timestamp,
    version integer not null default 0,
    availability_id varchar(50) NOT NULL,

    PRIMARY KEY (id)
);

CREATE INDEX idx_reservations_availability_id
    ON reservations(availability_id);


END;
