create table if not exists chat_message
(
    status    smallint
        constraint chat_message_status_check
            check ((status >= 0) AND (status <= 1)),
    id        bigint not null
        primary key,
    timestamp timestamp(6),
    content   varchar(255),
    receiver  varchar(255),
    sender    varchar(255)
);

alter table chat_message
    owner to postgres;

create table if not exists offers
(
    negotiable  boolean      not null,
    id          bigint       not null
        primary key,
    description varchar(255) not null,
    image_url   varchar(255) not null,
    title       varchar(255) not null
);

alter table offers
    owner to postgres;

create table if not exists job
(
    salary       double precision,
    id           bigint not null
        primary key
        constraint fki6enkf29hgx6m9ck3uo9hitay
            references offers,
    localization varchar(255)
);

alter table job
    owner to postgres;

create table if not exists roles
(
    id   integer      not null
        primary key,
    name varchar(255) not null
        unique
);

alter table roles
    owner to postgres;

create table if not exists service
(
    price double precision,
    id    bigint not null
        primary key
        constraint fk7yxo0qqc2mw0174gce6i18sag
            references offers
);

alter table service
    owner to postgres;

create table if not exists users
(
    created_at   timestamp(6),
    id           bigint       not null
        primary key,
    updated_at   timestamp(6),
    county       varchar(255) not null,
    district     varchar(255) not null,
    email        varchar(255) not null
        unique,
    name         varchar(255) not null,
    password     varchar(255) not null,
    phone_number varchar(255) not null,
    resume_url   varchar(255)
);

alter table users
    owner to postgres;

create table if not exists applications
(
    id       bigint not null
        primary key,
    offer_id bigint
        constraint fk1jq84e96g7lohehw7jb6kesoa
            references offers,
    user_id  bigint
        constraint fkfsfqljedcla632u568jl5qf3w
            references users
);

alter table applications
    owner to postgres;

create table if not exists favorite_users
(
    favorite_user_id bigint not null
        constraint fkn8ugixo672l43j5wj3j3t6j6c
            references users,
    user_id          bigint not null
        constraint fk50hs5f52ppppt206wbsmydr6b
            references users
);

alter table favorite_users
    owner to postgres;

create table if not exists favorites_offers
(
    offer_id bigint not null
        constraint fkf37pigkc2umre31ykgw0mo2ft
            references offers,
    user_id  bigint not null
        constraint fk6xt7wklc5gkb4dqpjmjmqj8ge
            references users
);

alter table favorites_offers
    owner to postgres;

create table if not exists users_roles
(
    role_id integer not null
        constraint fkj6m8fwv7oqv74fcehir1a9ffy
            references roles,
    user_id bigint  not null
        constraint fk2o0jvgh89lemvvo17cbqvdxaa
            references users,
    primary key (role_id, user_id)
);

alter table users_roles
    owner to postgres;