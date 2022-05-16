package com.alternabank.engine.loan.event.listener;

import com.alternabank.engine.loan.event.LoanStatusUpdateEvent;

import java.util.EventListener;

public interface LoanStatusUpdateListener extends EventListener {

    void statusUpdated(LoanStatusUpdateEvent event);

}
