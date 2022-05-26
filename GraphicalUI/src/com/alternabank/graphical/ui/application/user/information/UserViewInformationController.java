package com.alternabank.graphical.ui.application.user.information;

import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import com.alternabank.graphical.ui.application.user.UserViewController;
import com.alternabank.graphical.ui.application.user.information.transaction.UserAccountTransactionViewController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

public class UserViewInformationController implements Initializable {

    private UserViewController userViewComponentController;
    @FXML private UserAccountTransactionViewController userAccountTransactionViewComponentController;
    @FXML private LoanViewController loanerLoanViewComponentController;
    @FXML private LoanViewController borrowerLoanViewComponentController;

    public UserViewController getUserViewController() {
        return userViewComponentController;
    }

    public void setUserViewController(UserViewController controller) {
        this.userViewComponentController = controller;
        loanerLoanViewComponentController.loanDetailsProperty().bind(userViewComponentController.loanerLoanDetailsProperty());
        borrowerLoanViewComponentController.loanDetailsProperty().bind(userViewComponentController.borrowerLoanDetailsProperty());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.userAccountTransactionViewComponentController.setUserViewInformationController(this);
    }
}
