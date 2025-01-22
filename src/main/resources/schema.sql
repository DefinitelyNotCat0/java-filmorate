create table if not exists mpa (
    id                  integer generated by default as identity primary key,
    name                varchar(60) not null
);

create table if not exists genres (
    id                  integer generated by default as identity primary key,
    name                varchar(60) not null
);

create table if not exists films (
    id                  bigint generated by default as identity primary key,
    name                varchar(150) not null,
    description         varchar(200) default null,
    release_date        date,
    duration            integer,
    mpa_id              integer not null,
    foreign key (mpa_id) references mpa(id)
);

create table if not exists users (
    id                  bigint generated by default as identity primary key,
    email               varchar(50) not null,
    login               varchar(20) not null,
    name                varchar(20),
    birthday            date
);

create table if not exists films_genres (
    film_id             bigint not null,
    genre_id            integer not null,
    foreign key (film_id) references films(id),
    foreign key (genre_id) references genres(id)
);

create table if not exists films_likes (
    film_id             bigint not null,
    user_id             bigint not null,
    foreign key (film_id) references films(id),
    foreign key (user_id) references users(id)
);

create table if not exists users_friends (
    user_id             bigint not null,
    friend_id           bigint not null,
    foreign key (user_id) references users(id),
    foreign key (friend_id) references users(id)
);
