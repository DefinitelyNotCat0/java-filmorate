package ru.yandex.practicum.filmorate.service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    public static final int MAX_AVAILABLE_DESCRIPTION_LENGTH = 200;
    public static final LocalDate EARLIEST_AVAILABLE_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    public static final Integer GET_FIRST_FILMS_LIMIT = 10;

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with id = " + id));
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film createFilm(Film film) {
        validate(film);
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id must not be empty");
        }
        if (!filmStorage.doesFilmExist(film.getId())) {
            throw new NotFoundException("Film not found with id = " + film.getId());
        }

        validate(film);
        return filmStorage.updateFilm(film);
    }

    public void addLike(Long id, Long userId) {
        if (!filmStorage.doesFilmExist(id)) {
            throw new NotFoundException("Film not found with id = " + id);
        }
        if (!userStorage.doesUserExist(userId)) {
            throw new NotFoundException("User not found with id = " + userId);
        }

        filmStorage.addLike(id, userId);
    }

    public void removeLike(Long id, Long userId) {
        if (!filmStorage.doesFilmExist(id)) {
            throw new NotFoundException("Film not found with id = " + id);
        }
        if (!userStorage.doesUserExist(userId)) {
            throw new NotFoundException("User not found with id = " + userId);
        }

        filmStorage.removeLike(id, userId);
    }

    public List<Film> getMostPopularFilms(Integer limit) {
        if (limit == null) {
            limit = GET_FIRST_FILMS_LIMIT;
        }
        if (limit <= 0) {
            throw new ValidationException("Limit must be empty or greater than zero");
        }

        return filmStorage.getMostPopularFilms(limit);
    }

    private void validate(Film film) {
        List<String> errors = new ArrayList<>();
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(EARLIEST_AVAILABLE_RELEASE_DATE)) {
            errors.add("Дата релиза не может быть раньше " + EARLIEST_AVAILABLE_RELEASE_DATE);
        }

        if (!errors.isEmpty()) {
            String errorMessage = StringUtils.arrayToDelimitedString(errors.toArray(), ";");
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }
}
