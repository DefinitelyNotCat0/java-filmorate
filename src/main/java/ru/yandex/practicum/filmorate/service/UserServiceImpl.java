package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public User getUserById(Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id = " + id));
    }

    @Override
    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Id must not be empty");
        }
        if (!userStorage.doesUserExist(user.getId())) {
            throw new NotFoundException("User not found with id = " + user.getId());
        }

        return userStorage.updateUser(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        if (!userStorage.doesUserExist(userId)) {
            throw new NotFoundException("User not found with id = " + userId);
        }
        if (!userStorage.doesUserExist(friendId)) {
            throw new NotFoundException("User not found with id = " + friendId);
        }
        userStorage.addFriend(userId, friendId);
        userStorage.addFriend(friendId, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        if (!userStorage.doesUserExist(userId)) {
            throw new NotFoundException("User not found with id = " + userId);
        }
        if (!userStorage.doesUserExist(friendId)) {
            throw new NotFoundException("User not found with id = " + friendId);
        }
        userStorage.removeFriend(userId, friendId);
        userStorage.removeFriend(friendId, userId);
    }

    @Override
    public List<User> getFriendsByUserId(Long id) {
        if (!userStorage.doesUserExist(id)) {
            throw new NotFoundException("User not found with id = " + id);
        }
        return userStorage.getFriendsByUserId(id);
    }

    @Override
    public List<User> getMutualFriends(Long id, Long otherId) {
        if (!userStorage.doesUserExist(id)) {
            throw new NotFoundException("User not found with id = " + id);
        }
        if (!userStorage.doesUserExist(otherId)) {
            throw new NotFoundException("User not found with id = " + otherId);
        }
        return userStorage.getMutualFriends(id, otherId);
    }
}
