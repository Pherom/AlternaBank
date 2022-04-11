package com.alternabank.engine.transaction.event.listener;

import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;

public interface UnilateralTransactionListener {

    void unilateralTransactionExecuted(UnilateralTransactionEvent event);

}
