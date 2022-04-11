package com.alternabank.ui.message.transaction;

import com.alternabank.engine.transaction.event.UnilateralTransactionEvent;
import com.alternabank.ui.message.AbstractMessage;

public class UnilateralTransactionMessage extends AbstractMessage {

    public UnilateralTransactionMessage(UnilateralTransactionEvent event) {
        super("Unilateral transaction executed:" + System.lineSeparator() + event.getRecord().toString());
    }

}
