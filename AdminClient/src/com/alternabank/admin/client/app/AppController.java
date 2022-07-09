package com.alternabank.admin.client.app;

import com.alternabank.admin.client.admin.AdminViewController;
import com.alternabank.admin.client.header.HeaderController;
import com.alternabank.client.util.loan.LoanCategoriesUtil;
import com.alternabank.client.util.loan.LoanDetailsUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable{

    private final StringProperty username = new SimpleStringProperty();

    @FXML
    private HeaderController headerComponentController;

    @FXML
    private AdminViewController adminViewComponentController;

    @FXML
    private BorderPane appComponent;

    public StringProperty usernameProperty() {
        return username;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    private void startRefreshers() {
        ServerTimeUtil.startCurrentTimeRefresher();
        LoanDetailsUtil.startLoanDetailsRefresher();
        LoanCategoriesUtil.startLoanCategoriesRefresher();
        adminViewComponentController.startRefreshers();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        headerComponentController.setAppController(this);
        adminViewComponentController.setAppController(this);
        username.addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                startRefreshers();
        });
    }
}
