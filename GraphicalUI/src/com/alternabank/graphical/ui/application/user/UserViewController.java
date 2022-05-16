package com.alternabank.graphical.ui.application.user;

import com.alternabank.engine.user.User;
import com.alternabank.graphical.ui.application.AppController;
import com.alternabank.graphical.ui.application.user.information.UserViewInformationController;
import com.alternabank.graphical.ui.application.user.investment.UserViewInvestmentController;
import com.alternabank.graphical.ui.application.user.payment.UserViewPaymentController;
import javafx.fxml.FXML;

public class UserViewController {

    @FXML private AppController appComponentController;
    @FXML private UserViewInformationController userViewInformationComponentController;
    @FXML private UserViewInvestmentController userViewInvestmentComponentController;
    @FXML private UserViewPaymentController userViewPaymentComponentController;

    public void setAppController(AppController controller) {
        this.appComponentController = controller;
    }

    public void populate(User selectedUser) {
        userViewInformationComponentController.populate(selectedUser);
        userViewInvestmentComponentController.populate(selectedUser);
        userViewPaymentComponentController.populate(selectedUser);
    }

}
