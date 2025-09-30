package com.interface21.jdbc.core;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class JdbcTemplateTest {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(new TestDataSource());

    @AfterEach
    void drop() {
        final var dropTableSql = "drop table test_users";
        jdbcTemplate.update(dropTableSql);
    }

    @Test
    void 데이터베이스에_insert_할_수_있다() {
        final var createSql = "create table test_users (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
            "name VARCHAR(50)," +
            "password VARCHAR(50))";
        final var insertSql = "insert into test_users (name, password) values (?, ?)";
        final var findSql = "select id, name, password from test_users where name = ?";
        jdbcTemplate.update(createSql);
        TestUser user = new TestUser("test", "1234");

        jdbcTemplate.update(insertSql, user.getName(), user.getPassword());

        TestUser findUser = (TestUser) jdbcTemplate.findOne(findSql, TestUser.class, user.getName());
        Assertions.assertThat(findUser.getName()).isEqualTo(user.getName());
        Assertions.assertThat(findUser.getPassword()).isEqualTo(user.getPassword());
    }

    @Test
    void 데이터베이스에서_모든_컬럼을_찾을_수_있다() {
        final var createSql = "create table test_users (" +
            "id BIGINT PRIMARY KEY AUTO_INCREMENT," +
            "name VARCHAR(50)," +
            "password VARCHAR(50))";
        final var insertSql = "insert into test_users (name, password) values (?, ?)";
        final var findAllSql = "select id, name, password from test_users";
        jdbcTemplate.update(createSql);
        TestUser user1 = new TestUser("test1", "1234");
        TestUser user2 = new TestUser("test2", "1234");
        TestUser user3 = new TestUser("test3", "1234");
        jdbcTemplate.update(insertSql, user1.getName(), user1.getPassword());
        jdbcTemplate.update(insertSql, user2.getName(), user2.getPassword());
        jdbcTemplate.update(insertSql, user3.getName(), user3.getPassword());

        List<Object> objects = jdbcTemplate.find(findAllSql, TestUser.class);

        Assertions.assertThat(objects.size()).isEqualTo(3);
    }
}
