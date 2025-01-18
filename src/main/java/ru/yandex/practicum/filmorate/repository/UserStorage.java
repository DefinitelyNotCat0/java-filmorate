package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    boolean exists(Long id);

    Optional<User> getById(Long id);

    Collection<User> getAll();

    User save(User user);

    User update(User user);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriendsByUserId(Long id);

    List<User> getMutualFriends(Long id, Long otherId);
}
