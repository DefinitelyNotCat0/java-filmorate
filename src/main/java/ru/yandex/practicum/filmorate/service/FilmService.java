package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmService {

    Film getFilmById(Long id);

    Collection<Film> getFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    List<Film> getMostPopularFilms(Integer limit);
}
