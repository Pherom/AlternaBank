package com.alternabank.engine.transaction.event.listener;

import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;

import java.util.EventListener;

public interface UnilateralTransactionListener extends EventListener {

    void unilateralTransactionExecuted(UnilateralTransactionEvent event);

}
