package com.alternabank.client.user.payment;

import com.alternabank.client.user.UserViewController;
import com.alternabank.client.loan.LoanViewController;
import com.alternabank.client.user.payment.controls.PaymentControlsViewController;
import com.alternabank.client.user.payment.notification.PaymentNotificationViewController;
import com.alternabank.dto.loan.LoanDetails;
import com.alternabank.dto.loan.status.LoanStatusData;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class UserViewPaymentController implements Initializable {

    @FXML private PaymentNotificationViewController paymentNotificationViewComponentController;

    @FXML private PaymentControlsViewController paymentControlsViewComponentController;

    @FXML private LoanViewController borrowerLoanViewComponentController;

    private UserViewController userViewComponentController;

    private final ObjectProperty<LoanDetails> selectedLoanDetails = new SimpleObjectProperty<>();

    public ObjectProperty<LoanDetails> SelectedLoanDetailsProperty() {
        return selectedLoanDetails;
    }

    public LoanDetails getSelectedLoanDetails() {
        return selectedLoanDetails.get();
    }

    public void setUserViewController(UserViewController controller) {
        this.userViewComponentController = controller;
        borrowerLoanViewComponentController.loanDetailsProperty().set(new FilteredList<>(userViewComponentController.borrowerLoanDetailsProperty(), loanDetails -> loanDetails.getStatus() == LoanStatusData.ACTIVE || loanDetails.getStatus() == LoanStatusData.RISK));
    }

    public UserViewController getUserViewComponentController() {
        return userViewComponentController;
    }

/*    public void onUserSelection() {
        paymentNotificationViewComponentController.onUserSelection();
    }*/

    public void startRefreshers() {
        paymentNotificationViewComponentController.startPaymentNotificationRefresher();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paymentNotificationViewComponentController.setUserViewPaymentController(this);
        paymentControlsViewComponentController.setUserViewPaymentController(this);
        selectedLoanDetails.bind(borrowerLoanViewComponentController.selectedLoanDetailsProperty());
        borrowerLoanViewComponentController.selectedLoanDetailsProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                onLoanSelected();
            else onLoanDeselected();
        });
    }

    public void onLoanSelected() {
        paymentControlsViewComponentController.onLoanSelected();
    }

    public void onLoanDeselected() {
        paymentControlsViewComponentController.onLoanDeselected();
    }
}
