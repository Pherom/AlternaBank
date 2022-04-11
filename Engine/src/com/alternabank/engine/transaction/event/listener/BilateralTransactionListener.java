package com.alternabank.engine.transaction.event.listener;

import com.alternabank.engine.transaction.event.BilateralTransactionEvent;

public interface BilateralTransactionListener {

    void bilateralTransactionExecuted(BilateralTransactionEvent event);

}
