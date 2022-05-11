package com.alternabank.console.ui.message.transaction;

import com.alternabank.console.ui.message.AbstractMessage;
import com.alternabank.engine.transaction.event.BilateralTransactionEvent;

public class BilateralTransactionMessage extends AbstractMessage {

    public BilateralTransactionMessage(BilateralTransactionEvent event) {
        super("Bilateral transaction executed:" + System.lineSeparator() + event.getRecord().toString());
    }

}
