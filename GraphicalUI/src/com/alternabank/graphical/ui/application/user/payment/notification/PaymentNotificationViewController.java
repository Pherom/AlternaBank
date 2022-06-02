package com.alternabank.graphical.ui.application.user.payment.notification;

import com.alternabank.engine.loan.notification.PaymentNotification;
import com.alternabank.engine.user.User;
import com.alternabank.engine.user.UserManager;
import com.alternabank.graphical.ui.application.user.payment.UserViewPaymentController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentNotificationViewController {

    @FXML
    private ListView<PaymentNotification> paymentNotificationViewComponent;

    private UserViewPaymentController userViewPaymentComponentController;

    public void onUserSelection() {
        User selectedUser = userViewPaymentComponentController.getUserViewComponentController().getAppController().getSelectedUser();
        paymentNotificationViewComponent.setItems(FXCollections.observableList(
        UserManager.getInstance().getAdmin().getCustomerManager().getCustomersByName().get(selectedUser.getName()).getPostedLoansIDs().stream()
                .map(loanID -> UserManager.getInstance().getAdmin().getLoanManager().getLoansByID().get(loanID).toLoanDetails().getPaymentNotifications())
                .flatMap(List::stream).sorted((notification1, notification2) -> {
                    if (notification1.getPaymentTime() > notification2.getPaymentTime())
                        return -1;
                    else if (notification1.getPaymentTotal() < notification2.getPaymentTime())
                        return 1;
                    else return 0;
                }).collect(Collectors.toList())));
    }

    public void setUserViewPaymentController(UserViewPaymentController controller) {
        this.userViewPaymentComponentController = controller;
    }
}
