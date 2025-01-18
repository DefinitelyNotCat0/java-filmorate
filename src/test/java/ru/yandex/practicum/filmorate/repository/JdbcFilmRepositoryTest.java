package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, FilmRowMapper.class,
        MpaRepository.class, MpaRowMapper.class,
        GenreRepository.class, GenreRowMapper.class})
public class JdbcFilmRepositoryTest {

    private final FilmStorage filmRepository;

    static List<Film> getTestFilms() {
        List<Film> filmList = new ArrayList<>();

        Film film1 = new Film(1L,
                "film1",
                "desc1",
                LocalDate.of(2001, 4, 1),
                30,
                new LinkedHashSet<>(Arrays.asList(new Genre(2, "Драма"), new Genre(4, "Триллер"))),
                new Mpa(1, "G"));

        Film film2 = new Film(2L,
                "film2",
                "desc2",
                LocalDate.of(1982, 1, 12),
                20,
                new LinkedHashSet<>(List.of(new Genre(1, "Комедия"))),
                new Mpa(2, "PG"));

        filmList.add(film1);
        filmList.add(film2);

        return filmList;
    }

    @Test
    public void getFilmByIdTest() {
        Optional<Film> filmOptional = filmRepository.getById(1L);
        Film testFilm = getTestFilms().getFirst();

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
        assertThat(filmOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testFilm);
    }

    @Test
    public void getAllFilmTest() {
        List<Film> filmList = (List<Film>) filmRepository.getAll();
        Film testFilm1 = getTestFilms().getFirst();
        Film testFilm2 = getTestFilms().getLast();

        assertFalse(filmList.isEmpty());
        assertThat(filmList.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(testFilm1);
        assertThat(filmList.getLast())
                .usingRecursiveComparison()
                .isEqualTo(testFilm2);
    }

    @Test
    public void saveUserAndGetAllTest() {
        Film film3 = new Film(3L,
                "film3",
                "desc3",
                LocalDate.of(1982, 1, 12),
                20,
                new LinkedHashSet<>(List.of(new Genre(1, "Комедия"))),
                new Mpa(2, "PG"));

        filmRepository.save(film3);
        Optional<Film> filmOptional3 = filmRepository.getById(3L);

        assertThat(filmOptional3)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 3L)
                );
        assertThat(filmOptional3)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(film3);
    }

    @Test
    public void updateTest() {
        Film filmForUpdate = new Film(1L,
                "filmUpdated",
                "descUpdated",
                LocalDate.of(1980, 1, 12),
                30,
                new LinkedHashSet<>(List.of(new Genre(2, "Драма"))),
                new Mpa(1, "G"));

        filmRepository.update(filmForUpdate);

        Optional<Film> userOptional = filmRepository.getById(1L);
        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(filmForUpdate);
    }
}
