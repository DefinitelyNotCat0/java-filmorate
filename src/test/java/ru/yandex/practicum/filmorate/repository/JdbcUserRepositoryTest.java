package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserRepository.class, UserRowMapper.class})
public class JdbcUserRepositoryTest {

    private final UserStorage userRepository;

    static User getTestUser() {
        return new User(1L,
                "test1@gmail.com",
                "test1_login",
                "test1",
                LocalDate.of(2000, 9, 9));
    }

    @Test
    public void findUserByIdTest() {
        Optional<User> userOptional = userRepository.getById(1L);
        User testUser = getTestUser();

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(testUser);
    }

    @Test
    public void saveUserAndGetAllTest() {
        User user4 = new User(4L,
                "test4@gmail.com",
                "test4_login",
                "test4",
                LocalDate.of(1995, 9, 9));

        userRepository.save(user4);
        Optional<User> userOptional4 = userRepository.getById(4L);

        assertThat(userOptional4)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 4L)
                );
        assertThat(userOptional4)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(user4);

        List<User> users = (List<User>) userRepository.getAll();
        assertEquals(4, users.size());
        assertThat(users.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(getTestUser());
        assertThat(users.getLast())
                .usingRecursiveComparison()
                .isEqualTo(user4);
    }

    @Test
    public void updateTest() {
        User userForUpdate = new User(1L,
                "test2@gmail.com",
                "test2_login",
                "test2",
                LocalDate.of(1999, 9, 9));

        userRepository.update(userForUpdate);

        Optional<User> userOptional = userRepository.getById(1L);
        assertThat(userOptional)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(userForUpdate);
    }

    @Test
    public void saveAndFriendTest() {
        User user1 = getTestUser();
        User user2 = new User(2L,
                "test2@gmail.com",
                "test2_login",
                "test2",
                LocalDate.of(1999, 9, 9));
        User user3 = new User(3L,
                "test3@gmail.com",
                "test3_login",
                "test3",
                LocalDate.of(1998, 9, 9));

        userRepository.addFriend(user1.getId(), user2.getId());
        List<User> friends = userRepository.getFriendsByUserId(user1.getId());
        assertEquals(1, friends.size());
        assertThat(friends.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(user2);

        userRepository.addFriend(user3.getId(), user2.getId());
        List<User> mutualFriends = userRepository.getMutualFriends(user1.getId(), user3.getId());
        assertEquals(1, mutualFriends.size());
        assertThat(mutualFriends.getFirst())
                .usingRecursiveComparison()
                .isEqualTo(user2);
    }
}
