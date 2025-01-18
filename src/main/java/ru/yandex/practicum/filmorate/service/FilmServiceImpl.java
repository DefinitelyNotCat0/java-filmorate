package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.FilmStorage;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    public static final int MAX_AVAILABLE_DESCRIPTION_LENGTH = 200;
    public static final LocalDate EARLIEST_AVAILABLE_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);
    public static final Integer GET_FIRST_FILMS_LIMIT = 10;

    @Qualifier("filmRepository")
    private final FilmStorage filmStorage;
    @Qualifier("userRepository")
    private final UserStorage userStorage;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    @Override
    public Film getFilmById(Long id) {
        return filmStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Film not found with id = " + id));
    }

    @Override
    public Collection<Film> getFilms() {
        return filmStorage.getAll();
    }

    @Override
    public Film createFilm(Film film) {
        validate(film);
        return filmStorage.save(film);
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id must not be empty");
        }
        if (!filmStorage.exists(film.getId())) {
            throw new NotFoundException("Film not found with id = " + film.getId());
        }

        validate(film);
        return filmStorage.update(film);
    }

    @Override
    public void addLike(Long id, Long userId) {
        if (!filmStorage.exists(id)) {
            throw new NotFoundException("Film not found with id = " + id);
        }
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("User not found with id = " + userId);
        }

        filmStorage.addLike(id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        if (!filmStorage.exists(id)) {
            throw new NotFoundException("Film not found with id = " + id);
        }
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("User not found with id = " + userId);
        }

        filmStorage.removeLike(id, userId);
    }

    @Override
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
        if (!mpaRepository.exists(film.getMpa().getId())) {
            errors.add("Mpa not found with id = " + film.getMpa().getId());
        }

        validateGenres(film, errors);
        if (!errors.isEmpty()) {
            String errorMessage = StringUtils.arrayToDelimitedString(errors.toArray(), ";");
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
    }

    private void validateGenres(Film film, List<String> errors) {
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Integer> existingGenreIds = genreRepository.getAll().stream().map(Genre::getId).toList();
            List<Genre> nonExistingGenreIds = film.getGenres().stream()
                    .filter(genre -> !existingGenreIds.contains(genre.getId()))
                    .toList();

            if (!nonExistingGenreIds.isEmpty()) {
                errors.add("Genres not found with ids = " + nonExistingGenreIds);
            }
        }
    }
}
