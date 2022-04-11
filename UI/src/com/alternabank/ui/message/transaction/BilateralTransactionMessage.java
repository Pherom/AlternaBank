package com.alternabank.ui.message.transaction;

import com.alternabank.engine.transaction.event.BilateralTransactionEvent;
import com.alternabank.ui.message.AbstractMessage;

public class BilateralTransactionMessage extends AbstractMessage {

    public BilateralTransactionMessage(BilateralTransactionEvent event) {
        super("Bilateral transaction executed:" + System.lineSeparator() + event.getRecord().toString());
    }

}
