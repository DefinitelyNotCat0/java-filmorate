package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private FilmController filmController;
    private UserService userService;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmServiceImpl(filmStorage, userStorage);
        filmController = new FilmController(filmService);
        userService = new UserServiceImpl(userStorage);

        Film film = new Film(null, "Name",
                "-".repeat(FilmServiceImpl.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmServiceImpl.EARLIEST_AVAILABLE_RELEASE_DATE
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
                "1".repeat(FilmServiceImpl.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmServiceImpl.EARLIEST_AVAILABLE_RELEASE_DATE.plusYears(20).plusDays(10),
                5);
        filmController.createFilm(firstFilm);
        assertEquals(2, filmController.getFilms().size());
        Film firstCreatedFilm = filmController.getFilms().stream().skip(1).findFirst().orElse(new Film());
        assertEquals(firstFilm, firstCreatedFilm);
        assertEquals(2, firstCreatedFilm.getId());

        Film secondFilm = new Film(null, "Name 2",
                "2".repeat(FilmServiceImpl.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmServiceImpl.EARLIEST_AVAILABLE_RELEASE_DATE.plusYears(25).plusMonths(6),
                28);
        filmController.createFilm(secondFilm);
        assertEquals(3, filmController.getFilms().size());
        Film secondCreatedFilm = filmController.getFilms().stream().skip(2).findFirst().orElse(new Film());
        assertEquals(secondFilm, secondCreatedFilm);
        assertEquals(3, secondCreatedFilm.getId());

        Film firstFilmUpdate = new Film(firstCreatedFilm.getId(), "Name 1 updated",
                "u".repeat(FilmServiceImpl.MAX_AVAILABLE_DESCRIPTION_LENGTH),
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
                "u".repeat(FilmServiceImpl.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                secondCreatedFilm.getReleaseDate().plusMonths(2),
                8);
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(secondFilmUpdate));
        secondFilmUpdate.setId(null);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(secondFilmUpdate));
    }

    @Test
    void validationTest() {
        Film film = new Film(null, "Name 1",
                "-".repeat(FilmServiceImpl.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmServiceImpl.EARLIEST_AVAILABLE_RELEASE_DATE.plusYears(20).plusDays(10),
                5);
        assertDoesNotThrow(() -> filmController.createFilm(film));

        // Release Date
        Film filmEarliestAvailableReleaseDateEqual = new Film(null, film.getName(), film.getDescription(),
                FilmServiceImpl.EARLIEST_AVAILABLE_RELEASE_DATE,
                film.getDuration());
        assertDoesNotThrow(() -> filmController.createFilm(filmEarliestAvailableReleaseDateEqual));
        filmEarliestAvailableReleaseDateEqual.setId(1L);
        assertDoesNotThrow(() -> filmController.updateFilm(filmEarliestAvailableReleaseDateEqual));

        Film filmEarliestAvailableReleaseDateMore = new Film(null, film.getName(), film.getDescription(),
                FilmServiceImpl.EARLIEST_AVAILABLE_RELEASE_DATE.plusDays(1),
                film.getDuration());
        assertDoesNotThrow(() -> filmController.createFilm(filmEarliestAvailableReleaseDateMore));
        filmEarliestAvailableReleaseDateMore.setId(1L);
        assertDoesNotThrow(() -> filmController.updateFilm(filmEarliestAvailableReleaseDateMore));

        Film filmEarliestAvailableReleaseDateLess = new Film(null, film.getName(), film.getDescription(),
                FilmServiceImpl.EARLIEST_AVAILABLE_RELEASE_DATE.minusDays(1),
                film.getDuration());
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmEarliestAvailableReleaseDateLess));
        filmEarliestAvailableReleaseDateLess.setId(1L);
        assertThrows(ValidationException.class, () -> filmController.updateFilm(filmEarliestAvailableReleaseDateLess));
    }

    @Test
    void testLikes() {
        User firstUser = new User(null, "email1@gmail.com", "Login1",
                "name1",
                LocalDate.now().minusYears(36));
        userService.createUser(firstUser);
        User firstCreatedUser = userService.getUserById(1L);

        User secondUser = new User(null, "email2@gmail.com", "Login2",
                "name2",
                LocalDate.now().minusYears(26));
        userService.createUser(secondUser);
        User secondCreatedUser = userService.getUserById(2L);

        filmController.addLike(1L, firstCreatedUser.getId());
        assertEquals(1, filmController.getFilmById(1L).getLikes().size());
        assertEquals(firstCreatedUser.getId(),
                filmController.getFilmById(1L).getLikes().stream().toList().getFirst());

        Film firstFilm = new Film(null, "Name 1",
                "1".repeat(FilmServiceImpl.MAX_AVAILABLE_DESCRIPTION_LENGTH),
                FilmServiceImpl.EARLIEST_AVAILABLE_RELEASE_DATE.plusYears(20).plusDays(10),
                5);
        filmController.createFilm(firstFilm);
        Film firstCreatedFilm = filmController.getFilmById(2L);
        filmController.addLike(firstCreatedFilm.getId(), firstCreatedUser.getId());
        filmController.addLike(firstCreatedFilm.getId(), secondCreatedUser.getId());

        List<Film> mostPopularFilms = filmController.getMostPopularFilms(null);
        assertEquals(2, mostPopularFilms.size());
        assertEquals(mostPopularFilms.get(0), filmController.getFilmById(2L));
        assertEquals(mostPopularFilms.get(1), filmController.getFilmById(1L));

        mostPopularFilms = filmController.getMostPopularFilms(1);
        assertEquals(1, mostPopularFilms.size());

        filmController.removeLike(firstCreatedFilm.getId(), firstCreatedUser.getId());
        filmController.removeLike(firstCreatedFilm.getId(), secondCreatedUser.getId());

        mostPopularFilms = filmController.getMostPopularFilms(null);
        assertEquals(2, mostPopularFilms.size());
        assertEquals(mostPopularFilms.get(0), filmController.getFilmById(1L));
        assertEquals(mostPopularFilms.get(1), filmController.getFilmById(2L));
    }
}