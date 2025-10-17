package com.interface21.jdbc.core;

import com.interface21.jdbc.bind.RowMapper;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;

public class NamedJdbcTemplate {

    private final JdbcTemplate jdbcTemplate;

    public NamedJdbcTemplate(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public NamedJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int update(final String sql, final NamedSqlParamMap parameters) {
        String lastSql = parameters.convertNoNamedSql(sql);
        Object[] params = parameters.extractParameters(sql);

        return jdbcTemplate.update(lastSql, params);
    }

    public int update(final Connection connection, final String sql, final NamedSqlParamMap parameters) {
        String lastSql = parameters.convertNoNamedSql(sql);
        Object[] params = parameters.extractParameters(sql);

        return jdbcTemplate.update(connection, lastSql, params);
    }

    public <T> T selectForOne(final String sql, final NamedSqlParamMap parameters, final RowMapper<T> rowMapper) {
        String lastSql = parameters.convertNoNamedSql(sql);
        Object[] params = parameters.extractParameters(sql);

        return jdbcTemplate.findOne(lastSql, rowMapper, params);
    }

    public <T> T selectForOne(final Connection connection,
                              final String sql,
                              final NamedSqlParamMap parameters,
                              final RowMapper<T> rowMapper
    ) {
        String lastSql = parameters.convertNoNamedSql(sql);
        Object[] params = parameters.extractParameters(sql);

        return jdbcTemplate.findOne(connection, lastSql, rowMapper, params);
    }

    public <T> List<T> select(final String sql, final RowMapper<T> rowMapper) {
        return select(sql, new NamedSqlParamMap(), rowMapper);
    }

    public <T> List<T> select(final Connection connection, final String sql, final RowMapper<T> rowMapper) {
        return select(connection, sql, new NamedSqlParamMap(), rowMapper);
    }

    public <T> List<T> select(final String sql, final NamedSqlParamMap parameters, final RowMapper<T> rowMapper
    ) {
        String lastSql = parameters.convertNoNamedSql(sql);
        Object[] params = parameters.extractParameters(sql);

        return jdbcTemplate.find(lastSql, rowMapper, params);
    }

    public <T> List<T> select(final Connection connection,
                              final String sql,
                              final NamedSqlParamMap parameters,
                              final RowMapper<T> rowMapper
    ) {
        String lastSql = parameters.convertNoNamedSql(sql);
        Object[] params = parameters.extractParameters(sql);

        return jdbcTemplate.find(connection, lastSql, rowMapper, params);
    }

    public DataSource getDataSource() {
        return jdbcTemplate.getDataSource();
    }
}
