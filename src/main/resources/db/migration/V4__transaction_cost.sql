alter table account
    add column transaction_cost bigint;

alter table account_limits
    drop column requests_per_day;

alter table account_limits
    drop column requests_per_second;

alter table account_limits
    drop column requests_per_hour;

alter table transaction
    add column cost bigint;

alter table account_limits
    add column parallel_requests bigint;