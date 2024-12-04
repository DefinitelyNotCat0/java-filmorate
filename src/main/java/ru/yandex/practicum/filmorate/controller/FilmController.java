package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    public static final int MAX_AVAILABLE_DESCRIPTION_LENGTH = 200;
    public static final LocalDate EARLIEST_AVAILABLE_RELEASE_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    private final Map<Long, Film> filmMap = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return filmMap.values();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        validate(film);
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        log.debug("film created");
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (!filmMap.containsKey(film.getId())) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }

        Film oldFilm = filmMap.get(film.getId());
        validate(film);

        oldFilm.setName(film.getName());
        oldFilm.setDescription(film.getDescription());
        oldFilm.setReleaseDate(film.getReleaseDate());
        oldFilm.setDuration(film.getDuration());
        log.debug("film updated");
        return oldFilm;
    }

    private void validate(Film film) {
        List<String> errors = new ArrayList<>();
        if (film.getName() == null || film.getName().isBlank()) {
            errors.add("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > MAX_AVAILABLE_DESCRIPTION_LENGTH) {
            errors.add(String.format("Максимальная длина описания — %d символов", MAX_AVAILABLE_DESCRIPTION_LENGTH));
        }
        if (film.getReleaseDate() != null &&
                film.getReleaseDate().isBefore(EARLIEST_AVAILABLE_RELEASE_DATE)) {
            errors.add("Дата релиза не может быть раньше " + EARLIEST_AVAILABLE_RELEASE_DATE);
        }
        if (film.getDuration() != null && film.getDuration().isNegative()) {
            errors.add("Продолжительность фильма должна быть положительным числом");
        }

        if (!errors.isEmpty()) {
            String errorMessage = StringUtils.arrayToDelimitedString(errors.toArray(), ";");
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }
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
