package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User getUserById(Long id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + id));
    }

    @Override
    public Collection<User> getUsers() {
        return userStorage.getAll();
    }

    @Override
    public User createUser(User user) {
        return userStorage.save(user);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id must not be empty");
        }
        if (!userStorage.exists(user.getId())) {
            throw new NotFoundException("User not found with id = " + user.getId());
        }

        return userStorage.update(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("User not found with id = " + userId);
        }
        if (!userStorage.exists(friendId)) {
            throw new NotFoundException("User not found with id = " + friendId);
        }
        userStorage.addFriend(userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (!userStorage.exists(userId)) {
            throw new NotFoundException("User not found with id = " + userId);
        }
        if (!userStorage.exists(friendId)) {
            throw new NotFoundException("User not found with id = " + friendId);
        }
        userStorage.removeFriend(userId, friendId);
    }

    @Override
    public List<User> getFriendsByUserId(Long id) {
        if (!userStorage.exists(id)) {
            throw new NotFoundException("User not found with id = " + id);
        }
        return userStorage.getFriendsByUserId(id);
    }

    @Override
    public List<User> getMutualFriends(Long id, Long otherId) {
        if (!userStorage.exists(id)) {
            throw new NotFoundException("User not found with id = " + id);
        }
        if (!userStorage.exists(otherId)) {
            throw new NotFoundException("User not found with id = " + otherId);
        }
        return userStorage.getMutualFriends(id, otherId);
    }
}
