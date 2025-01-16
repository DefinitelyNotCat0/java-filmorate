package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    boolean doesUserExist(Long id);

    Optional<User> getUserById(Long id);

    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getFriendsByUserId(Long id);

    List<User> getMutualFriends(Long id, Long otherId);
}
