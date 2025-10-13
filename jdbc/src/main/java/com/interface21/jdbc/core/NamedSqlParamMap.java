package com.interface21.jdbc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedSqlParamMap {

    private final Pattern pattern = Pattern.compile(":(\\w+)");
    private final Map<String, Object> parameters;

    public NamedSqlParamMap() {
        this.parameters = new HashMap<>();
    }

    public NamedSqlParamMap(final String name, final Object value) {
        this.parameters = new HashMap<>();
        this.parameters.put(name, value);
    }

    public NamedSqlParamMap addValue(final String name, final Object value) {
        parameters.put(name, value);
        return this;
    }

    Object[] extractParameters(final String sql) {
        Matcher matcher = pattern.matcher(sql);

        List<Object> params = new ArrayList<>();
        while (matcher.find()) {
            String param = matcher.group(1);
            params.add(parameters.get(param));
        }
        return params.toArray();
    }

    String convertNoNamedSql(final String sql) {
        String newSql = sql;

        Matcher matcher = pattern.matcher(sql);
        while (matcher.find()) {
            newSql = newSql.replace(":" + matcher.group(1), "?");
        }

        return newSql;
    }
}
