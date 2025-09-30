package com.interface21.jdbc.core;

public class TestUser {

    private final Long id;
    private final String name;
    private final String password;

    public TestUser(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public TestUser(String name, String password) {
        this.id = null;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
