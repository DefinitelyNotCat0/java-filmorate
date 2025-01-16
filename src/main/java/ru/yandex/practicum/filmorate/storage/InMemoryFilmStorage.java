package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> filmMap = new HashMap<>();

    @Override
    public boolean doesFilmExist(Long id) {
        return filmMap.containsKey(id);
    }

    @Override
    public Optional<Film> getFilmById(Long id) {
        if (filmMap.containsKey(id)) {
            return Optional.of(filmMap.get(id));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Film> getFilms() {
        return filmMap.values();
    }

    @Override
    public Film createFilm(Film film) {
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        log.debug("film created");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id must not be empty");
        }
        if (!filmMap.containsKey(film.getId())) {
            throw new NotFoundException("Film not found with id = " + film.getId());
        }

        Film oldFilm = filmMap.get(film.getId());
        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());

        log.debug("film updated");
        return oldFilm;
    }

    @Override
    public void addLike(Long id, Long userId) {
        filmMap.get(id).getLikes().add(userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        filmMap.get(id).getLikes().remove(userId);
    }

    @Override
    public List<Film> getMostPopularFilms(Integer limit) {
        return filmMap.values().stream()
                .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                .sorted(Comparator.comparingInt(Film::getLikeCount).reversed())
                .limit(limit)
                .toList();
    }

    private long getNextId() {
        long currentMaxId = filmMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
