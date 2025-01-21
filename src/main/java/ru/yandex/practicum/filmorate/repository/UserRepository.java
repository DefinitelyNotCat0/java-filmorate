package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository implements UserStorage {

    private final NamedParameterJdbcOperations jdbc;
    private final UserRowMapper userRowMapper;

    @Override
    public boolean exists(Long id) {
        String query = "select count(*) from users where id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        Integer count = jdbc.queryForObject(query, params, Integer.class);
        return count != null && count > 0;
    }

    @Override
    public Optional<User> getById(Long id) {
        String query = "select * from users where id = :id";
        if (!exists(id)) {
            return Optional.empty();
        }

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        User user = jdbc.query(query, params, userRowMapper).getFirst();
        return Optional.of(user);
    }

    @Override
    public Collection<User> getAll() {
        String query = "select * from users";
        return jdbc.query(query, userRowMapper);
    }

    @Override
    public User save(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        String userName = (user.getName() == null || user.getName().isBlank()) ? user.getLogin() : user.getName();

        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", userName);
        params.addValue("birthday", user.getBirthday());

        jdbc.update("insert into users(email, login, name, birthday)" +
                "values(:email, :login, :name, :birthday)", params, keyHolder, new String[]{"id"});

        user.setId(keyHolder.getKeyAs(Long.class));
        log.debug("user created");
        return user;
    }

    @Override
    public User update(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        String userName = (user.getName() == null || user.getName().isBlank()) ? user.getLogin() : user.getName();

        params.addValue("email", user.getEmail());
        params.addValue("login", user.getLogin());
        params.addValue("name", userName);
        params.addValue("birthday", user.getBirthday());
        params.addValue("id", user.getId());

        jdbc.update("update users set " +
                "email = :email, login = :login, name = :name, birthday = :birthday " +
                "where id = :id", params);

        log.debug("user updated");
        return getById(user.getId()).orElse(null);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);

        jdbc.update("insert into users_friends(user_id, friend_id) values(:user_id, :friend_id)",
                params);
        log.debug("User with id {} was added to user's (id={}) friend list", friendId, userId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("user_id", userId);
        params.addValue("friend_id", friendId);

        jdbc.update("delete from users_friends where user_id = :user_id and friend_id = :friend_id",
                params);
        log.debug("Friend with id {} was removed from user with id {}", friendId, userId);
    }

    @Override
    public List<User> getFriendsByUserId(Long id) {
        String query = "select u.* from users_friends uf " +
                "inner join users u on u.id = uf.friend_id " +
                "where uf.user_id = :id";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        return jdbc.query(query, params, userRowMapper);
    }

    @Override
    public List<User> getMutualFriends(Long id, Long otherId) {
        String query = "select u.* " +
                "from users u " +
                "where exists (" +
                "select uf.friend_id, count(uf.user_id) " +
                "from users_friends uf " +
                "where (uf.user_id = :id OR uf.user_id = :other_id) " +
                "and uf.friend_id = u.id " +
                "group by uf.friend_id " +
                "having count(uf.user_id) > 1)";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("other_id", otherId);

        return jdbc.query(query, params, userRowMapper);
    }
}
