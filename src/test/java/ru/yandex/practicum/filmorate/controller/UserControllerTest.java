package ru.yandex.practicum.filmorate.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserController userController;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        UserService userService = new UserService(userStorage);
        userController = new UserController(userService);

        User user = new User(null, "email@gmail.com", "Login",
                "name",
                LocalDate.now().minusYears(20).minusMonths(3).minusDays(12));
        userController.createUser(user);
    }

    @Test
    void createUpdateAndGetUsers() {
        assertEquals(1, userController.getUsers().size());
        User firstUser = new User(null, "email1@gmail.com", "Login1",
                "name1",
                LocalDate.now().minusYears(36));
        userController.createUser(firstUser);
        assertEquals(2, userController.getUsers().size());
        User firstCreatedUser = userController.getUsers().stream().skip(1).findFirst().orElse(new User());
        assertEquals(firstUser, firstCreatedUser);
        assertEquals(2, firstCreatedUser.getId());

        User secondUser = new User(null, "email2@gmail.com", "Login2",
                "name2",
                LocalDate.now().minusYears(26));
        userController.createUser(secondUser);
        assertEquals(3, userController.getUsers().size());
        User secondCreatedUser = userController.getUsers().stream().skip(2).findFirst().orElse(new User());
        assertEquals(secondUser, secondCreatedUser);
        assertEquals(3, secondCreatedUser.getId());

        User firstUserUpdate = new User(firstCreatedUser.getId(), "email1_updated@gmail.com",
                "Login1_updated", "Name1 updated",
                LocalDate.now().minusYears(37));
        userController.updateUser(firstUserUpdate);
        assertEquals(3, userController.getUsers().size());
        User firstUserUpdateGet = userController.getUsers().stream().skip(1).findFirst().orElse(new User());
        assertEquals(firstUserUpdate, firstUserUpdateGet);
        assertEquals(2, firstUserUpdateGet.getId());

        User secondUserUpdate = new User(
                (long) (userController.getUsers().size() + 1),
                "email2_updated@gmail.com",
                "Login2_updated", "Name1 updated",
                LocalDate.now().minusYears(37));
        assertThrows(NotFoundException.class, () -> userController.updateUser(secondUserUpdate));
        secondUserUpdate.setId(null);
        assertThrows(ValidationException.class, () -> userController.updateUser(secondUserUpdate));
    }

    @Test
    void friendsTest() {
        assertEquals(1, userController.getUsers().size());
        User firstUser = new User(null, "email1@gmail.com", "Login1",
                "name1",
                LocalDate.now().minusYears(36));
        userController.createUser(firstUser);
        User firstCreatedUser = userController.getUsers().stream().skip(1).findFirst().orElse(new User());

        User secondUser = new User(null, "email2@gmail.com", "Login2",
                "name2",
                LocalDate.now().minusYears(26));
        userController.createUser(secondUser);
        User secondCreatedUser = userController.getUsers().stream().skip(2).findFirst().orElse(new User());

        userController.addFriend(1L, firstCreatedUser.getId());
        assertEquals(1, userController.getFriendsByUserId(1L).size());

        userController.addFriend(1L, secondCreatedUser.getId());
        assertEquals(2, userController.getFriendsByUserId(1L).size());

        userController.addFriend(firstCreatedUser.getId(), secondCreatedUser.getId());
        assertEquals(1, userController.getMutualFriends(1L, firstCreatedUser.getId()).size());
        assertEquals(userController.getUserById(secondCreatedUser.getId()),
                userController.getMutualFriends(1L, firstCreatedUser.getId()).getFirst());

        userController.removeFriend(1L, secondCreatedUser.getId());
        assertEquals(1, userController.getFriendsByUserId(1L).size());
    }
}