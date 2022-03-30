create table transaction
(
    id uuid primary key,
    account_id uuid,
    submit_time bigint,
    completed_time bigint,
    status text,

    constraint account_fk foreign key (account_id) references account (id) on delete no action on update cascade
)