package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.repository.MpaRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaRepository mpaRepository;

    @Override
    public List<Mpa> getMpaAll() {
        return mpaRepository.getAll();
    }

    @Override
    public Mpa getMpaById(Integer id) {
        return mpaRepository.getById(id)
                .orElseThrow(() -> new NotFoundException("Mpa not found with id = " + id));
    }
}
