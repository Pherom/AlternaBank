package com.alternabank.console.ui.event.handler.transaction;


import com.alternabank.console.ui.message.Message;
import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.engine.transaction.event.listener.UnilateralTransactionListener;
import com.alternabank.console.ui.message.transaction.UnilateralTransactionMessage;

public class UnilateralTransactionEventHandler implements UnilateralTransactionListener {

    @Override
    public void unilateralTransactionExecuted(UnilateralTransactionEvent event) {
        Message unilateralTransactionMessage = new UnilateralTransactionMessage(event);
        unilateralTransactionMessage.display();
    }

}
