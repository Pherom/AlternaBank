package com.alternabank.engine.loan.event.listener;

import com.alternabank.engine.loan.event.PaymentDueEvent;

import java.util.EventListener;

public interface PaymentDueListener extends EventListener {

    void paymentDue(PaymentDueEvent event);

}
