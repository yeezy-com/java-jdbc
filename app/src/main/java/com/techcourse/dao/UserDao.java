package com.techcourse.dao;

import com.interface21.jdbc.core.JdbcTemplate;
import com.interface21.jdbc.core.NamedJdbcTemplate;
import com.interface21.jdbc.bind.RowMapper;
import com.techcourse.domain.User;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao {

    private static final Logger log = LoggerFactory.getLogger(UserDao.class);

    private final NamedJdbcTemplate namedJdbcTemplate;

    public UserDao(final DataSource dataSource) {
        this.namedJdbcTemplate = new NamedJdbcTemplate(dataSource);
    }

    public UserDao(final JdbcTemplate jdbcTemplate) {
        this.namedJdbcTemplate = new NamedJdbcTemplate(jdbcTemplate);
    }

    public UserDao(final NamedJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public void insert(final User user) {
        final var sql = "insert into users (account, password, email) values (:account, :password, :email)";

        log.debug("insert user: {}", user);
        namedJdbcTemplate.setValue("account", user.getAccount())
            .setValue("password", user.getPassword())
            .setValue("email", user.getEmail())
            .update(sql);
    }

    public void update(final User user) {
        final var sql = "update users set account = :account, password = :password, email = :email where id = :id";

        log.info("update user: {}", user);
        namedJdbcTemplate.setValue("account", user.getAccount())
            .setValue("email", user.getEmail())
            .setValue("id", user.getId())
            .setValue("password", user.getPassword())
            .update(sql);
    }

    public List<User> findAll() {
        final var sql = "select id, account, password, email from users";

        return namedJdbcTemplate.select(sql, userRowMapper());
    }

    public Optional<User> findById(final Long id) {
        final var sql = "select id, account, password, email from users where id = :id";

        User user = namedJdbcTemplate.setValue("id", id)
            .selectForOne(sql, userRowMapper());
        return Optional.of(user);
    }

    public Optional<User> findByAccount(final String account) {
        final var sql = "select id, account, password, email from users where account = :account";

        User user = namedJdbcTemplate.setValue("account", account)
            .selectForOne(sql, userRowMapper());
        return Optional.of(user);
    }

    private RowMapper<User> userRowMapper() {
        return rs -> new User(
            rs.getLong("id"),
            rs.getString("account"),
            rs.getString("password"),
            rs.getString("email")
        );
    }
}
