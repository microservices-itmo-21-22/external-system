create table project
(
    id   uuid primary key,
    name text
);

create table account
(
    id            uuid primary key,
    name          text,
    client_secret uuid,
    callback_url  text,
    project_id    uuid,
    answer_method text,

    constraint project_fk foreign key (project_id) references project (id) on delete cascade on update cascade
);
