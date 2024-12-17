package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    void setUp() {
        filmController = new FilmController();

        Film film = new Film(null, "Name",
                "-".repeat(FilmController.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmController.EARLIEST_AVAILABLE_RELEASE_DATE
                        .plusYears(10)
                        .plusMonths(5)
                        .plusDays(10),
                90);
        filmController.createFilm(film);
    }

    @Test
    void createUpdateAndGetFilms() {
        assertEquals(1, filmController.getFilms().size());
        Film firstFilm = new Film(null, "Name 1",
                "1".repeat(FilmController.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmController.EARLIEST_AVAILABLE_RELEASE_DATE.plusYears(20).plusDays(10),
                5);
        filmController.createFilm(firstFilm);
        assertEquals(2, filmController.getFilms().size());
        Film firstCreatedFilm = filmController.getFilms().stream().skip(1).findFirst().orElse(new Film());
        assertEquals(firstFilm, firstCreatedFilm);
        assertEquals(2, firstCreatedFilm.getId());

        Film secondFilm = new Film(null, "Name 2",
                "2".repeat(FilmController.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmController.EARLIEST_AVAILABLE_RELEASE_DATE.plusYears(25).plusMonths(6),
                28);
        filmController.createFilm(secondFilm);
        assertEquals(3, filmController.getFilms().size());
        Film secondCreatedFilm = filmController.getFilms().stream().skip(2).findFirst().orElse(new Film());
        assertEquals(secondFilm, secondCreatedFilm);
        assertEquals(3, secondCreatedFilm.getId());

        Film firstFilmUpdate = new Film(firstCreatedFilm.getId(), "Name 1 updated",
                "u".repeat(FilmController.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                firstCreatedFilm.getReleaseDate().plusMonths(2),
                8);
        filmController.updateFilm(firstFilmUpdate);
        assertEquals(3, filmController.getFilms().size());
        Film firstFilmUpdateGet = filmController.getFilms().stream().skip(1).findFirst().orElse(new Film());
        assertEquals(firstFilmUpdate, firstFilmUpdateGet);
        assertEquals(2, firstFilmUpdateGet.getId());

        Film secondFilmUpdate = new Film(
                (long) (filmController.getFilms().size() + 1),
                "Name 2 updated",
                "u".repeat(FilmController.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                secondCreatedFilm.getReleaseDate().plusMonths(2),
                8);
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(secondFilmUpdate));
        secondFilmUpdate.setId(null);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(secondFilmUpdate));
    }

    @Test
    void validationTest() {
        Film film = new Film(null, "Name 1",
                "-".repeat(FilmController.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmController.EARLIEST_AVAILABLE_RELEASE_DATE.plusYears(20).plusDays(10),
                5);
        assertDoesNotThrow(() -> filmController.createFilm(film));

        // Release Date
        Film filmEarliestAvailableReleaseDateEqual = new Film(null, film.getName(), film.getDescription(),
                FilmController.EARLIEST_AVAILABLE_RELEASE_DATE,
                film.getDuration());
        assertDoesNotThrow(() -> filmController.createFilm(filmEarliestAvailableReleaseDateEqual));
        filmEarliestAvailableReleaseDateEqual.setId(1L);
        assertDoesNotThrow(() -> filmController.updateFilm(filmEarliestAvailableReleaseDateEqual));

        Film filmEarliestAvailableReleaseDateMore = new Film(null, film.getName(), film.getDescription(),
                FilmController.EARLIEST_AVAILABLE_RELEASE_DATE.plusDays(1),
                film.getDuration());
        assertDoesNotThrow(() -> filmController.createFilm(filmEarliestAvailableReleaseDateMore));
        filmEarliestAvailableReleaseDateMore.setId(1L);
        assertDoesNotThrow(() -> filmController.updateFilm(filmEarliestAvailableReleaseDateMore));

        Film filmEarliestAvailableReleaseDateLess = new Film(null, film.getName(), film.getDescription(),
                FilmController.EARLIEST_AVAILABLE_RELEASE_DATE.minusDays(1),
                film.getDuration());
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmEarliestAvailableReleaseDateLess));
        filmEarliestAvailableReleaseDateLess.setId(1L);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(filmEarliestAvailableReleaseDateLess));
    }
}