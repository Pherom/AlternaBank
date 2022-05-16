package com.alternabank.engine.transaction.event.listener;

import com.alternabank.engine.transaction.event.BilateralTransactionEvent;

import java.util.EventListener;

public interface BilateralTransactionListener extends EventListener {

    void bilateralTransactionExecuted(BilateralTransactionEvent event);

}
