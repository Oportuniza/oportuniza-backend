create table chat_message
(
    id        bigint not null
        primary key,
    content   varchar(255),
    receiver  varchar(255),
    sender    varchar(255),
    status    smallint
        constraint chat_message_status_check
            check ((status >= 0) AND (status <= 1)),
    timestamp timestamp(6)
);

alter table chat_message
    owner to postgres;

create table chat_notification
(
    id     bigint not null
        primary key,
    sender varchar(255)
);

alter table chat_notification
    owner to postgres;

create table general_notification
(
    id          bigint not null
        primary key,
    message     varchar(255),
    target_user varchar(255)
);

alter table general_notification
    owner to postgres;

create table roles
(
    id   integer      not null
        primary key,
    name varchar(255) not null
        constraint ukofx66keruapi6vyqpv6f2or37
            unique
);

alter table roles
    owner to postgres;

create table users
(
    id             bigint       not null
        primary key,
    average_rating double precision,
    county         varchar(255),
    created_at     timestamp(6),
    district       varchar(255),
    email          varchar(255) not null
        constraint uk6dotkott2kjsp8vw4d0m25fb7
            unique,
    name           varchar(255) not null,
    password       varchar(255) not null,
    phone_number   varchar(255) not null,
    resume_url     varchar(255),
    review_count   integer      not null,
    updated_at     timestamp(6)
);

alter table users
    owner to postgres;

create table favorite_users
(
    user_id          bigint not null
        constraint fk50hs5f52ppppt206wbsmydr6b
            references users,
    favorite_user_id bigint not null
        constraint fkn8ugixo672l43j5wj3j3t6j6c
            references users
);

alter table favorite_users
    owner to postgres;

create table offers
(
    id          bigint       not null
        primary key,
    description varchar(255) not null,
    image_url   varchar(255),
    negotiable  boolean      not null,
    title       varchar(255) not null,
    user_id     bigint
        constraint fk9yilcimbeupq2lyrqr1nlrjyb
            references users
);

alter table offers
    owner to postgres;

create table applications
(
    id         bigint not null
        primary key,
    email      varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    message    varchar(255),
    resume_url varchar(255),
    status     varchar(255),
    offer_id   bigint
        constraint fk1jq84e96g7lohehw7jb6kesoa
            references offers,
    user_id    bigint
        constraint fkfsfqljedcla632u568jl5qf3w
            references users
);

alter table applications
    owner to postgres;

create table documents
(
    id             bigint not null
        primary key,
    url            varchar(255),
    application_id bigint not null
        constraint fk8umh06sslm8f0rbfasqk6yy0f
            references applications
);

alter table documents
    owner to postgres;

create table favorites_offers
(
    user_id  bigint not null
        constraint fk6xt7wklc5gkb4dqpjmjmqj8ge
            references users,
    offer_id bigint not null
        constraint fkf37pigkc2umre31ykgw0mo2ft
            references offers
);

alter table favorites_offers
    owner to postgres;

create table job
(
    localization   varchar(255),
    salary         double precision,
    working_model  varchar(255),
    working_regime varchar(255),
    id             bigint not null
        primary key
        constraint fki6enkf29hgx6m9ck3uo9hitay
            references offers
);

alter table job
    owner to postgres;

create table reviews
(
    id          bigint generated by default as identity
        primary key,
    rating      integer not null,
    reviewed_id bigint  not null
        constraint fk2fbmducna9wit1mcfn18y71md
            references users,
    reviewer_id bigint  not null
        constraint fkd1isgfajhtdl8mgg29up6mofi
            references users
);

alter table reviews
    owner to postgres;

create table service
(
    price double precision,
    id    bigint not null
        primary key
        constraint fk7yxo0qqc2mw0174gce6i18sag
            references offers
);

alter table service
    owner to postgres;

create table users_roles
(
    user_id bigint  not null
        constraint fk2o0jvgh89lemvvo17cbqvdxaa
            references users,
    role_id integer not null
        constraint fkj6m8fwv7oqv74fcehir1a9ffy
            references roles,
    primary key (user_id, role_id)
);

alter table users_roles
    owner to postgres;
