package com.alternabank.dto.customer;

import com.alternabank.dto.loan.notification.PaymentNotification;

import java.util.ArrayList;
import java.util.List;

public class PaymentNotificationsAndVersion {

    private final List<PaymentNotification> paymentNotifications;

    private final int version;

    public PaymentNotificationsAndVersion(List<PaymentNotification> paymentNotifications, int version) {
        this.paymentNotifications = new ArrayList<>(paymentNotifications);
        this.version = version;
    }

    public List<PaymentNotification> getPaymentNotifications() {
        return paymentNotifications;
    }

    public int getVersion() {
        return version;
    }

}
