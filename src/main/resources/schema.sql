-- Database Schema

CREATE TABLE public.machine
(
    id             BIGSERIAL                   NOT NULL,
    name           CHARACTER VARYING(32)       NOT NULL,
    last_startup   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    last_update    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    running_status CHARACTER VARYING(10)       NOT NULL
);
