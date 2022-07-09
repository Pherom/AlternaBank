package com.alternabank.client.app;

import com.alternabank.client.header.HeaderController;
import com.alternabank.client.loan.LoanRequestFormController;
import com.alternabank.client.user.UserViewController;
import com.alternabank.client.util.loan.LoanCategoriesUtil;
import com.alternabank.client.util.loan.LoanDetailsUtil;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.loan.LoanDetails;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.ResourceBundle;

public class AppController implements Initializable{

    private final StringProperty username = new SimpleStringProperty();

    @FXML
    private HeaderController headerComponentController;

    @FXML
    UserViewController userViewComponentController;

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
        userViewComponentController.startRefreshers();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        headerComponentController.setAppController(this);
        userViewComponentController.setAppController(this);
        username.addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                startRefreshers();
        });
    }

/*    public OptionalInt getMaxBorrowerActiveLoanCount() {
        return LoanDetailsUtil.loanDetails.stream().mapToInt(LoanDetails::getBorrowerActiveLoanCount).max();
    }

    public OptionalInt getMaxLoanTerm() {
        return LoanDetailsUtil.loanDetails.stream().mapToInt(LoanDetails::getOriginalTerm).max();
    }*/
}
