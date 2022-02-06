alter table account_limits
    add column failure_lost_transaction boolean default false;