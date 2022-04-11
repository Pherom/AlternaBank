package com.alternabank.ui.form.error.xml;

import com.alternabank.engine.xml.event.XMLCustomerLoadFailureEvent;
import com.alternabank.ui.form.error.AbstractErrorDialogue;

public class XMLCustomerLoadErrorDialogue extends AbstractErrorDialogue {
    public XMLCustomerLoadErrorDialogue(XMLCustomerLoadFailureEvent event) {
        super(event.getErrorMessage(), "Skip? (Y/N) (Not skipping will result in load cancellation):");
    }
}
