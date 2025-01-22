package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    boolean exists(Long id);

    Optional<Film> getById(Long id);

    Collection<Film> getAll();

    Film save(Film film);

    Film update(Film film);

    void addLike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    List<Film> getMostPopularFilms(Integer limit);
}
