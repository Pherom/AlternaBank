package com.alternabank.ui.event.handler.transaction;

import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.engine.transaction.event.listener.BilateralTransactionListener;
import com.alternabank.ui.message.transaction.BilateralTransactionMessage;
import com.alternabank.ui.message.Message;

public class BilateralTransactionEventHandler implements BilateralTransactionListener {

    @Override
    public void bilateralTransactionExecuted(BilateralTransactionEvent event) {
        Message bilateralTransactionMessage = new BilateralTransactionMessage(event);
        bilateralTransactionMessage.display();
    }

}
