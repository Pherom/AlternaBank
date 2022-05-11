package com.alternabank.console.ui.form.error.xml;

import com.alternabank.engine.xml.event.XMLLoanLoadFailureEvent;
import com.alternabank.console.ui.form.error.AbstractErrorDialogue;

public class XMLLoanLoadErrorDialogue extends AbstractErrorDialogue {
    public XMLLoanLoadErrorDialogue(XMLLoanLoadFailureEvent event) {
        super(event.getErrorMessage(), "Skip? (Y/N) (Not skipping will result in load cancellation):");
    }
}
