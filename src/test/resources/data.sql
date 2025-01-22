insert into users(email, login, name, birthday)
values ('test1@gmail.com', 'test1_login', 'test1', '2000-09-09');

insert into users(email, login, name, birthday)
values ('test2@gmail.com', 'test2_login', 'test2', '1999-09-09');

insert into users(email, login, name, birthday)
values ('test3@gmail.com', 'test3_login', 'test3', '1998-09-09');

insert into films(name, description, release_date, duration, mpa_id)
values ('film1', 'desc1', '2001-04-01', 30, 1);

insert into films_genres(film_id, genre_id)
values (1, 2), (1, 4);

insert into films(name, description, release_date, duration, mpa_id)
values ('film2', 'desc2', '1982-01-12', 20, 2);

insert into films_genres(film_id, genre_id)
values (2, 1);