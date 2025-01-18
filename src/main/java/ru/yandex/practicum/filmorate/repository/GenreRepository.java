package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreRepository {
    private final NamedParameterJdbcOperations jdbc;
    private final GenreRowMapper genreRowMapper;

    public boolean exists(Integer id) {
        String query = "select count(*) from genres where id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Integer count = jdbc.queryForObject(query, params, Integer.class);
        return count != null && count > 0;
    }

    public List<Genre> getAll() {
        String query = "select * from genres";
        return jdbc.query(query, genreRowMapper);
    }

    public Optional<Genre> getById(Integer id) {
        String query = "select * from genres where id = :id";
        if (!exists(id)) {
            return Optional.empty();
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Genre genre = jdbc.query(query, params, genreRowMapper).getFirst();
        return Optional.of(genre);
    }

    public List<Genre> getByFilmId(Long id) {
        String query = "select g.* from films f inner join films_genres fg on fg.film_id = f.id " +
                "inner join genres g on g.id = fg.genre_id where f.id = :id order by g.id asc";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return jdbc.query(query, params, genreRowMapper);
    }

    public void saveFilmGenres(Long filmId, Set<Genre> genreList) {
        MapSqlParameterSource[] params = genreList.stream().map(genre -> {
            MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
            mapSqlParameterSource.addValue("film_id", filmId);
            mapSqlParameterSource.addValue("genre_id", genre.getId());
            return mapSqlParameterSource;
        }).toArray(MapSqlParameterSource[]::new);

        jdbc.batchUpdate("insert into films_genres(film_id, genre_id) values(:film_id, :genre_id)", params);
    }

    public void removeFilmGenres(Long filmId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);

        int cnt = jdbc.update("delete from films_genres where film_id = :film_id", params);
        log.debug("Deleted {} genres from film with id {}", cnt, filmId);
    }
}
