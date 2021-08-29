create table account_limits
(
    id                  uuid primary key,
    accept_transactions boolean,
    enable_response_time_variation boolean,
    response_time_lower_bound bigint,
    response_time_upper_bound bigint,
    enable_failures boolean,
    failure_probability double precision,
    enable_rate_limits boolean,
    requests_per_second bigint,
    requests_per_minute bigint,
    requests_per_hour bigint,
    requests_per_day bigint,
    enable_server_errors boolean,
    server_error_probability double precision
);

alter table account
    add column account_limits_id uuid;

alter table account
    add constraint account_limits_id_fk foreign key (account_limits_id) references account_limits (id);