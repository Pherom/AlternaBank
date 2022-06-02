package com.alternabank.graphical.ui.application.user.payment;

import com.alternabank.engine.loan.Loan;
import com.alternabank.engine.loan.dto.LoanDetails;
import com.alternabank.engine.loan.notification.PaymentNotification;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.engine.user.User;
import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.loan.LoanViewController;
import com.alternabank.graphical.ui.application.user.UserViewController;
import com.alternabank.graphical.ui.application.user.payment.controls.PaymentControlsViewController;
import com.alternabank.graphical.ui.application.user.payment.notification.PaymentNotificationViewController;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javax.swing.text.html.ListView;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
        borrowerLoanViewComponentController.loanDetailsProperty().set(new FilteredList<>(userViewComponentController.borrowerLoanDetailsProperty(), loanDetails -> loanDetails.getStatus() == Loan.Status.ACTIVE || loanDetails.getStatus() == Loan.Status.RISK));
    }

    public UserViewController getUserViewComponentController() {
        return userViewComponentController;
    }

    public void onUserSelection() {
        paymentNotificationViewComponentController.onUserSelection();
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
