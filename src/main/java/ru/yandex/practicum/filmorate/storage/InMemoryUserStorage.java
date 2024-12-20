package ru.yandex.practicum.filmorate.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> userMap = new HashMap<>();

    @Override
    public boolean doesUserExist(Long id) {
        return userMap.containsKey(id);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        if (userMap.containsKey(id)) {
            return Optional.of(userMap.get(id));
        }
        return Optional.empty();
    }

    @Override
    public Collection<User> getUsers() {
        return userMap.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userMap.put(user.getId(), user);
        log.debug("user created");
        return user;
    }

    @Override
    public User updateUser(User user) {
        User oldUser = userMap.get(user.getId());
        oldUser.setEmail(user.getEmail());
        oldUser.setLogin(user.getLogin());
        if (user.getName() == null || user.getName().isBlank()) {
            oldUser.setName(user.getLogin());
        } else {
            oldUser.setName(user.getName());
        }
        oldUser.setBirthday(user.getBirthday());

        log.debug("user updated");
        return oldUser;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        userMap.get(userId).getFriends().add(friendId);
        log.debug("User with id {} was added to user's (id={}) friend list", friendId, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        userMap.get(userId).getFriends().remove(friendId);
        log.debug("Friend with id {} was removed from user with id {}", friendId, userId);
    }

    @Override
    public List<User> getFriendsByUserId(Long id) {
        return userMap.get(id).getFriends().stream()
                .map(userMap::get)
                .toList();
    }

    @Override
    public List<User> getMutualFriends(Long id, Long otherId) {
        List<Long> mutualFriendIdList = new ArrayList<>(userMap.get(id).getFriends());
        mutualFriendIdList.retainAll(userMap.get(otherId).getFriends());
        return mutualFriendIdList.stream()
                .map(userMap::get)
                .toList();
    }

    private long getNextId() {
        long currentMaxId = userMap.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
