package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmRepository implements FilmStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final FilmRowMapper filmRowMapper;
    private final GenreRepository genreRepository;

    @Override
    public boolean exists(Long id) {
        String query = "select count(*) from films where id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Integer count = jdbc.queryForObject(query, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public Optional<Film> getById(Long id) {
        String query = "select * from films where id = :id";
        if (!exists(id)) {
            return Optional.empty();
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Film film = jdbc.query(query, params, filmRowMapper).getFirst();
        return Optional.of(film);
    }

    @Override
    public Collection<Film> getAll() {
        String query = "select * from films";
        return jdbc.query(query, filmRowMapper);
    }

    @Override
    public Film save(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", film.getMpa().getId());

        jdbc.update("insert into films(name, description, release_date, duration, mpa_id) " +
                        "values(:name, :description, :release_date, :duration, :mpa_id)",
                params, keyHolder, new String[]{"id"});
        film.setId(keyHolder.getKeyAs(Long.class));

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreRepository.saveFilmGenres(film.getId(), film.getGenres());
        }

        log.debug("film created");
        return getById(film.getId()).orElse(null);
    }

    @Override
    public Film update(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", film.getMpa().getId());
        params.addValue("id", film.getId());

        jdbc.update("update films set name = :name, description = :description, " +
                "release_date = :release_date, duration = :duration, mpa_id = :mpa_id " +
                "where id = :id", params);

        genreRepository.removeFilmGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            genreRepository.saveFilmGenres(film.getId(), film.getGenres());
        }

        log.debug("film updated");
        return getById(film.getId()).orElse(null);
    }

    @Override
    public void addLike(Long id, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("film_id", id);
        params.addValue("user_id", userId);

        jdbc.update("insert into films_likes(film_id, user_id) " +
                "values(:film_id, :user_id)", params);
        log.debug("Like was added to film {} by user with id {}", id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("film_id", id);
        params.addValue("user_id", userId);

        jdbc.update("delete from films_likes where film_id = :film_id " +
                "and user_id = :user_id", params);
        log.debug("Like was removed from film {} by user with id {}", id, userId);
    }

    @Override
    public List<Film> getMostPopularFilms(Integer limit) {
        String query = "select f.* " +
                "from films f " +
                "inner join (" +
                "select fl.film_id, count(fl.user_id) as cnt " +
                "from films_likes fl " +
                "group by (fl.film_id) " +
                ") fl on fl.film_id = f.id " +
                "order by fl.cnt desc " +
                "limit :limit";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("limit", limit);

        return jdbc.query(query, params, filmRowMapper);
    }
}
