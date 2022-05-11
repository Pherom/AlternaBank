package com.alternabank.console.ui.message.transaction;

import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.console.ui.message.AbstractMessage;

public class UnilateralTransactionMessage extends AbstractMessage {

    public UnilateralTransactionMessage(UnilateralTransactionEvent event) {
        super("Unilateral transaction executed:" + System.lineSeparator() + event.getRecord().toString());
    }

}
