package com.alternabank.dto.customer;

import com.alternabank.dto.loan.notification.LoanStatusChangeNotification;

import java.util.ArrayList;
import java.util.List;

public class LoanStatusChangeNotificationsAndVersion {

    private final List<LoanStatusChangeNotification> loanStatusChangeNotifications;

    private final int version;

    public LoanStatusChangeNotificationsAndVersion(List<LoanStatusChangeNotification> loanStatusChangeNotifications, int version) {
        this.loanStatusChangeNotifications = new ArrayList<>(loanStatusChangeNotifications);
        this.version = version;
    }

    public List<LoanStatusChangeNotification> getLoanStatusChangeNotifications() {
        return loanStatusChangeNotifications;
    }

    public int getVersion() {
        return version;
    }
}
