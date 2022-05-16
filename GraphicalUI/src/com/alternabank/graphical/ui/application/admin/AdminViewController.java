package com.alternabank.graphical.ui.application.admin;

import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.xml.event.XMLLoadSuccessEvent;
import com.alternabank.engine.xml.event.listener.XMLLoadSuccessListener;
import com.alternabank.graphical.ui.application.admin.header.AdminViewHeaderController;
import com.alternabank.graphical.ui.application.AppController;
import com.alternabank.graphical.ui.application.customer.CustomerViewController;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class AdminViewController implements Initializable {

    @FXML private AppController appController;
    @FXML private AdminViewHeaderController adminViewHeaderComponentController;

    @FXML private LoanViewController loanViewComponentController;
    @FXML private CustomerViewController customerViewComponentController;

    public void setAppController(AppController controller) {
        this.appController = controller;
    }

    public void setAdminViewHeaderController(AdminViewHeaderController controller) {
        this.adminViewHeaderComponentController = controller;
        controller.setAdminViewController(this);
    }

    public void onAdvanceTimeRequest(ActionEvent event) {
        appController.onAdvanceTimeRequest(event);
    }

    public void onLoadFileRequest(ActionEvent event) {
        appController.onLoadFileRequest(event);
    }

    public void loadedSuccessfully(XMLLoadSuccessEvent event) {
        populateLoanView(event.getSource().getAdmin().getLoanManager().getLoanDetails());
        adminViewHeaderComponentController.loadedSuccessfully(event);
    }

    private void populateLoanView(Set<LoanDetails> loanDetails) {
        loanViewComponentController.populate(loanDetails);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAdminViewHeaderController(adminViewHeaderComponentController);
    }
}
