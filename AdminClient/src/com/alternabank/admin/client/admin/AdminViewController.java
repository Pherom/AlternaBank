package com.alternabank.admin.client.admin;

import com.alternabank.admin.client.app.AppController;
import com.alternabank.admin.client.customer.CustomerViewController;
import com.alternabank.admin.client.loan.LoanViewController;
import com.alternabank.client.util.loan.LoanDetailsUtil;
import com.alternabank.dto.customer.CustomerDetails;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.*;

public class AdminViewController implements Initializable {

    private AppController appComponentController;
    @FXML private LoanViewController loanViewComponentController;
    @FXML private CustomerViewController customerViewComponentController;

    private Timer timer;

    private TimerTask customerDetailsRefresher;

    public void setAppController(AppController controller) {
        this.appComponentController = controller;
    }

    public void updateCustomerDetails(Map<String, CustomerDetails> customerDetails) {
        customerViewComponentController.setCustomerDetails(customerDetails);
    }

    private void startCustomerDetailsRefresher() {
        customerDetailsRefresher = new CustomerDetailsRefresher(this::updateCustomerDetails);
        timer = new Timer();
        timer.schedule(customerDetailsRefresher, 500, 500);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loanViewComponentController.loanDetailsProperty().set(LoanDetailsUtil.loanDetails);
    }

    public void startRefreshers() {
        startCustomerDetailsRefresher();
    }
}
