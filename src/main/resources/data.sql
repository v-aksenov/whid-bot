create schema if not exists task_bot;

create table if not exists task_bot.user_task
(
    id            identity PRIMARY KEY,
    created       timestamp,
    spent_minutes int,
    message       varchar(200),
    telegram_id   varchar(50)
);