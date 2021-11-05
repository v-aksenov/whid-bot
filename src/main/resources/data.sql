create table user_task
(
    id            identity PRIMARY KEY,
    created       timestamp not null default now(),
    spent_minutes int       not null default 0,
    message       varchar(200),
    telegram_id   varchar(50) not null
);