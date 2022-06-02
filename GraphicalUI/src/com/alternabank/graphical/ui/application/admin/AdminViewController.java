package com.alternabank.graphical.ui.application.admin;

import com.alternabank.engine.customer.CustomerManager;
import com.alternabank.engine.customer.dto.CustomerDetails;
import com.alternabank.engine.loan.event.PaymentDueEvent;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.engine.user.UserManager;
import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.graphical.ui.application.admin.header.AdminViewHeaderController;
import com.alternabank.graphical.ui.application.AppController;
import com.alternabank.graphical.ui.application.customer.CustomerViewController;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.Notifications;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class AdminViewController implements Initializable {

    private AppController appComponentController;
    @FXML private AdminViewHeaderController adminViewHeaderComponentController;

    @FXML private LoanViewController loanViewComponentController;
    @FXML private CustomerViewController customerViewComponentController;

    public void setAppController(AppController controller) {
        this.appComponentController = controller;
        loanViewComponentController.loanDetailsProperty().bind(appComponentController.loanDetailsProperty());
        customerViewComponentController.customerDetailsProperty().bind(appComponentController.customerDetailsProperty());
    }

    public void onAdvanceTimeRequest(ActionEvent event) {
        appComponentController.onAdvanceTimeRequest(event);
    }

    public void onLoadFileRequest(ActionEvent event) {
        appComponentController.onLoadFileRequest(event);
    }

    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        adminViewHeaderComponentController.loadedSuccessfully(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adminViewHeaderComponentController.setAdminViewController(this);
    }

    public void bilateralTransactionExecuted(BilateralTransactionEvent event) {
        if (appComponentController.getSelectedUser() == UserManager.getInstance().getAdmin())
            Notifications.create().text(event.getRecord().toString()).showInformation();
    }

    public void unilateralTransactionExecuted(UnilateralTransactionEvent event) {
        if (appComponentController.getSelectedUser() == UserManager.getInstance().getAdmin())
            Notifications.create().text(event.getRecord().toString()).showInformation();
    }

    public void paymentDue(PaymentDueEvent event) {
        if (appComponentController.getSelectedUser() == UserManager.getInstance().getAdmin())
            Notifications.create().text(event.getNotification().toString()).showInformation();
    }
}
