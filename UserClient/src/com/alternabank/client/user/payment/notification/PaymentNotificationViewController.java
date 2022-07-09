package com.alternabank.client.user.payment.notification;

import com.alternabank.client.user.payment.UserViewPaymentController;
import com.alternabank.client.util.time.ServerTimeUtil;
import com.alternabank.dto.customer.PaymentNotificationsAndVersion;
import com.alternabank.dto.loan.notification.PaymentNotification;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.controlsfx.control.Notifications;

import java.sql.Time;
import java.util.List;
import java.util.Timer;
import java.util.stream.Collectors;

public class PaymentNotificationViewController {

    @FXML
    private ListView<PaymentNotification> paymentNotificationViewComponent;

    private PaymentNotificationsRefresher paymentNotificationsRefresher;

    private Timer timer;

    private UserViewPaymentController userViewPaymentComponentController;

/*    public void onUserSelection() {
        String username = userViewPaymentComponentController.getUserViewComponentController().getAppController().getUsername();
        paymentNotificationViewComponent.setItems(FXCollections.observableList(
        userViewPaymentComponentController.getUserViewComponentController().getBorrowerLoanDetails().stream()
                .map(loanDetails -> loanDetails.getPaymentNotifications())
                .flatMap(List::stream).sorted((notification1, notification2) -> {
                    if (notification1.getPaymentTime() > notification2.getPaymentTime())
                        return -1;
                    else if (notification1.getPaymentTotal() < notification2.getPaymentTime())
                        return 1;
                    else return 0;
                }).collect(Collectors.toList())));
    }*/

    private void updatePaymentNotifications(PaymentNotificationsAndVersion paymentNotificationsAndVersion) {
        if (!ServerTimeUtil.isRewindMode()) {
            paymentNotificationsAndVersion.getPaymentNotifications().forEach(paymentNotification -> Platform.runLater(() -> paymentNotificationViewComponent.getItems().add(0, paymentNotification)));
            paymentNotificationsAndVersion.getPaymentNotifications().forEach(paymentNotification -> Platform.runLater(() -> Notifications.create().text(paymentNotification.toString()).showInformation()));
        }
        else {
            Platform.runLater(() -> paymentNotificationViewComponent.getItems().setAll(paymentNotificationsAndVersion.getPaymentNotifications()));
        }
    }

    public void startPaymentNotificationRefresher() {
        paymentNotificationsRefresher = new PaymentNotificationsRefresher(this::updatePaymentNotifications);
        timer = new Timer();
        timer.schedule(paymentNotificationsRefresher, 500, 500);
    }

    public void setUserViewPaymentController(UserViewPaymentController controller) {
        this.userViewPaymentComponentController = controller;
    }
}
