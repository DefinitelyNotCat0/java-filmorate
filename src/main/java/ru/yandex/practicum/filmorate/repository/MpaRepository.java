package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaRepository {

    private final NamedParameterJdbcOperations jdbc;
    private final MpaRowMapper mpaRowMapper;

    public boolean exists(Integer id) {
        String query = "select count(*) from mpa where id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Integer count = jdbc.queryForObject(query, params, Integer.class);
        return count != null && count > 0;
    }

    public List<Mpa> getAll() {
        String query = "select * from mpa";
        return jdbc.query(query, mpaRowMapper);
    }

    public Optional<Mpa> getById(Integer id) {
        String query = "select * from mpa where id = :id";
        if (!exists(id)) {
            return Optional.empty();
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Mpa mpa = jdbc.query(query, params, mpaRowMapper).getFirst();
        return Optional.of(mpa);
    }
}
