package com.interface21.jdbc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;

public class NamedJdbcTemplate {

    private final JdbcTemplate jdbcTemplate;
    private final Pattern pattern = Pattern.compile(":(\\w+)");

    private Map<String, Object> parameters;

    public NamedJdbcTemplate(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        parameters = new HashMap<>();
    }

    public NamedJdbcTemplate(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        parameters = new HashMap<>();
    }

    public NamedJdbcTemplate setValue(final String parameterName, final Object parameter) {
        parameters.put(parameterName, parameter);
        return this;
    }

    public int update(final String sql) {
        String lastSql = convertNoNamedSql(sql);
        Object[] params = extractParameters(sql);

        this.parameters = new HashMap<>();
        return jdbcTemplate.update(lastSql, params);
    }

    public <T> T selectForOne(final String sql, final RowMapper<T> rowMapper) {
        String lastSql = convertNoNamedSql(sql);
        Object[] params = extractParameters(sql);

        this.parameters = new HashMap<>();
        return jdbcTemplate.findOne(lastSql, rowMapper, params);
    }

    public <T> List<T> select(final String sql, final RowMapper<T> rowMapper) {
        String lastSql = convertNoNamedSql(sql);
        Object[] params = extractParameters(sql);

        this.parameters = new HashMap<>();
        return jdbcTemplate.find(lastSql, rowMapper, params);
    }

    private Object[] extractParameters(String sql) {
        Matcher matcher = pattern.matcher(sql);

        List<Object> params = new ArrayList<>();
        while (matcher.find()) {
            String param = matcher.group(1);
            params.add(parameters.get(param));
        }
        return params.toArray();
    }

    private String convertNoNamedSql(final String sql) {
        String newSql = sql;

        for (String key : parameters.keySet()) {
            newSql = newSql.replace(":" + key, "?");
        }

        return newSql;
    }
}
