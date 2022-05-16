package com.alternabank.engine.user;

import com.alternabank.engine.customer.CustomerManager;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserManager {

    private static UserManager instance = null;
    private final Admin admin = new Admin();
    private User currentUser = admin;

    public static UserManager getInstance() {
        if(instance == null)
            instance = new UserManager();
        return instance;
    }

    private UserManager() {

    }

    public Admin getAdmin() {
        return admin;
    }

    public User getUser(String name) {
        return admin.getCustomerManager().getCustomersByName().get(name);
    }

    public Set<User> getUsers() {
        return new HashSet<>(admin.getCustomerManager().getCustomersByName().values());
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

}
