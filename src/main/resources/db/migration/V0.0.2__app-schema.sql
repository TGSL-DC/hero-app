CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create table if not exists kudos
(
    id          uuid primary key DEFAULT uuid_generate_v4(),
    channel     text      not null,
    username    text      not null,
    message     text      not null,
    create_date TIMESTAMP not null
);

drop table hero;
