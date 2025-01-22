package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.repository.GenreRepository;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaRepository.getById(rs.getInt("mpa_id")).orElse(null))
                .build();

        List<Genre> genreList = genreRepository.getByFilmId(film.getId());
        film.setGenres(new LinkedHashSet<>(genreList));

        return film;
    }
}
