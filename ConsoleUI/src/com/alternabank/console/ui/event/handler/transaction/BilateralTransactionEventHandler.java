package com.alternabank.console.ui.event.handler.transaction;

import com.alternabank.console.ui.message.Message;
import com.alternabank.console.ui.message.transaction.BilateralTransactionMessage;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;

public class BilateralTransactionEventHandler implements BilateralTransactionListener {

    @Override
    public void bilateralTransactionExecuted(BilateralTransactionEvent event) {
        Message bilateralTransactionMessage = new BilateralTransactionMessage(event);
        bilateralTransactionMessage.display();
    }

}
