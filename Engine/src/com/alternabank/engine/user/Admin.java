package com.alternabank.engine.user;

public class Admin implements User {

    private final String name = "Admin";

    @Override
    public String getName() {
        return name;
    }
}
